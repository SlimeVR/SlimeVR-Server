import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import {
  boardComponentGraph,
  ComponentNode,
  SelectedSouce,
  useFirmwareTool,
  validateSource,
} from '@/hooks/firmware-tool';
import { useEffect, useState } from 'react';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { InputInside } from '@/components/commons/Input';
import { DropdownInside } from '@/components/commons/Dropdown';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { TrashIcon } from '@/components/commons/icon/TrashIcon';

function BoardDefaultsGraph({ graph }: { graph: ComponentNode[] }) {
  const { l10n } = useLocalization();

  const renderComponent = (
    c: ComponentNode,
    depth = 0,
    onDelete?: () => void
  ) => {
    if (c.type === 'group') {
      return (
        <div
          key={c.path.join('/')}
          className={classNames('flex flex-col rounded-lg', {
            'p-4 ': depth !== 0,
            'bg-background-80': depth >= 1,
          })}
        >
          <div className="flex justify-between items-center">
            <Typography variant="section-title">{c.label}</Typography>
            {onDelete && (
              <div
                className="p-2 rounded-full fill-background-10 hover:bg-background-50 hover:fill-status-critical cursor-pointer"
                onClick={() => onDelete && onDelete()}
              >
                <TrashIcon size={20} />
              </div>
            )}
          </div>
          <div
            className={classNames({
              'flex flex-col gap-4': depth + 1 <= 1,
              'grid sm:grid-cols-2 gap-2 items-end': depth + 1 > 1,
            })}
          >
            {c.childrens.map((c) => renderComponent(c, depth + 1))}
          </div>
        </div>
      );
    }

    if (c.type === 'checkbox') {
      return (
        <div key={c.path.join('/')}>
          <CheckboxInternal
            name={c.label}
            label={c.label}
            onChange={(e) => c.onMutate(e.currentTarget.checked)}
            checked={c.value}
            variant="toggle"
            outlined
          />
        </div>
      );
    }

    if (c.type === 'text') {
      return (
        <div className="flex flex-col pt-2" key={c.path.join('/')}>
          <InputInside
            name={c.label}
            label={c.label}
            placeholder={c.label}
            value={c.value}
            type={c.format === 'number' ? 'number' : 'text'}
            error={
              c.error
                ? { type: 'validate', message: l10n.getString(c.error) }
                : undefined
            }
            variant="primary"
            onChange={(e) => c.onMutate(e.currentTarget.value)}
          />
        </div>
      );
    }

    if (c.type === 'dropdown') {
      return (
        <div className="flex flex-col pt-2 gap-1" key={c.path.join('/')}>
          <Typography>{c.label}</Typography>
          <DropdownInside
            items={c.items.map((i) => ({ value: i, label: i }))}
            name={c.label}
            onChange={c.onMutate}
            error={
              c.error
                ? { type: 'validate', message: l10n.getString(c.error) }
                : undefined
            }
            placeholder={c.label}
            display="block"
            value={c.value}
            variant="secondary"
            maxHeight={200}
          />
        </div>
      );
    }

    if (c.type === 'list') {
      return (
        <div
          key={c.path.join('/')}
          className={classNames('flex flex-col gap-2 rounded-lg', {
            'p-4': depth >= 2,
            'bg-background-70': depth >= 1,
          })}
        >
          <Typography variant="section-title">{c.label}</Typography>
          <div
            className={classNames({
              'grid sm:grid-cols-2 gap-2': true,
            })}
          >
            {c.childrens.map((c2, index) =>
              renderComponent(
                c2,
                depth + 1,
                c.del ? () => c.del?.(index) : undefined
              )
            )}
            {c.add && (
              <div className="flex flex-col justify-center">
                <div className="flex justify-center">
                  <Localized id="firmware_tool-board_defaults-add">
                    <Button variant="primary" onClick={c.add} />
                  </Localized>
                </div>
              </div>
            )}
          </div>
        </div>
      );
    }
  };

  return (
    <div className="flex flex-col gap-2">
      {graph.map((c) => renderComponent(c, 0))}
    </div>
  );
}

export function BoardDefaultsStep({
  nextStep,
  prevStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
}) {
  const { l10n } = useLocalization();
  const { selectedSource, setSelectedSource } = useFirmwareTool();
  const [graph, setGraph] = useState<ComponentNode[]>([]);
  const [temporarySource, setTemporarySource] = useState<SelectedSouce>();
  const [valid, setValid] = useState<boolean>(false);
  const [tr, setTr] = useState(0);

  useEffect(() => {
    if (!selectedSource) return;
    setTemporarySource(JSON.parse(JSON.stringify(selectedSource))); // make a deep copy bc the graph will modify temporarySource by references
  }, [selectedSource]);

  useEffect(() => {
    if (!temporarySource) return;
    const components = boardComponentGraph(temporarySource, () => {
      setTr((tr) => tr + 1);
    });
    setGraph(components);
    setValid(validateSource(temporarySource));
  }, [temporarySource, tr]);

  const submit = () => {
    setSelectedSource(temporarySource);
    nextStep();
  };

  const reset = () => {
    setTemporarySource(selectedSource);
    setTr((tr) => tr + 1);
  };

  return (
    <>
      <div className="flex flex-col w-full gap-4">
        <div className="flex flex-grow flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-board_defaults-description')}
          </Typography>
        </div>
        <BoardDefaultsGraph graph={graph} />
        <div className="flex justify-between">
          <Localized id="firmware_tool-previous_step">
            <Button variant="secondary" onClick={prevStep} />
          </Localized>
          <div className="flex gap-2">
            <Localized id="firmware_tool-board_defaults-reset">
              <Button variant="secondary" onClick={reset} />
            </Localized>
            <Localized id="firmware_tool-ok">
              <Button variant="primary" disabled={!valid} onClick={submit} />
            </Localized>
          </div>
        </div>
      </div>
    </>
  );
}
