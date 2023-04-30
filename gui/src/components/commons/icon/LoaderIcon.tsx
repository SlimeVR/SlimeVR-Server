export enum SlimeState {
  HAPPY,
  SAD,
  JUMPY,
  CURIOUS,
}

export function LoaderIcon({
  slimeState = SlimeState.HAPPY,
}: {
  slimeState: SlimeState;
}) {
  return (
    <>
      <img
        hidden={slimeState !== SlimeState.JUMPY}
        src="/images/jumping-slime.gif"
        alt="Slime jumping"
        width="85"
        className="crisp-edges"
      ></img>
      <img
        hidden={slimeState !== SlimeState.HAPPY}
        src="/images/happy-slime.gif"
        alt="Happy slime"
        width="85"
        className="crisp-edges"
      ></img>
      <img
        hidden={slimeState !== SlimeState.SAD}
        src="/images/sad-slime.gif"
        alt="Sad slime"
        width="85"
        className="crisp-edges"
      ></img>
      <img
        hidden={slimeState !== SlimeState.CURIOUS}
        src="/images/curious-slime.gif"
        alt="Slime looking for something"
        width="85"
        className="crisp-edges"
      ></img>
    </>
  );
}
