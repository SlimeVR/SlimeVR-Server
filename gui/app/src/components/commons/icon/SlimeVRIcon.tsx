import { useConfig } from '@/hooks/config';

export function SlimeVRIcon({ width = 28 }: { width?: number }) {
  const { config } = useConfig();
  if (config?.theme == 'snep') {
    return (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fillRule="evenodd"
        strokeMiterlimit="10"
        clipRule="evenodd"
        width={width}
        viewBox="0 0 380 380"
      >
        <g fill="none" stroke="#fff">
          <path strokeWidth="13.62" d="m 58.065408,191.74 37,-39 39,36" />
          <path strokeWidth="13.62" d="m 194.06861,187.74 38,-35 36,38" />
          <path
            strokeLinecap="square"
            strokeWidth="17"
            d="m 264.21323,100.54097 c 36.55564,-13.927358 80.48248,-20.252638 96.44182,-0.16058 15.95933,20.09207 -5.55378,62.57663 -18.85775,71.31398 -13.30397,8.73734 -24.9251,23.65102 11.38415,55.001 -41.88653,1.00415 -20.70613,38.05812 4.23915,51.07844"
          />
          <path
            strokeLinecap="round"
            strokeWidth="17"
            d="m 178.71549,220.85825 c -11.18717,2.62658 -20.63024,3.18933 -31.30189,0.37013 7.7283,1.04116 13.35686,4.67519 16.14313,9.6455 1.97058,-5.04296 8.30663,-8.85748 15.15876,-10.01593 z"
          />
          <path
            strokeLinecap="square"
            strokeWidth="17"
            d="m 114.0349,266.90992 c 14.41809,-4.43279 38.26495,-10.17404 49.29422,-23.81979 10.73948,13.35362 31.14902,18.81171 48.74742,23.621"
          />
        </g>
      </svg>
    );
  }
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      fillRule="evenodd"
      strokeMiterlimit="10"
      clipRule="evenodd"
      width={width}
      viewBox="0 0 380 380"
    >
      <g fill="none" stroke="#fff">
        <path strokeWidth="13.62" d="M72.867 191.74l37-39 39 36" />
        <path strokeWidth="13.62" d="M208.87 187.74l38-35 36 38" />
        <path
          strokeLinecap="square"
          strokeWidth="17"
          d="M56.867 253.74s130.61-31.182 248 5c13.45 4.146 20.244 2.975 20-8s1.909-126.06-46-131"
        />
      </g>
    </svg>
  );
}
