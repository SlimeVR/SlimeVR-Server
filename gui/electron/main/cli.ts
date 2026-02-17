import { program } from "commander";

program
  .option('-p --path <path>', 'set launch path')
  .option(
    '--skip-server-if-running',
    'gui will not launch the server if it is already running'
  )
  .allowUnknownOption();

program.parse(process.argv);
export const options = program.opts();
