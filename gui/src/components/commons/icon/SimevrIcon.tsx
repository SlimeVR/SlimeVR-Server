import { useConfig } from '@/hooks/config';

export function SlimeVRIcon({ drag }: { drag?: boolean }) {
  const { config } = useConfig();
  if (config?.theme == 'snep') {
    return (
      <svg
        width="49"
        height="29"
        viewBox="-4 -2 49 33"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        data-tauri-drag-region={drag}
      >
        <path
          d="m 1.6647024,15.257308 4.84329,-5.8061114 5.1394996,4.7526114"
          stroke="#FFCCE5"
          strokeWidth="3"
          strokeLinecap="round"
        />
        <path
          d="m 22.099692,14.390108 5.7806,-4.8728814 4.2323,5.5751814"
          stroke="#FFCCE5"
          strokeWidth="3"
          strokeLinecap="round"
        />
        <path
          d="m 9.7241618,27.517333 c 2.9071362,-0.836166 5.2501762,-1.583484 7.0857782,-3.854543 1.787374,2.222439 3.963276,3.063619 7.087706,3.839132"
          stroke="#FFCCE5"
          strokeWidth="3"
          strokeLinecap="round"
        />
        <path
          d="m 19.337465,19.962745 c -1.861871,0.437141 -3.433485,0.530797 -5.209565,0.06165 1.286223,0.173275 2.222982,0.778091 2.686704,1.605299 0.327959,-0.839305 1.382466,-1.47415 2.522861,-1.666949 z"
          stroke="#FFCCE5"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="m 35.942918,2.6356084 c 6.330566,-1.5164535 11.583704,-1.69795947 15.609729,0.9503118 2.180495,1.4343036 1.678869,4.6673575 0.754839,5.9005803 -2.596688,3.4655715 -9.485458,7.3237605 -5.116612,11.0623905 -4.998324,0.352073 -3.13787,5.686673 1.260384,6.928864"
          stroke="#FFCCE5"
          strokeWidth="3"
          strokeLinecap="round"
        />
      </svg>
    );
  }
  return (
    <svg
      width="49"
      height="29"
      viewBox="0 0 49 29"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      data-tauri-drag-region={drag}
    >
      <path
        d="M2 26.996C10.44 25.59 29.16 23.1571 46.509 26.9091C46.509 26.9091 48.89 -0.199966 35.761 2.14503"
        stroke="#A44FED"
        strokeWidth="3"
        strokeLinecap="round"
      />
      <path
        d="M7.52161 15.0107L12.3649 9.20459L17.5044 13.9572"
        stroke="#A44FED"
        strokeWidth="3.00157"
        strokeLinecap="round"
      />
      <path
        d="M27.9566 14.1435L33.7372 9.27062L37.9695 14.8458"
        stroke="#A44FED"
        strokeWidth="3.00136"
        strokeLinecap="round"
      />
    </svg>
  );
}
