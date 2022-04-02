module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}",],
  theme: {
    extend: {
      colors: {
        primary: {
          1: '#201527',
          2: '#26192E',
          3: '#2F2037',
          4: '#382740',
          5: '#432E4D'
        },
        misc: {
          1: '#50E897',
          2: '#FF486E',
          3: '#A44FED',
          4: '#D5C055'
        }
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}