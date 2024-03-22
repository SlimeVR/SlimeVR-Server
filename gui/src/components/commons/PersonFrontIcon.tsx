import { BodyPart } from 'solarxr-protocol';

export const SIDES = [
  {
    shoulder: BodyPart.LEFT_SHOULDER,
    upperArm: BodyPart.LEFT_UPPER_ARM,
    lowerArm: BodyPart.LEFT_LOWER_ARM,
    hand: BodyPart.LEFT_HAND,
    upperLeg: BodyPart.LEFT_UPPER_LEG,
    lowerLeg: BodyPart.LEFT_LOWER_LEG,
    foot: BodyPart.LEFT_FOOT,
  },
  {
    shoulder: BodyPart.RIGHT_SHOULDER,
    upperArm: BodyPart.RIGHT_UPPER_ARM,
    lowerArm: BodyPart.RIGHT_LOWER_ARM,
    hand: BodyPart.RIGHT_HAND,
    upperLeg: BodyPart.RIGHT_UPPER_LEG,
    lowerLeg: BodyPart.RIGHT_LOWER_LEG,
    foot: BodyPart.RIGHT_FOOT,
  },
];

export function PersonFrontIcon({
  width,
  mirror = true,
}: {
  width?: number;
  mirror?: boolean;
}) {
  const CIRCLE_RADIUS = 0.0001;
  const left = +!mirror;
  const right = +mirror;

  return (
    <svg
      width={width || 240}
      viewBox="0 0 165 392"
      xmlns="http://www.w3.org/2000/svg"
    >
      <image
        height={'105%'}
        x="8.5%"
        href="/images/front-standing-pose.webp"
      ></image>
      {/* <path d="M84.53 224.074C83.953 230.874 88.569 266.874 90.951 280.984C92.085 287.671 95.195 298.565 94.076 304.349C92.476 312.411 92.017 322.843 92.896 328.918C93.451 332.607 95.196 349.618 92.696 355.845C91.389 359.108 88.996 375.832 88.996 375.832C82.756 391.587 86.278 390.812 86.278 390.812C88.21 393.183 91.519 390.998 91.519 390.998C92.1549 391.464 92.9388 391.682 93.7241 391.612C94.5094 391.542 95.2421 391.188 95.785 390.616C97.949 392.407 100.471 390.396 100.471 390.396C103.189 391.807 105.71 389.205 105.71 389.205C107.271 389.991 107.653 388.998 107.653 388.998C112.337 388.698 105.039 373.706 105.039 373.706C103.291 360.242 106.773 352.748 106.773 352.748C118.178 318.926 118.758 309.948 114.199 297.204C112.915 293.524 112.59 292.067 113.181 290.47C114.547 286.783 113.551 271.953 115.217 266.064C118.431 254.706 121.602 225.903 123.254 212.464C125.475 194.364 115.388 170.088 115.388 170.088C113.179 160.21 116.418 125.016 116.418 125.016C120.941 132.054 120.768 144.477 120.768 144.477C120.05 157.506 131.294 177.42 131.294 177.42C136.694 185.649 138.742 193.456 138.742 194.036C138.742 196.407 138.223 202.145 138.223 202.145L138.43 207.145C138.803 209.721 139.034 212.316 139.123 214.918C138.28 227.953 140.35 225.501 140.35 225.501C142.098 225.501 144.018 215.011 144.018 215.011C144.018 217.711 143.357 225.811 144.818 228.869C146.564 232.512 147.848 228.244 147.871 227.387C148.333 210.787 149.33 215.138 149.33 215.138C150.301 228.602 151.494 231.644 153.63 230.591C155.25 229.818 153.769 214.433 153.769 214.433C156.544 223.572 158.649 225.027 158.649 225.027C163.229 228.243 160.397 219.361 159.76 217.602C156.371 208.256 156.267 205.017 156.267 205.017C160.501 213.417 163.692 213.104 163.692 213.104C167.822 211.786 160.083 199.894 155.548 194.197C153.234 191.297 150.248 187.408 149.384 185.097C147.973 181.188 146.907 168.62 146.907 168.62C146.48 153.79 142.813 147.348 142.813 147.348C136.544 137.314 135.365 118.598 135.365 118.598L135.09 87C132.89 65.445 117.01 65.29 117.01 65.29C100.957 62.9 98.723 57.714 98.723 57.714C95.323 52.821 97.266 43.44 97.266 43.44C100.087 41.145 101.175 35.053 101.175 35.053C105.859 31.461 105.63 26.205 103.466 26.262C101.73 26.308 102.123 24.87 102.123 24.87C105.052 1.208 84.046 0 84.046 0H80.836C80.836 0 59.821 1.208 62.746 24.864C62.746 24.864 63.139 26.304 61.388 26.256C59.23 26.199 59.029 31.456 63.696 35.047C63.696 35.047 64.783 41.137 67.605 43.434C67.605 43.434 69.548 52.814 66.148 57.708C66.148 57.708 63.922 62.894 47.861 65.284C47.861 65.284 31.952 65.44 29.788 86.994L29.488 118.594C29.488 118.594 28.331 137.311 22.038 147.344C22.038 147.344 18.389 153.787 17.967 168.616C17.967 168.616 16.898 181.184 15.492 185.093C14.635 187.393 11.653 191.276 9.32001 194.193C4.74601 199.878 -2.94199 211.745 1.17101 213.1C1.17101 213.1 4.37901 213.412 8.59601 205.013C8.59601 205.013 8.50901 208.229 5.12501 217.598C4.46001 219.334 1.63201 228.217 6.21301 225.024C6.21301 225.024 8.33501 223.567 11.093 214.43C11.093 214.43 9.61301 229.815 11.26 230.588C13.412 231.642 14.586 228.599 15.56 215.135C15.56 215.135 16.56 210.787 17.017 227.384C17.04 228.241 18.295 232.509 20.049 228.866C21.529 225.811 20.864 217.727 20.864 215.008C20.864 215.008 22.764 225.498 24.536 225.498C24.536 225.498 26.624 227.95 25.767 214.915C25.628 212.786 26.375 208.415 26.467 207.142L26.667 202.142C26.667 202.142 26.146 196.417 26.146 194.033C26.146 193.442 28.194 185.646 33.594 177.417C33.594 177.417 44.826 157.494 44.103 144.474C44.103 144.474 43.947 132.051 48.47 125.013C48.47 125.013 51.68 160.205 49.505 170.085C49.505 170.085 39.405 194.358 41.629 212.461C43.27 225.937 46.435 254.702 49.657 266.061C51.34 271.938 50.345 286.761 51.693 290.467C52.301 292.076 51.982 293.558 50.675 297.201C46.141 309.947 46.718 318.925 58.123 352.745C58.123 352.745 61.633 360.239 59.859 373.703C59.859 373.703 52.572 388.695 57.239 388.995C57.239 388.995 57.604 389.988 59.182 389.202C59.182 389.202 61.703 391.802 64.427 390.393C64.427 390.393 66.95 392.407 69.106 390.613C69.6451 391.185 70.3751 391.54 71.158 391.61C71.9409 391.681 72.7225 391.462 73.355 390.995C73.355 390.995 76.664 393.227 78.63 390.809C78.63 390.809 82.123 391.584 75.904 375.829C75.904 375.829 73.522 359.129 72.209 355.842C69.709 349.621 71.474 332.57 72.009 328.915C72.87 322.806 72.409 312.398 70.835 304.346C69.684 298.575 72.801 287.679 73.952 280.981C76.317 266.881 80.952 230.881 80.373 224.071L82.288 224.743C83.0863 224.756 83.8692 224.522 84.53 224.074Z" /> */}
      <circle
        className="body-part-circle"
        cx="82"
        cy="114"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.UPPER_CHEST]}
      />
      <circle
        className="body-part-circle"
        cx="82"
        cy="130"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.CHEST]}
      />
      <circle
        className="body-part-circle"
        cx="82"
        cy="191"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.HIP]}
      />
      <circle
        className="body-part-circle"
        cx="82"
        cy="165"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.WAIST]}
      />
      <circle
        className="body-part-circle"
        cx="81.5"
        cy="91"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.NECK]}
      />
      <circle
        className="body-part-circle"
        cx="81.5"
        cy="50"
        r={CIRCLE_RADIUS}
        id={BodyPart[BodyPart.HEAD]}
      />
      <circle
        className="body-part-circle"
        cx="128"
        cy="218"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].hand]}
      />
      <circle
        className="body-part-circle"
        cx="115"
        cy="140"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].upperArm]}
      />
      <circle
        className="body-part-circle"
        cx="105"
        cy="105"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].shoulder]}
      />
      <circle
        className="body-part-circle"
        cx="125"
        cy="194"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].lowerArm]}
      />
      <circle
        className="body-part-circle"
        cx="97.004"
        cy="360"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].lowerLeg]}
      />
      <circle
        className="body-part-circle"
        cx="97"
        cy="250"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].upperLeg]}
      />
      <circle
        className="body-part-circle"
        cx="97.004"
        cy="380"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[right].foot]}
      />

      <circle
        className="body-part-circle"
        cx="36"
        cy="218"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].hand]}
      />

      <circle
        className="body-part-circle"
        cx="50"
        cy="140"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].upperArm]}
      />
      <circle
        className="body-part-circle"
        cx="58"
        cy="105"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].shoulder]}
      />
      <circle
        className="body-part-circle"
        cx="39"
        cy="194"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].lowerArm]}
      />
      <circle
        className="body-part-circle"
        cx="67.004"
        cy="360"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].lowerLeg]}
      />

      <circle
        className="body-part-circle"
        cx="67"
        cy="250"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].upperLeg]}
      />
      <circle
        className="body-part-circle"
        cx="67.004"
        cy="380"
        r={CIRCLE_RADIUS}
        id={BodyPart[SIDES[left].foot]}
      />
    </svg>
  );
}
