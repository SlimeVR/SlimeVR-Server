import { createContext, useContext, useMemo, useState } from 'react';
import {
  DeviceIdT,
  FirmwarePartT,
  FirmwareUpdateMethod,
  FirmwareUpdateRequestT,
  FirmwareUpdateStatus,
  OTAFirmwareUpdateT,
  SerialDevicePortT,
  SerialFirmwareUpdateT,
} from 'solarxr-protocol';
import { OnboardingContext } from './onboarding';
import {
  BoardDefaults,
  FirmwareBoardDefaultsNullable,
  FirmwareWithFiles,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { GetFirmwareBoardDefaultsQueryParams } from '@/firmware-tool-api/firmwareToolComponents';
import { SomeJSONSchema } from 'ajv/dist/types/json-schema';
import { Ajv2020 } from 'ajv/dist/2020';

export type SelectedSouce = {
  source: GetFirmwareBoardDefaultsQueryParams;
  default: FirmwareBoardDefaultsNullable;
};

export type SelectedDevice = {
  type: FirmwareUpdateMethod;
  deviceId: string | number;
  deviceNames: string[];
};

export const firmwareUpdateErrorStatus = [
  FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED,
  FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND,
  FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED,
  FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED,
  FirmwareUpdateStatus.ERROR_TIMEOUT,
  FirmwareUpdateStatus.ERROR_UNKNOWN,
  FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD,
  FirmwareUpdateStatus.ERROR_UPLOAD_FAILED,
];

export const firmwareUpdateStatusLabel: Record<FirmwareUpdateStatus, string> = {
  [FirmwareUpdateStatus.DOWNLOADING]: 'firmware_update-status-DOWNLOADING',
  [FirmwareUpdateStatus.NEED_MANUAL_REBOOT]:
    'firmware_update-status-NEED_MANUAL_REBOOT-v2',
  [FirmwareUpdateStatus.AUTHENTICATING]: 'firmware_update-status-AUTHENTICATING',
  [FirmwareUpdateStatus.UPLOADING]: 'firmware_update-status-UPLOADING',
  [FirmwareUpdateStatus.SYNCING_WITH_MCU]: 'firmware_update-status-SYNCING_WITH_MCU',
  [FirmwareUpdateStatus.REBOOTING]: 'firmware_update-status-REBOOTING',
  [FirmwareUpdateStatus.PROVISIONING]: 'firmware_update-status-PROVISIONING',
  [FirmwareUpdateStatus.DONE]: 'firmware_update-status-DONE',
  [FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND]:
    'firmware_update-status-ERROR_DEVICE_NOT_FOUND',
  [FirmwareUpdateStatus.ERROR_TIMEOUT]: 'firmware_update-status-ERROR_TIMEOUT',
  [FirmwareUpdateStatus.ERROR_DOWNLOAD_FAILED]:
    'firmware_update-status-ERROR_DOWNLOAD_FAILED',
  [FirmwareUpdateStatus.ERROR_AUTHENTICATION_FAILED]:
    'firmware_update-status-ERROR_AUTHENTICATION_FAILED',
  [FirmwareUpdateStatus.ERROR_UPLOAD_FAILED]:
    'firmware_update-status-ERROR_UPLOAD_FAILED',
  [FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED]:
    'firmware_update-status-ERROR_PROVISIONING_FAILED',
  [FirmwareUpdateStatus.ERROR_UNSUPPORTED_METHOD]:
    'firmware_update-status-ERROR_UNSUPPORTED_METHOD',
  [FirmwareUpdateStatus.ERROR_UNKNOWN]: 'firmware_update-status-ERROR_UNKNOWN',
};

export type FirmwareToolContext = ReturnType<typeof provideFirmwareTool>;
export const FirmwareToolContextC = createContext<FirmwareToolContext>(
  undefined as any
);

export function useFirmwareTool() {
  const context = useContext<FirmwareToolContext>(FirmwareToolContextC);
  if (!context) {
    throw new Error('useFirmwareTool must be within a FirmwareToolContext Provider');
  }
  return context;
}

export function provideFirmwareTool() {
  const [selectedSource, setSelectedSource] = useState<SelectedSouce>();
  const [files, setFiles] = useState<FirmwareWithFiles['files']>();
  const [selectedDevices, selectDevices] = useState<SelectedDevice[] | null>(null);

  return {
    selectedSource,
    setSelectedSource,
    files,
    setFiles,
    selectedDevices,
    selectDevices,
    selectedDefault: useMemo(
      () =>
        (selectedSource?.source.board &&
          selectedSource?.default?.data.defaults[selectedSource.source.board]) ||
        null,
      [selectedSource]
    ),
  };
}

export const getFlashingRequests = (
  devices: SelectedDevice[],
  firmwareFiles: FirmwareWithFiles['files'],
  onboardingState: OnboardingContext['state'],
  defaultConfig: BoardDefaults | null
) => {
  const firmware = firmwareFiles.find(({ isFirmware }) => isFirmware);
  if (!firmware) throw new Error('invalid state - no firmware to find');

  const requests = [];

  for (const device of devices) {
    switch (device.type) {
      case FirmwareUpdateMethod.OTAFirmwareUpdate: {
        const dId = new DeviceIdT();
        dId.id = +device.deviceId;

        const part = new FirmwarePartT();
        part.offset = 0;
        part.url = firmware.filePath;
        part.digest = firmware.digest;

        const method = new OTAFirmwareUpdateT();
        method.deviceId = dId;
        method.firmwarePart = part;

        const req = new FirmwareUpdateRequestT();
        req.method = method;
        req.methodType = FirmwareUpdateMethod.OTAFirmwareUpdate;
        requests.push(req);
        break;
      }
      case FirmwareUpdateMethod.SerialFirmwareUpdate: {
        const id = new SerialDevicePortT();
        id.port = device.deviceId.toString();

        if (!onboardingState.wifi?.ssid)
          throw new Error('invalid state, wifi should be set');

        const method = new SerialFirmwareUpdateT();
        method.deviceId = id;
        method.ssid = onboardingState.wifi.ssid;
        method.password = onboardingState.wifi.password;
        method.needManualReboot =
          defaultConfig?.flashingRules.needManualReboot ?? false;

        method.firmwarePart = firmwareFiles.map(({ offset, filePath, digest }) => {
          const part = new FirmwarePartT();
          part.offset = offset;
          part.url = filePath;
          part.digest = digest;
          return part;
        });

        const req = new FirmwareUpdateRequestT();
        req.method = method;
        req.methodType = FirmwareUpdateMethod.SerialFirmwareUpdate;
        requests.push(req);
        break;
      }
      default: {
        throw new Error('unsupported flashing method');
      }
    }
  }
  return requests;
};

const refToKey = (ref: string) => ref.substring('#/$defs/'.length);

type Path = (string | number)[];

type OnChangeCallback = (path: Path, newValue: any, rootData: any) => void;

type TraversalContext = {
  defs: NonNullable<SomeJSONSchema['$defs']>;
  rootData: any;
  onChange: OnChangeCallback;
  ownerSchema: SomeJSONSchema | null;
  path: Path;
  data: any;
  propertySchema?: SomeJSONSchema; // The original property schema (before ref resolution)
};

export type ComponentNode =
  | {
      type: 'checkbox';
      label: string;
      value: boolean;
      path: Path;
      onMutate: (newValue: boolean) => void;
    }
  | {
      type: 'dropdown';
      items: string[];
      value: string;
      label: string;
      path: Path;
      error?: string;
      onMutate: (newValue: string) => void;
    }
  | {
      type: 'text';
      format: 'string' | 'number';
      value: string;
      label: string;
      path: Path;
      error?: string;
      onMutate: (newValue: string) => void;
    }
  | {
      type: 'group';
      label: string | undefined;
      childrens: ComponentNode[];
      path: Path;
    }
  | {
      type: 'list';
      childrens: ComponentNode[];
      label: string;
      path: Path;
      add?: () => void;
      del?: (index: number) => void;
    };

const setAtPath = (obj: any, path: Path, value: any): void => {
  let current = obj;
  for (let i = 0; i < path.length - 1; i++) {
    current = current[path[i]];
  }
  if (value === undefined) {
    const key = path[path.length - 1];
    if (Array.isArray(current) && typeof key === 'number') {
      current.splice(key, 1);
    } else {
      delete current[key];
    }
  } else {
    current[path[path.length - 1]] = value;
  }
};

const resolveLabel = (
  ctx: TraversalContext,
  node: SomeJSONSchema,
  propertySchema?: SomeJSONSchema
): string | null => {
  if (!ctx.defs) throw 'Womp womp no defs';

  // If we have a property schema (the original schema before ref resolution), check it first
  if (propertySchema?.$ref && propertySchema?.description) {
    return propertySchema.description;
  }

  if (node.description) return node.description;

  if (propertySchema?.$ref) {
    const refContent = ctx.defs[refToKey(propertySchema.$ref)];
    return resolveLabel(ctx, refContent, propertySchema);
  }

  if (node.$ref) {
    const refContent = ctx.defs[refToKey(node.$ref)];
    return resolveLabel(ctx, refContent, node);
  }

  return null;
};

const extractDiscriminatorValues = (
  ctx: TraversalContext,
  oneOf: SomeJSONSchema[],
  discriminatorProp: string,
  mapping: Record<string, string>
): string[] => {
  const values = new Set<string>();

  Object.keys(mapping).forEach((key) => values.add(key));

  oneOf.forEach((option: SomeJSONSchema) => {
    const target = option.$ref ? ctx.defs[refToKey(option.$ref)] : option;
    const prop = target?.properties?.[discriminatorProp];

    if (prop) {
      if (prop.const !== undefined) {
        values.add(prop.const);
      }
      if (Array.isArray(prop.enum)) {
        prop.enum.forEach((v: string) => values.add(v));
      }
    }
  });

  return Array.from(values);
};

const isPropertyRequired = (ctx: TraversalContext): boolean => {
  const propertyName = ctx.path[ctx.path.length - 1];

  if (!ctx.ownerSchema || typeof propertyName === 'number') {
    return false;
  }

  // The ownerSchema is already resolved (no $refs), so we can check directly
  return ctx.ownerSchema.required?.includes(propertyName as string) ?? false;
};

const handleObjectNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  if (node.oneOf && Array.isArray(node.oneOf) && node.discriminator?.propertyName) {
    return handleDiscriminatedOneOf(ctx, node);
  }

  if (node.properties) {
    const childs: ComponentNode[] = [];

    for (const property of Object.keys(node.properties)) {
      const propSchema = node.properties[property];
      childs.push(
        ...handleNode(
          {
            ...ctx,
            ownerSchema: node,
            propertySchema: propSchema,
            path: [...ctx.path, property],
            data: ctx.data[property],
          },
          propSchema
        )
      );
    }

    return [
      {
        type: 'group',
        childrens: childs,
        path: ctx.path,
        label: resolveLabel(ctx, node, ctx.propertySchema) ?? undefined,
      },
    ];
  }

  throw new Error('Unknown object structure: missing oneOf or properties');
};

