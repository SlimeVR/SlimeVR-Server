import { Dispatch, ReactNode, SetStateAction } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import classNames from 'classnames';
import { Config, useConfig } from '@/hooks/config';

export function LayoutSelector({
  children,
  name,
  active = false,
  onClick,
}: {
  children: ReactNode;
  name: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <div
      className={classNames(
        'w-40 aspect-video bg-background-70 flex-col flex rounded-lg border-2 group cursor-pointer',
        {
          'border-accent-background-20': active,
          'border-background-50 hover:border-background-40': !active,
        }
      )}
      onClick={onClick}
    >
      <div className="px-2 pt-2 pb-1">
        <Typography id={name}></Typography>
      </div>
      <div
        className={classNames('h-[2px] w-full mb-2', {
          'group-hover:bg-background-40 bg-background-50': !active,
          'bg-accent-background-20': active,
        })}
      ></div>
      {children}
    </div>
  );
}

export function HomeSettingsModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
  const { config, setConfig } = useConfig();

  const setLayout = (layout: Config['homeLayout']) =>
    setConfig({ homeLayout: layout });

  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={'max-w-xl w-full'}
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col gap-4">
        <Typography variant="main-title">Home Page Settings</Typography>
        <div className="flex flex-col gap-2">
          <Typography variant="section-title">Trackers list layout</Typography>
          <div className="flex gap-4">
            <LayoutSelector
              name="Grid"
              active={config?.homeLayout === 'default'}
              onClick={() => setLayout('default')}
            >
              <div className="grid grid-cols-2 gap-2 p-2">
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
              </div>
            </LayoutSelector>
            <LayoutSelector
              name="Table"
              active={config?.homeLayout === 'table'}
              onClick={() => setLayout('table')}
            >
              <div className="grid grid-cols-1 gap-2 p-2">
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
                <div className="h-2 rounded-lg bg-background-40"></div>
              </div>
            </LayoutSelector>
          </div>
        </div>

        <div className="flex justify-end">
          <Button variant="tertiary" onClick={() => open[1](false)}>
            Close
          </Button>
        </div>
      </div>
    </BaseModal>
  );
}
