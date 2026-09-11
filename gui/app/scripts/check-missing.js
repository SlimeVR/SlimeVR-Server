#!/usr/bin/env node
/*
  Process exit codes:
  First bit: Fluent syntax error in english file
  Second bit: Fluent syntax error in translation file
  Third bit: Missing key
  Fourth bit: Missing attribute
  Fifth bit: Extra key
  Sixth bit: Extra attribute
*/

// eslint-disable-next-line @typescript-eslint/no-var-requires
const fs = require('fs');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const path = require('path');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const process = require('process');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const { execSync } = require('child_process');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const { FluentBundle, FluentResource } = require('@fluent/bundle');

const PATH = path.join(
  execSync('git rev-parse --show-toplevel', { encoding: 'utf-8' }).trim(),
  'gui/public/i18n'
);

const langs = fs.readdirSync(PATH).filter((x) => x !== 'en');
const en = new FluentBundle('en');
const enErrors = en.addResource(
  new FluentResource(fs.readFileSync(path.join(PATH, 'en/translation.ftl'), 'utf-8'))
);
if (enErrors.length) {
  for (const error of enErrors) {
    console.error(error);
  }
  process.exit(1);
}
const requiredMessages = [...en._messages.keys()];

process.exitCode = 0;
for (const lang of langs) {
  const resource = new FluentResource(
    fs.readFileSync(path.join(PATH, lang, 'translation.ftl'), 'utf-8')
  );
  const bundle = new FluentBundle(lang);
  const errors = bundle.addResource(resource);

  // Check for syntax errors
  for (const error of errors) {
    console.error(error);
    process.exitCode &= 0b10;
  }
  if (errors.length) process.exit();

  // Check for missing
  for (const msg of requiredMessages) {
    if (!bundle._messages.has(msg)) {
      console.log(`missing key in ${lang}: ${msg}`);
      process.exitCode &= 0b100;
      continue;
    }

    const data = en._messages.get(msg);
    const localAttributes = Object.keys(bundle._messages.get(msg).attributes);
    const missing = Object.keys(data.attributes).filter(
      (x) => !localAttributes.some((y) => x === y)
    );

    if (missing.length) {
      console.log(`missing attributes in ${lang} of ${msg}: [${missing}]`);
      process.exitCode &= 0b1000;
    }
  }

  // Check for extra
  for (const msg of bundle._messages.keys()) {
    if (!en._messages.has(msg)) {
      console.log(`extra key in ${lang}: ${msg}`);
      process.exitCode &= 0b10000;
      continue;
    }

    const data = bundle._messages.get(msg);
    const localAttributes = Object.keys(en._messages.get(msg).attributes);
    const missing = Object.keys(data.attributes).filter(
      (x) => !localAttributes.some((y) => x === y)
    );

    if (missing.length) {
      console.log(`extra attributes in ${lang} of ${msg}: [${missing}]`);
      process.exitCode &= 0b100000;
    }
  }
}
