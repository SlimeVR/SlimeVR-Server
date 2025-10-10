import { useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import {} from '@/firmware-tool-api/firmwareToolComponents';
import { SomeJSONSchema } from 'ajv/dist/types/json-schema';
import { useEffect } from 'react';

const refToKey = (ref: string) => ref.substring('#/$defs/'.length);

type ComponentNode = { label: string } & (
  | {
      type: 'checkbox';
      value: boolean;
    }
  | {
      type: 'dropdown';
      items: string[];
      value: string;
    }
  | {
      type: 'text';
      value: string;
    }
  | {
      type: 'group';
      name: string;
      childrens: ComponentNode[];
    }
  | {
      type: 'list';
      childrens: ComponentNode[];
    }
);

const handleNode = (
  defs: SomeJSONSchema['$defs'],
  node: SomeJSONSchema,
  data: any,
  parentNode: SomeJSONSchema | null = null
): ComponentNode[] => {
  const components: ComponentNode[] = [];
  if (!node) return [];

  if (node.$ref) {
    return handleNode(defs, defs![refToKey(node.$ref)], data, node);
  }
  if (node.type === 'object') {
    if (
      node.oneOf &&
      Array.isArray(node.oneOf) &&
      node.discriminator?.propertyName
    ) {
      const discriminator = node.discriminator.propertyName;
      const selected = node.oneOf.find((o) => {
        if (
          o.$ref &&
          defs![refToKey(o.$ref)].properties[discriminator].const ===
            data[discriminator]
        ) {
          return true;
        }

        if (
          o.type === 'object' &&
          o.properties[discriminator].const === data[discriminator]
        ) {
          console.log(data);
          return true;
        }
        return false;
      });
      components.push(...handleNode(defs, selected, data, node));
    } else if (node.properties) {
      const childs: ComponentNode[] = [];
      for (const property of Object.keys(node.properties)) {
        childs.push(
          ...handleNode(defs, node.properties[property], data[property], node)
        );
      }
      components.push({
        type: 'group',
        name: node.description,
        childrens: childs,
        label: node.description ?? parentNode?.description ?? 'nothing o',
      });
    } else {
      throw 'unknown object';
    }
  }
  if (node.type === 'boolean') {
    components.push({
      type: 'checkbox',
      label: node.description ?? parentNode?.description ?? 'nothing b',
      value: data,
    });
  }
  if (node.type === 'array') {
    if (!Array.isArray(data)) throw 'not an array';
    const childs: ComponentNode[] = [];
    data.forEach((d) => {
      childs.push(...handleNode(defs, node.items, d, node));
    });
    components.push({
      type: 'list',
      childrens: childs,
      label: node.description ?? parentNode?.description ?? 'nothing a',
    });
  }
  if (node.type === 'string') {
    if (node.enum) {
      components.push({
        type: 'dropdown',
        label: node.description ?? parentNode?.description ?? 'nothing e',
        items: node.enum,
        value: data,
      });
    } else {
      components.push({
        type: 'text',
        label: node.description ?? parentNode?.description ?? 'nothing s',
        value: data,
      });
    }
  }
  if (node.type === 'number') {
    components.push({
      type: 'text',
      label: node.description ?? parentNode?.description ?? 'nothing n',
      value: data,
    });
  }
  return components;
};

export function BoardDefaultsStep({
  nextStep,
  goTo,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
}) {
  const { l10n } = useLocalization();
  const { selectedSource } = useFirmwareTool();

  useEffect(() => {
    if (!selectedSource) return;
    const d = selectedSource?.default?.schema as any as SomeJSONSchema;
    if (!d.$defs) throw 'no defs';
    const t = refToKey(d.properties.defaults.additionalProperties.$ref);
    if (!t) throw 'unable to get defaults ref';
    const boardConfig = d.$defs[t];
    const boardValues = d.$defs[refToKey(boardConfig.properties.values.$ref)];
    const data = selectedSource.default?.defaults.values as any;
    console.log(
      // selectedSource.default?.defaults.values
      JSON.stringify(handleNode(d.$defs, boardValues, data), null, 2)
    );
  }, [selectedSource]);

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-board_step-description')}
          </Typography>
        </div>
        <div className="my-4"></div>
      </div>
    </>
  );
}
