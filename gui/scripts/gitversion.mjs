#!/usr/bin/env node
import { promisify } from 'node:util';
import { exec as execCallback } from 'node:child_process';
const exec = promisify(execCallback);

const [commitHash, versionTag, lastTag, gitClean] = await Promise.all([
  exec('git rev-parse --verify --short HEAD').then((res) =>
    res.stdout.trim().substring(1)
  ),
  exec('git --no-pager tag --sort -taggerdate --points-at HEAD').then((res) =>
    res.stdout.split('\n')[0].trim()
  ),
  exec('git describe --tags --abbrev=0').then((res) =>
    res.stdout.trim().substring(1)
  ),
  // If not empty then it's not clean
  exec('git status --porcelain').then((res) => (res.stdout ? false : true)),
]);

console.log(
  JSON.stringify({
    version: `${versionTag || `${lastTag}-${commitHash}`}${gitClean ? '' : '-dirty'}`,
  })
);
