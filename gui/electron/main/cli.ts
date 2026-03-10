import { program } from "commander";

program
  .option('-p --path <path>', 'set launch path')
  .option("-s --steam [os]", 'steam mode')
  .option("-i --install [os]", 'run the driver installer')
  .option(
    '--skip-server-if-running',
    'gui will not launch the server if it is already running'
  )
  .allowUnknownOption();

console.log(process.argv)
program.parse(process.argv);
export const options = program.opts();