const handleDiscriminatedOneOf = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  if (!ctx.defs) throw 'Womp womp no defs';

  const discriminator = node.discriminator!.propertyName;
  const mapping = node.discriminator!.mapping ?? {};
  const discriminatorValue = ctx.data?.[discriminator];

  const possibleValues = extractDiscriminatorValues(
    ctx,
    node.oneOf!,
    discriminator,
    mapping
  );

  const discriminatorPath = [...ctx.path, discriminator];
  const discriminatorDropdown: ComponentNode = {
    type: 'dropdown',
    label: discriminator,
    items: possibleValues,
    value: discriminatorValue ?? possibleValues[0] ?? '',
    path: discriminatorPath,
    onMutate: (newValue: string) => {
      setAtPath(ctx.rootData, discriminatorPath, newValue);
      ctx.onChange(discriminatorPath, newValue, ctx.rootData);
    },
  };

  const activeValue = discriminatorValue ?? possibleValues[0];

  if (!activeValue) {
    console.warn('No discriminator values found, skipping');
    return [];
  }

  let candidate: SomeJSONSchema | undefined;

  if (mapping[activeValue]) {
    const mappedKey = refToKey(mapping[activeValue]);
    candidate = ctx.defs[mappedKey];
  }

  if (!candidate) {
    candidate = node.oneOf!.find((o: SomeJSONSchema) => {
      const target = o.$ref ? ctx.defs[refToKey(o.$ref)] : o;
      const prop = target?.properties?.[discriminator];

      if (!prop) return false;

      if (prop.const === activeValue) return true;
      if (Array.isArray(prop.enum) && prop.enum.includes(activeValue)) return true;

      return false;
    });
  }

  if (!candidate) {
    console.warn(`No matching discriminator found for ${activeValue}, skipping`);
    return [];
  }

  const resolvedCandidate = candidate.$ref
    ? ctx.defs[refToKey(candidate.$ref)]
    : candidate;

  const childs: ComponentNode[] = [discriminatorDropdown];

  if (resolvedCandidate.properties) {
    for (const property of Object.keys(resolvedCandidate.properties)) {
      if (property === discriminator) continue;

      const propSchema = resolvedCandidate.properties[property];
      childs.push(
        ...handleNode(
          {
            ...ctx,
            ownerSchema: resolvedCandidate,
            propertySchema: propSchema,
            path: [...ctx.path, property],
            data: ctx.data[property],
          },
          propSchema
        )
      );
    }
  }

  return [
    {
      type: 'group',
      childrens: childs,
      path: ctx.path,
      label:
        resolveLabel(ctx, resolvedCandidate) ??
        resolveLabel(ctx, node, ctx.propertySchema) ??
        undefined,
    },
  ];
};

const handleArrayNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  if (!ctx.defs) throw 'Womp womp no defs';
  if (!Array.isArray(ctx.data)) {
    throw new Error('Expected array data but received non-array');
  }

  const childs: ComponentNode[] = [];

  ctx.data.forEach((d, index) => {
    childs.push(
      ...handleNode(
        {
          ...ctx,
          ownerSchema: node,
          propertySchema: node.items,
          path: [...ctx.path, index],
          data: d,
        },
        node.items
      )
    );
  });

  let add = undefined;
  if (node.items.$ref && childs.length < node.maxItems) {
    const item = ctx.defs[refToKey(node.items.$ref)];
    const discriminatorPath = [...ctx.path, childs.length];
    if (item.discriminator?.propertyName && item.oneOf) {
      const discriminator = item.discriminator?.propertyName;
      const discriminatorValue = item.oneOf
        .map((o: SomeJSONSchema) => {
          if (o.$ref) return ctx.defs[refToKey(o.$ref)];
          return o;
        })
        .map((o: SomeJSONSchema) => {
          if (o.type === 'object' && o.properties[discriminator]?.const)
            return o.properties[discriminator].const;
          if (o.type === 'string' && o.enum && o.enum.length > 0) return o.enum[0];
          return null;
        })
        .find((o: string) => !!o);
      if (discriminatorValue) {
        const payload = {
          [discriminator]: discriminatorValue,
        };

        add = () => {
          setAtPath(ctx.rootData, discriminatorPath, payload);
          ctx.onChange(discriminatorPath, payload, ctx.rootData);
        };
      }
    } else {
      add = () => {
        setAtPath(ctx.rootData, discriminatorPath, item);
        ctx.onChange(discriminatorPath, item, ctx.rootData);
      };
    }
  }

  let del = undefined;
  if (node.items.$ref && childs.length > node.minItems) {
    del = (index: number) => {
      const discriminatorPath = [...ctx.path, index];
      setAtPath(ctx.rootData, discriminatorPath, undefined);
      ctx.onChange(discriminatorPath, undefined, ctx.rootData);
    };
  }

  return [
    {
      type: 'list',
      childrens: childs,
      path: ctx.path,
      label: resolveLabel(ctx, node, ctx.propertySchema) ?? 'unknown label',
      add,
      del,
    },
  ];
};

const handleStringNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  const required = isPropertyRequired(ctx);

  if (!ctx.defs) throw 'Womp womp no defs';
  if (node.enum) {
    const error = required && !ctx.data;
    return [
      {
        type: 'dropdown',
        label: resolveLabel(ctx, node, ctx.propertySchema) ?? 'unknown label',
        items: node.enum,
        value: ctx.data,
        path: ctx.path,
        error: error ? 'Field not selected' : undefined,
        onMutate: (newValue: string) => {
          setAtPath(ctx.rootData, ctx.path, newValue);
          ctx.onChange(ctx.path, newValue, ctx.rootData);
        },
      },
    ];
  }

  let error: string | undefined = undefined;
  if (required && !ctx.data) {
    error = 'firmware_tool-board_defaults-error-required';
  } else if (ctx.data && node.pattern && !ctx.data.match(new RegExp(node.pattern))) {
    error = 'firmware_tool-board_defaults-error-format';
  }

  return [
    {
      type: 'text',
      format: 'string',
      label: resolveLabel(ctx, node, ctx.propertySchema) ?? 'unknown label',
      value: ctx.data,
      path: ctx.path,
      error,
      onMutate: (newValue: string) => {
        setAtPath(ctx.rootData, ctx.path, newValue);
        ctx.onChange(ctx.path, newValue, ctx.rootData);
      },
    },
  ];
};

const handleNumberNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  const required = isPropertyRequired(ctx);
  const value: string = ctx.data?.toString() ?? '';

  let error: string | undefined = undefined;
  if (required && !value) {
    error = 'firmware_tool-board_defaults-error-required';
  } else if (value && !value.match(/^-?\d+(\.\d+)?$/)) {
    error = 'firmware_tool-board_defaults-error-format-number';
  }

  return [
    {
      type: 'text',
      format: 'number',
      label: resolveLabel(ctx, node, ctx.propertySchema) ?? 'unknown label',
      value,
      path: ctx.path,
      error,
      onMutate: (newValue: string) => {
        setAtPath(ctx.rootData, ctx.path, Number(newValue));
        ctx.onChange(ctx.path, Number(newValue), ctx.rootData);
      },
    },
  ];
};

const handleBooleanNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema
): ComponentNode[] => {
  return [
    {
      type: 'checkbox',
      label: resolveLabel(ctx, node, ctx.propertySchema) ?? 'unknown label',
      value: ctx.data,
      path: ctx.path,
      onMutate: (newValue: boolean) => {
        setAtPath(ctx.rootData, ctx.path, newValue);
        ctx.onChange(ctx.path, newValue, ctx.rootData);
      },
    },
  ];
};

const handleNode = (
  ctx: TraversalContext,
  node: SomeJSONSchema | undefined
): ComponentNode[] => {
  if (!ctx.defs) throw 'Womp womp no defs';

  if (!node) {
    return [];
  }

  // When we encounter a $ref, resolve it but keep the same ownerSchema and propertySchema
  if (node.$ref) {
    return handleNode(ctx, ctx.defs[refToKey(node.$ref)]);
  }

  switch (node.type) {
    case 'object':
      return handleObjectNode(ctx, node);
    case 'boolean':
      return handleBooleanNode(ctx, node);
    case 'array':
      return handleArrayNode(ctx, node);
    case 'string':
      return handleStringNode(ctx, node);
    case 'number':
      return handleNumberNode(ctx, node);
    default:
      console.warn('unhandled type', node.type, 'giving empty components');
      return [];
  }
};

export const boardComponentGraph = (
  selectedSource: SelectedSouce,
  onChange?: OnChangeCallback
) => {
  const d = selectedSource.default?.schema as any as SomeJSONSchema;
  if (!d.$defs) throw 'no defs';
  const t = refToKey(d.properties.defaults.additionalProperties.$ref);
  if (!t) throw 'unable to get defaults ref';
  const boardConfig = d.$defs[t];
  const boardValues = d.$defs[refToKey(boardConfig.properties.values.$ref)];
  const data = selectedSource.default?.data.defaults[selectedSource.source.board]
    .values as any;

  const ctx: TraversalContext = {
    defs: d.$defs,
    rootData: data,
    onChange: onChange ?? (() => {}),
    ownerSchema: null,
    path: [],
    data: data,
  };

  const graph = handleNode(ctx, boardValues);
  return graph;
};

export const validateSource = (source: SelectedSouce) => {
  const def = source.default;
  if (!def) throw 'no schema or data';

  const ajv = new Ajv2020({ discriminator: true, verbose: true, allErrors: true });
  return ajv.validate(def.schema as any, source.default?.data);
};
