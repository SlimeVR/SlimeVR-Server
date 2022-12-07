import { ReactNode } from 'react';
import { BulbIcon } from './icon/BulbIcon';
import { Typography } from './Typography';

export function TipBox({ children }: { children: ReactNode }) {
  return (
    <div className="flex flex-row gap-4 bg-accent-background-50 p-4 rounded-md">
      <div className="fill-accent-background-20 flex flex-col justify-center">
        <BulbIcon></BulbIcon>
      </div>
      <div className="flex flex-col">
        <Typography color="text-accent-background-10">{children}</Typography>
      </div>
    </div>
  );
}
