import { ReactNode } from 'react';
import { BodyPart } from 'solarxr-protocol';

export type ExtremitySide = 'left' | 'right';

export type ExtremityParts = {
  digits: Record<string, BodyPart[]>;
  root: BodyPart;
};

export type ExtremityFigureSpec = {
  image: string;
  width: number;
  height: number;
  anchors: Record<string, [number, number][]>;
  rootAnchor: [number, number];
};

export type ExtremityDescriptor = {
  digits: readonly string[];
  sides: Record<ExtremitySide, ExtremityParts>;
  figure: ExtremityFigureSpec;
  digitLabelId: (digit: string) => string;
  partLabelId: (part: BodyPart) => string;
  mainPart: (parts: BodyPart[]) => BodyPart;
  Layout: () => ReactNode;
};
