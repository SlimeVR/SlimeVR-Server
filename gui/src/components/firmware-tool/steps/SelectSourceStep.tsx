import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import {
  fetchGetFirmwareBoardDefaults,
  useGetFirmwareSources,
} from '@/firmware-tool-api/firmwareToolComponents';
import { useEffect, useMemo, useState } from 'react';

function Selector({
  text,
  active,
  disabled,
  official,
  onClick,
}: {
  text: string;
  active: boolean;
  official?: boolean;
  disabled?: boolean;
  onClick: () => void;
}) {
  return (
    <div
      className={classNames(
        'p-3 rounded-md hover:bg-background-50 w-full cursor-pointer relative',
        {
          'bg-background-50 text-background-10': active, // FIXME: use selected source
          'bg-background-60': !active,
          'bg-background-80 text-background-50': disabled,
        }
      )}
      onClick={() => {
        if (!disabled) onClick();
      }}
    >
      {official && (
        <div className="absolute px-2 py-0.5 rounded-md bg-accent-background-20 -top-2 -right-2">
          <Typography>Official</Typography>
        </div>
      )}
      {text}
    </div>
  );
}

export function SelectSourceSetep({
  nextStep,
  goTo,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
}) {
  const { l10n } = useLocalization();
  const { setSelectedSource, selectedSource } = useFirmwareTool();
  const [partialBoard, setPartialBoard] = useState<{
    source?: string;
    version?: string;
    board?: string;
  }>();
  const { isFetching, data: sources } = useGetFirmwareSources({});

  const { possibleBoards, possibleVersions, sourcesGroupped } = useMemo(() => {
    return {
      sourcesGroupped: sources?.reduce(
        (curr, source) => {
          if (!curr.find(({ name }) => source.source === name))
            curr.push({
              name: source.source,
              official: source.official,
              disabled:
                !partialBoard?.board ||
                !source.availableBoards.includes(partialBoard.board),
            });
          return curr;
        },
        [] as { name: string; official: boolean; disabled: boolean }[]
      ),
      possibleBoards: sources?.reduce((curr, source) => {
        const unknownBoards = source.availableBoards.filter(
          (b) => !curr.includes(b)
        );
        curr.push(...unknownBoards);
        return curr;
      }, [] as string[]),
      possibleVersions: sources?.reduce(
        (curr, source) => {
          if (!curr.find(({ name }) => source.version === name))
            curr.push({
              disabled:
                !partialBoard?.board ||
                !source.availableBoards.includes(partialBoard.board) ||
                source.source !== partialBoard.source,
              name: source.version,
            });

          return curr;
        },
        [] as { name: string; disabled: boolean }[]
      ),
    };
  }, [sources, partialBoard]);

  useEffect(() => {
    if (partialBoard?.source && partialBoard.board && partialBoard.version) {
      const params = {
        board: partialBoard.board,
        source: partialBoard.source,
        version: partialBoard.version,
      };
      fetchGetFirmwareBoardDefaults({
        queryParams: params,
      }).then((board) =>
        setSelectedSource({
          source: params,
          default: board,
        })
      );
    }
  }, [partialBoard]);

  const formatSource = (name: string, official: boolean) => {
    return !official ? name : name.substring(name.indexOf('/') + 1);
  };

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-board_step-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isFetching && (
            <>
              <div className="grid grid-cols-3 gap-4">
                <div className="flex flex-col gap-2 w-full">
                  <Typography variant="section-title">Board Type</Typography>
                  <div className="flex flex-col gap-2">
                    {possibleBoards?.map((board) => (
                      <Selector
                        active={partialBoard?.board === board}
                        key={`${board}`}
                        onClick={() => {
                          setPartialBoard({ board });
                        }}
                        text={board}
                      ></Selector>
                    ))}
                  </div>
                </div>
                <div className="flex flex-col gap-2 w-full">
                  <Typography variant="section-title">
                    Firmware Source
                  </Typography>
                  <div className="flex flex-col gap-2">
                    {sourcesGroupped?.map(({ name, official, disabled }) => (
                      <Selector
                        active={partialBoard?.source === name}
                        disabled={disabled}
                        key={`${name}`}
                        official={official}
                        onClick={() => {
                          setPartialBoard((curr) => ({
                            ...curr,
                            source: name,
                          }));
                        }}
                        text={formatSource(name, official)}
                      ></Selector>
                    ))}
                  </div>
                </div>
                <div className="flex flex-col gap-2 w-full">
                  <Typography variant="section-title">
                    Firmware Version
                  </Typography>
                  <div className="flex flex-col gap-2">
                    {possibleVersions?.map(({ name, disabled }) => (
                      <Selector
                        active={partialBoard?.version === name}
                        disabled={disabled}
                        key={`${name}`}
                        onClick={() => {
                          setPartialBoard((curr) => ({
                            ...curr,
                            version: name,
                          }));
                        }}
                        text={name}
                      ></Selector>
                    ))}
                  </div>
                </div>
              </div>
              <div className="flex justify-end">
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={!selectedSource}
                    onClick={() => {
                      //   if (
                      //     selectedSource?.default?.defaults.flashingRules
                      //       .shouldOnlyUseDefaults
                      //   ) {
                      //     goTo('SelectFirmware');
                      //   } else {
                      nextStep();
                      //   }
                    }}
                  ></Button>
                </Localized>
              </div>
            </>
          )}

          {isFetching && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography></Typography>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
