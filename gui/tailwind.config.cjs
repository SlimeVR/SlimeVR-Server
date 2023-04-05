const plugin = require('tailwindcss/plugin');

const rem = (pt) => `${pt / 16}rem`;

const colors = {
  'blue-gray': {
    100: '#ffffff',
    200: '#78A4C6',
    300: '#608AAB',
    400: '#3D6381',
    500: '#1A3D59',
    600: '#112D43',
    700: '#081E30',
    800: '#00101C',
    900: '#000509',
  },
  purple: {
    100: '#BB8AE5',
    200: '#9D5CD4',
    500: '#65459A',
    700: '#623B83',
    900: '#2E2145',
  },
};

module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    screens: {
      xs: '800px',
      sm: '900px',
      md: '1100px',
      lg: '1300px',
      xl: '1600px',
    },
    extend: {
      colors: {
        status: {
          success: '#50E897',
          warning: '#D8CD37',
          critical: '#DF6D8C',
          special: '#A44FED',
        },
        ...colors,
        background: Object.keys(colors['blue-gray']).reduce(
          (curr, colork, index) => ({
            ...curr,
            [(index + 1) * 10]: colors['blue-gray'][colork],
          }),
          {}
        ),
        'accent-background': Object.keys(colors.purple).reduce(
          (curr, colork, index) => ({
            ...curr,
            [(index + 1) * 10]: colors.purple[colork],
          }),
          {}
        ),
      },
      fontSize: {
        DEFAULT: rem(12),
      },
      fontWeight: {
        DEFAULT: 500,
      },
      color: {
        DEFAULT: '#ffffff',
      },
      keyframes: {
        bounce: {
          '0%, 100%': {
            transform: 'translateY(0)',
            'animation-timing-function': 'cubic-bezier(0, 0, 0.2, 1)',
          },
          '50%': {
            transform: 'translateY(-25%)',
            'animation-timing-function': 'cubic-bezier(0.8, 0, 1, 1)',
          },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('tailwind-gradient-mask-image'),
    plugin(function ({ addUtilities, theme }) {
      const textConfig = (fontSize, fontWeight) => ({
        fontSize,
        fontWeight,
      });

      addUtilities({
        '.text-main-title': textConfig(rem(25), 700),
        '.text-section-title': textConfig(rem(14), 700),
        '.text-standard': textConfig(rem(12), 500),
        '.text-vr-accesible': textConfig(rem(14), 500),
        '.text-vr-accesible-bold': textConfig(rem(14), 700),
        '.text-standard-bold': textConfig(rem(12), 700),
      });
    }),
  ],
};
