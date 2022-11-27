import { BodyPart } from "solarxr-protocol";

export const bodypartToString = (id: BodyPart) =>
  BodyPart[id].replace(/_/g, ' ');