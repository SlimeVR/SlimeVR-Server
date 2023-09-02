export default {
  'server/**/*.{java,kt,kts}': (filenames) =>
    filenames.map(
      (filename) =>
        `./gradlew${
          process.platform === 'win32' ? '.bat' : ''
        } spotlessApply "-PspotlessIdeHook=${filename}"`
    ),
};
