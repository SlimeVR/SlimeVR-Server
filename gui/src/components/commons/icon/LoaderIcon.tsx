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
        alt="Slime Jumping"
        width="85"
        className="crisp-edges"
      ></img>
      <img
        hidden={slimeState !== SlimeState.HAPPY}
        src="/images/happy-slime.gif"
        alt="Slime Happy"
        width="85"
        className="crisp-edges"
      ></img>
      <img
        hidden={slimeState !== SlimeState.SAD}
        src="/images/sad-slime.gif"
        alt="Slime Happy"
        width="85"
        className="crisp-edges"
      ></img>
    </>
  );
}
