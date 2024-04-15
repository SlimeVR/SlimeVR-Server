#!/usr/bin/env node
import { promisify } from 'node:util';
import { exec as execCallback } from 'node:child_process';
const exec = promisify(execCallback);

const [commitHash, versionTag, gitClean] = await Promise.all([
  exec('git rev-parse --verify --short HEAD').then((res) =>
    res.stdout.toString().trim()
  ),
  exec('git --no-pager tag --sort -taggerdate --points-at HEAD').then((res) =>
    res.stdout.toString().split('\n')[0].trim()
  ),
  // If not empty then it's not clean
  exec('git status --porcelain').then((res) =>
    res.stdout.toString() ? false : true
  ),
]);

console.log(
  JSON.stringify({
    version: `${versionTag || commitHash}${gitClean ? '' : '-dirty'}`,
  })
);
