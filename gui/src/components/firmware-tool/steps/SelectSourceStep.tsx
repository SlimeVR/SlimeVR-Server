import { Localized } from '@fluent/react';
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
import { useSafeLocalization } from '@/i18n/config';

function Selector({
  text,
  active,
  disabled,
  tag,
  onClick,
}: {
  text: string;
  active: boolean;
  official?: boolean;
  tag?: 'official' | 'dev';
  disabled?: boolean;
  onClick: () => void;
}) {
  return (
    <div
      className={classNames(
        'p-3 rounded-md hover:bg-background-50 w-full cursor-pointer relative',
        {
          'bg-background-50 text-background-10': active,
          'bg-background-60': !active,
          'bg-background-80 text-background-50': disabled,
        }
      )}
      onClick={() => {
        if (!disabled) onClick();
      }}
    >
      {tag === 'official' && (
        <div
          className={classNames(
            'absolute px-2 py-0.5 rounded-md bg-accent-background-20 -top-2 -right-2',
            { 'brightness-20': disabled, 'brightness-50': !active }
          )}
        >
          <Localized id="firmware_tool-select_source-official">
            <Typography />
          </Localized>
        </div>
      )}
      {tag === 'dev' && (
        <div
          className={classNames(
            'absolute px-2 py-0.5 rounded-md bg-status-warning -top-2 -right-2',
            { 'brightness-20': disabled, 'brightness-50': !active }
          )}
        >
          <Localized id="firmware_tool-select_source-dev">
            <Typography color="text-background-90" />
          </Localized>
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
  const { l10n, getStringOrNull } = useSafeLocalization();
  const { setSelectedSource, selectedSource, selectedDefault } =
    useFirmwareTool();
  const [partialBoard, setPartialBoard] = useState<{
    source?: string;
    version?: string;
    board?: string;
  }>();

  const {
    isFetching,
    isError,
    data: sources,
    refetch,
  } = useGetFirmwareSources({});

  const { possibleBoards, possibleVersions, sourcesGroupped } = useMemo(() => {
    return {
      sourcesGroupped: sources
        ?.reduce(
          (curr, source) => {
            if (!curr.find(({ name }) => source.source === name))
              curr.push({
                name: source.source,
                official: source.official,
              });
            return curr;
          },
          [] as { name: string; official: boolean }[]
        )
        .sort((a, b) => {
          if (a.official !== b.official) return a.official ? -1 : 1;
          return a.name.localeCompare(b.name);
        }),
      possibleBoards: sources
        ?.reduce((curr, source) => {
          if (source.source !== partialBoard?.source) return curr;
          const unknownBoards = source.availableBoards.filter(
            (b) => !curr.includes(b)
          );
          curr.push(...unknownBoards);
          return curr;
        }, [] as string[])
        .sort((a, b) => {
          // Sort official board type first
          const aStartsWithBoard = a.startsWith('BOARD_SLIMEVR');
          const bStartsWithBoard = b.startsWith('BOARD_SLIMEVR');

          if (aStartsWithBoard && !bStartsWithBoard) return -1;
          if (!aStartsWithBoard && bStartsWithBoard) return 1;

          if (a === 'BOARD_SLIMEVR_DEV' && b !== 'BOARD_SLIMEVR_DEV') return 1;
          if (a !== 'BOARD_SLIMEVR_DEV' && b === 'BOARD_SLIMEVR_DEV') return -1;
          return a.localeCompare(b);
        }),
      possibleVersions: sources
        ?.reduce(
          (curr, source) => {
            if (source.source !== partialBoard?.source) return curr;
            if (!curr.find(({ name }) => source.version === name))
              curr.push({
                disabled:
                  !partialBoard?.board ||
                  !source.availableBoards.includes(partialBoard.board) ||
                  source.source !== partialBoard.source,
                name: source.version,
                isBranch: source.branch == source.version,
              });

            return curr;
          },
          [] as { name: string; disabled: boolean; isBranch: boolean }[]
        )
        .sort((a, b) => {
          if (a.isBranch !== b.isBranch) return a.isBranch ? 1 : -1;
          return a.name.localeCompare(b.name);
        }),
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
      }).then((board) => {
        setSelectedSource({
          source: params,
          default: board,
        });
      });
    } else {
      setSelectedSource(undefined);
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
            {l10n.getString('firmware_tool-select_source-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isFetching && !isError && (
            <div className="flex flex-col gap-2">
              <div className="grid md:grid-cols-3 gap-4">
                <div className="flex flex-col gap-1 w-full">
                  <Localized id="firmware_tool-select_source-firmware">
                    <Typography variant="section-title" />
                  </Localized>
                  <div className="flex flex-col gap-4 md:max-h-[305px] overflow-y-auto bg-background-80 rounded-lg p-4">
                    {sourcesGroupped?.map(({ name, official }) => (
                      <Selector
                        active={partialBoard?.source === name}
                        key={`${name}`}
                        tag={official ? 'official' : undefined}
                        onClick={() => {
                          if (partialBoard?.source !== name)
                            setPartialBoard({ source: name });
                        }}
                        text={formatSource(name, official)}
                      />
                    ))}
                  </div>
                </div>
                <div className="flex flex-col gap-1 w-full">
                  <Localized id="firmware_tool-select_source-board_type">
                    <Typography variant="section-title" />
                  </Localized>
                  <div className="flex flex-col gap-4 md:max-h-[305px] overflow-y-auto bg-background-80 rounded-lg p-4">
                    {possibleBoards?.map((board) => (
                      <Selector
                        active={partialBoard?.board === board}
                        key={`${board}`}
                        onClick={() => {
                          setPartialBoard((curr) => ({ ...curr, board }));
                        }}
                        tag={
                          board.startsWith('BOARD_SLIMEVR')
                            ? board === 'BOARD_SLIMEVR_DEV'
                              ? 'dev'
                              : 'official'
                            : undefined
                        }
                        text={
                          getStringOrNull(
                            `board_type-${board.replace('BOARD_', '')}`
                          ) ?? board.replace('BOARD_', '').replaceAll('_', ' ')
                        }
                      />
                    ))}
                    {partialBoard?.source && possibleBoards?.length === 0 && (
                      <Typography id="firmware_tool-select_source-no_boards" />
                    )}
                    {!partialBoard?.source && (
                      <Typography id="firmware_tool-select_source-not_selected" />
                    )}
                  </div>
                </div>

                <div className="flex flex-col gap-1 w-full">
                  <Localized id="firmware_tool-select_source-version">
                    <Typography variant="section-title" />
                  </Localized>
                  <div className="flex flex-col gap-4 md:max-h-[305px] overflow-y-auto bg-background-80 rounded-lg p-4">
                    {possibleVersions?.map(({ name, disabled }) => (
                      <Selector
                        active={partialBoard?.version === name}
                        disabled={disabled}
                        key={`${name}`}
                        tag={
                          partialBoard?.source?.startsWith('SlimeVR/') &&
                          name === 'llelievr/board-defaults'
                            ? 'dev'
                            : undefined
                        }
                        onClick={() => {
                          setPartialBoard((curr) => ({
                            ...curr,
                            version: name,
                          }));
                        }}
                        text={name}
                      />
                    ))}
                    {partialBoard?.source && possibleVersions?.length === 0 && (
                      <Typography id="firmware_tool-select_source-no_versions" />
                    )}
                    {!partialBoard?.source && (
                      <Typography id="firmware_tool-select_source-not_selected" />
                    )}
                  </div>
                </div>
              </div>
              <div className="flex justify-end">
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={!selectedSource}
                    onClick={() => {
                      if (selectedDefault?.flashingRules.shouldOnlyUseDefaults)
                        goTo('Build');
                      else nextStep();
                    }}
                  />
                </Localized>
              </div>
            </div>
          )}

          {isFetching && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY} />
              <Localized id="firmware_tool-loading">
                <Typography />
              </Localized>
            </div>
          )}
          {isError && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.SAD} />
              <Localized id="firmware_tool-select_source-error">
                <Typography />
              </Localized>
              <Localized id="firmware_tool-retry">
                <Button variant="primary" onClick={() => refetch()} />
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
