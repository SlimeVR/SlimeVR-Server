// lib holds services and reactive state, anything that renders lives in ui
const noJsx = {
  meta: {
    type: 'problem',
    messages: { jsx: 'No JSX in lib, components belong in ui.' },
    schema: [],
  },
  create(context) {
    const report = (node) => context.report({ node, messageId: 'jsx' });
    return { JSXElement: report, JSXFragment: report };
  },
};

export default { rules: { 'no-jsx': noJsx } };
