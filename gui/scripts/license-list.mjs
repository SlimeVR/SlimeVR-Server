#!/usr/bin/env node
/* eslint-env node */
import { promisify } from 'node:util';
import { exec as execCallback } from 'node:child_process';
import satisfies from 'spdx-satisfies';
const exec = promisify(execCallback);

const ACCEPTED_KEYS = 'MIT OR ISC OR Apache-2.0 OR BSD-3-Clause OR 0BSD OR OFL-1.1';

const licenses = await exec('pnpm licenses ls -P --json').then((res) =>
  JSON.parse(res.stdout)
);

if (licenses['Unknown'].find((p) => p.name !== 'flatbuffers')) {
  console.error(
    'Found more than one library with unknown license: ' +
      licenses['Unknown'].map((p) => p.name).join()
  );
  process.exit(1);
}

if (licenses['BSD'].find((p) => p.name !== 'css-mediaquery')) {
  console.error(
    'Found more than one library with invalid SPDX BSD license: ' +
      licenses['BSD'].map((p) => p.name).join()
  );
  process.exit(1);
}

for(let [license, pkgs] of Object.entries(licenses)) {
  if(license === 'BSD' || license === 'Unknown') continue;
  if(license === 'MIT or APACHE-2.0') {
    license = 'MIT OR Apache-2.0';
  }
  if(!satisfies(license, ACCEPTED_KEYS)) {
    console.error(`Found more than one library with incompatible license ${license}: ${pkgs.map((p) => p.name).join()}`)
    process.exit(1);
  }
}
console.log(Object.keys(licenses));
