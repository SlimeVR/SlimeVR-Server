#!/usr/bin/env node
import { promisify } from 'node:util';
import { exec as execCallback } from 'node:child_process';
const exec = promisify(execCallback);

const licenses = await exec("pnpm licenses ls -P --json").then(res => JSON.parse(res.stdout));

if(licenses["Unknown"].find(p => p.name !== "flatbuffers")) {
    console.error("Found more than one library with unknown license: " + licenses["Unknown"].map(p => p.name).join());
    process.exit(1);
}

console.log(Object.keys(licenses));
