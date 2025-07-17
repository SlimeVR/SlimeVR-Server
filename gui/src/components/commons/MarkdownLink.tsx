import { A } from '@/components/commons/A.js';
import { ComponentProps } from 'react';

export const MarkdownLink = (props: ComponentProps<'a'>) => (
  <A href={props.href}>{props.children}</A>
);
