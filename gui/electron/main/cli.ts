import { Option, program } from "commander";

const noUdevOption = new Option('--no-udev', 'disable udev warning');
noUdevOption.negate = false;

program
  .option('-p, --path <path>', 'set launch path')
  .option('-s, --steam [os]', 'steam mode')
  .option('-i, --install [os]', 'run the driver installer')
  .addOption(noUdevOption)
  .option(
    '--skip-server-if-running',
    'gui will not launch the server if it is already running'
  )
  .allowUnknownOption();

console.log(process.argv)
program.parse(process.argv);
export const options = program.opts();
