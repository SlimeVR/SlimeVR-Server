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
  'trans-blue': {
    100: '#4D222B', // Dark text
    200: '#1A6682', // Some lighter-ish text
    300: '#095470', // Light-ish text
    400: '#4F9FBD', // Button hover in some places
    500: '#EEEEEE', // Darker trans white tracker list background
    600: '#FFFFFF', // Trans white tracker list background and some buttons
    700: '#F5A9B8', // Trans pink home background
    800: '#5BCEFA', // Trans blue trans outer background
    900: '#000509',
  },
  'trans-pink': {
    100: '#B53A52', // Accent text
    200: '#FCCAD4', // Lighter trans pink button
    300: '#F7B7C4', // Somewhat lighter trans pink button
    400: '#F7B7C4', // Somewhat lighter trans pink button
    500: '#F5A9B8', // Trans pink buttons
    700: '#FA91A6', // Darker trans pink button
    900: '#F77C94', // Even darker trans pink home bottom
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
          success: 'rgb(var(--success), <alpha-value>)',
          warning: 'rgb(var(--warning), <alpha-value>)',
          critical: 'rgb(var(--critical), <alpha-value>)',
          special: 'rgb(var(--special), <alpha-value>)',
        },
        window: {
          icon: 'rgb(var(--window-icon-stroke), <alpha-value>)',
        },
        ...colors,
        background: {
          10: 'rgb(var(--background-10), <alpha-value>)',
          20: 'rgb(var(--background-20), <alpha-value>)',
          30: 'rgb(var(--background-30), <alpha-value>)',
          40: 'rgb(var(--background-40), <alpha-value>)',
          50: 'rgb(var(--background-50), <alpha-value>)',
          60: 'rgb(var(--background-60), <alpha-value>)',
          70: 'rgb(var(--background-70), <alpha-value>)',
          80: 'rgb(var(--background-80), <alpha-value>)',
          90: 'rgb(var(--background-90), <alpha-value>)',
        },
        'accent-background': {
          10: 'rgb(var(--accent-background-10), <alpha-value>)',
          20: 'rgb(var(--accent-background-20), <alpha-value>)',
          30: 'rgb(var(--accent-background-30), <alpha-value>)',
          40: 'rgb(var(--accent-background-40), <alpha-value>)',
          50: 'rgb(var(--accent-background-50), <alpha-value>)',
        },
      },
      fontSize: {
        DEFAULT: rem(12),
      },
      fontWeight: {
        DEFAULT: 500,
      },
      color: {
        DEFAULT: 'rgb(var(--default-color), <alpha-value>)',
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
      backgroundImage: {
        'trans-flag': `linear-gradient(135deg, ${colors['trans-blue'][800]} 40%, ${colors['trans-blue'][700]} 40% 70%, ${colors['trans-blue'][600]} 70% 100%)`,
        slime: `linear-gradient(135deg, ${colors.purple[100]} 50%, ${colors['blue-gray'][700]} 50% 100%)`,
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('tailwind-gradient-mask-image'),
    plugin(function ({ addUtilities }) {
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
