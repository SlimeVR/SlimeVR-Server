// Checks Fluent ids against the en-US translation.ftl: literal ids passed to `t`, `t.attr`,
// the `id` prop of l10n components, and string values of objects that
// `satisfies Record<..., MessageId>`. Ids must be full literals, args must match the message.
import { parse } from '@fluent/syntax';
import { readFileSync, statSync } from 'node:fs';
import { fileURLToPath } from 'node:url';

const FTL_FILE = fileURLToPath(new URL('../../app/public/i18n/en/translation.ftl', import.meta.url));
const COMPONENTS = new Set(['Localized']);

let cache = { mtime: -1, messages: new Map() };

function collect(node, out) {
  if (!node || typeof node !== 'object') return;
  if (Array.isArray(node)) {
    for (const child of node) collect(child, out);
    return;
  }
  if (node.type === 'VariableReference') out.vars.add(node.id.name);
  else if (node.type === 'MessageReference') {
    out.refs.push({ id: node.id.name, attr: node.attribute?.name ?? null });
  }
  for (const [key, value] of Object.entries(node)) {
    if (key !== 'span' && typeof value === 'object') collect(value, out);
  }
}

function patternInfo(pattern) {
  const out = { vars: new Set(), refs: [] };
  collect(pattern, out);
  return out;
}

function loadMessages() {
  const mtime = statSync(FTL_FILE).mtimeMs;
  if (mtime === cache.mtime) return cache.messages;
  const messages = new Map();
  for (const entry of parse(readFileSync(FTL_FILE, 'utf8'), { withSpans: false }).body) {
    if (entry.type !== 'Message') continue;
    messages.set(entry.id.name, {
      value: entry.value ? patternInfo(entry.value) : null,
      attrs: new Map(entry.attributes.map((a) => [a.id.name, patternInfo(a.value)])),
    });
  }
  cache = { mtime, messages };
  return messages;
}

function requiredVars(messages, id, attr, seen = new Set()) {
  const key = `${id}.${attr ?? ''}`;
  const message = messages.get(id);
  const info = attr === null ? message?.value : message?.attrs.get(attr);
  if (!info || seen.has(key)) return new Set();
  seen.add(key);
  const vars = new Set(info.vars);
  for (const ref of info.refs) {
    for (const v of requiredVars(messages, ref.id, ref.attr, seen)) vars.add(v);
  }
  return vars;
}

function literalIds(node, context) {
  if (node.type === 'Literal' && typeof node.value === 'string') return [node.value];
  if (node.type === 'TemplateLiteral') {
    if (node.expressions.length === 0) return [node.quasis[0].value.cooked];
    context.report({ node, messageId: 'dynamic' });
    return null;
  }
  if (node.type === 'ConditionalExpression') {
    const a = literalIds(node.consequent, context);
    const b = literalIds(node.alternate, context);
    return a && b ? [...a, ...b] : null;
  }
  return null;
}

function propName(prop) {
  if (prop.computed) return null;
  if (prop.key.type === 'Identifier') return prop.key.name;
  if (prop.key.type === 'Literal') return String(prop.key.value);
  return null;
}

const messageId = {
  meta: {
    type: 'problem',
    docs: { description: 'Fluent ids exist in en-US and get the args they need' },
    schema: [],
    messages: {
      dynamic: 'Fluent ids must be full literals so they stay greppable.',
      unknown: 'Unknown Fluent id "{{id}}" (not in en-US translation.ftl).',
      noValue: 'Fluent message "{{id}}" has no value, only attributes.',
      unknownAttr: 'Fluent message "{{id}}" has no attribute "{{attr}}".',
      missingArgs: 'Fluent message "{{id}}" needs args: {{vars}}.',
      unknownArg: 'Fluent message "{{id}}" does not use arg "{{arg}}".',
    },
  },
  create(context) {
    const messages = loadMessages();

    function checkArgs(node, id, attr, argsNode) {
      const vars = requiredVars(messages, id, attr);
      if (!argsNode) {
        if (vars.size) context.report({ node, messageId: 'missingArgs', data: { id, vars: [...vars].join(', ') } });
        return;
      }
      if (argsNode.type !== 'ObjectExpression') return;
      if (argsNode.properties.some((p) => p.type !== 'Property' || propName(p) === null)) return;
      const given = new Set(argsNode.properties.map(propName));
      const missing = [...vars].filter((v) => !given.has(v));
      if (missing.length) {
        context.report({ node: argsNode, messageId: 'missingArgs', data: { id, vars: missing.join(', ') } });
      }
      for (const prop of argsNode.properties) {
        const arg = propName(prop);
        if (!vars.has(arg)) context.report({ node: prop, messageId: 'unknownArg', data: { id, arg } });
      }
    }

    function check(idNode, { attr, argsNode, withArgs }) {
      const ids = literalIds(idNode, context);
      if (!ids) return;
      for (const id of ids) {
        const message = messages.get(id);
        if (!message) {
          context.report({ node: idNode, messageId: 'unknown', data: { id } });
          continue;
        }
        if (attr === undefined && !message.value) {
          context.report({ node: idNode, messageId: 'noValue', data: { id } });
          continue;
        }
        if (typeof attr === 'string' && !message.attrs.has(attr)) {
          context.report({ node: idNode, messageId: 'unknownAttr', data: { id, attr } });
          continue;
        }
        if (withArgs && attr !== null) checkArgs(idNode, id, attr ?? null, argsNode);
      }
    }

    return {
      CallExpression(node) {
        const { callee } = node;
        const [first, second, third] = node.arguments;
        if (!first) return;
        if (callee.type === 'Identifier' && callee.name === 't') {
          check(first, { attr: undefined, argsNode: second, withArgs: true });
        } else if (
          callee.type === 'MemberExpression' &&
          callee.object.type === 'Identifier' &&
          callee.object.name === 't' &&
          callee.property.type === 'Identifier' &&
          callee.property.name === 'attr' &&
          second
        ) {
          const attr = second.type === 'Literal' && typeof second.value === 'string' ? second.value : null;
          check(first, { attr, argsNode: third, withArgs: true });
        }
      },
      JSXOpeningElement(node) {
        if (node.name.type !== 'JSXIdentifier' || !COMPONENTS.has(node.name.name)) return;
        const attrs = node.attributes.filter((a) => a.type === 'JSXAttribute');
        const idAttr = attrs.find((a) => a.name.name === 'id');
        if (!idAttr?.value) return;
        const idNode = idAttr.value.type === 'JSXExpressionContainer' ? idAttr.value.expression : idAttr.value;
        const argsAttr = attrs.find((a) => a.name.name === 'args');
        // Spread props may carry args, skip the args check then
        const withArgs = !node.attributes.some((a) => a.type === 'JSXSpreadAttribute');
        const argsNode = argsAttr?.value?.type === 'JSXExpressionContainer' ? argsAttr.value.expression : undefined;
        check(idNode, { attr: undefined, argsNode, withArgs });
      },
      TSSatisfiesExpression(node) {
        const type = context.sourceCode.getText(node.typeAnnotation);
        const object = node.expression.type === 'TSAsExpression' ? node.expression.expression : node.expression;
        if (!/\bMessageId\b/.test(type) || object.type !== 'ObjectExpression') return;
        for (const prop of object.properties) {
          if (prop.type === 'Property') check(prop.value, { attr: undefined, withArgs: false });
        }
      },
    };
  },
};

export default { rules: { 'message-id': messageId } };
