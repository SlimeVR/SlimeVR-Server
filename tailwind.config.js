const plugin = require('tailwindcss/plugin')

const rem = (px) => `${(px / 12).toFixed(4)}rem`

module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}",],
  theme: {
    extend: {
      colors: {
        'accent': {
          darker: '#831ECC',
          lighter: '#C06FFB'
        },
        'status': {
          online: '#9AFF76',
          warning: '#FFB257',
          error: '#FF6464'
        },
        'purple-gray': {
          900: "#160B1D",
          800: "#261730",
          700: "#3F2A4F",
          600: "#593E6C",
          500: "#6E5084",
          400: "#8E6BA7",
          300: "#C0A1D8",
          200: "#EFE2F9",
          100: "#FFFFFF"
        },
      },
      fontSize: {
        DEFAULT: rem(12),
      },
      fontWeight: {
        DEFAULT: 400,
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    plugin(function({ addUtilities, theme }) {

      const textConfig = (fontSize, fontWeight, color) => ({
        color,
        fontSize,
        fontWeight
      })

      addUtilities({
        '.text-heading': textConfig(rem(35), 700, theme('colors.purple-gray.100')),
        '.text-secondary-heading': textConfig(rem(25), 700, theme('colors.purple-gray.100')),
        '.text-field-title': textConfig(rem(12), 700, theme('colors.purple-gray.100')),
        '.text-extra-emphasised': textConfig(rem(12), 600, theme('colors.purple-gray.100')),
        '.text-emphasised': textConfig(rem(12), 400, theme('colors.purple-gray.100')),
        '.text-default': textConfig(rem(12), 400, theme('colors.purple-gray.300')),
        '.text-section-indicator': textConfig(rem(12), 700, theme('colors.purple-gray.400'))
      })
    })
  ],
}