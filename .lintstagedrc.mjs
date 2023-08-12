export default {
  'server/**/*.{java,kt}': (filenames) =>
    filenames.map(
      (filename) =>
        `./gradlew${
          process.platform === 'win32' ? '.bat' : ''
        } spotlessApply "-PspotlessIdeHook=${filename}"`
    ),
};
