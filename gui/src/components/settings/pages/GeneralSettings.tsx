import { useEffect, useRef } from 'react';
import { DefaultValues, useForm } from 'react-hook-form';
import { useLocation } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  FilteringSettingsT,
  FilteringType,
  ModelSettingsT,
  ModelTogglesT,
  TapDetectionSettingsT,
  LegTweaksSettingsT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  SteamVRTrackersSettingT
} from 'solarxr-protocol';
import { useConfig } from '../../../hooks/config';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { CheckBox } from '../../commons/Checkbox';
import { SquaresIcon } from '../../commons/icon/SquaresIcon';
import { SteamIcon } from '../../commons/icon/SteamIcon';
import { WrenchIcon } from '../../commons/icon/WrenchIcons';
import { NumberSelector } from '../../commons/NumberSelector';
import { Radio } from '../../commons/Radio';
import { Typography } from '../../commons/Typography';
import { SettingsPageLayout } from '../SettingsPageLayout';

interface SettingsForm {
  trackers: {
    waist: boolean;
    chest: boolean;
    feet: boolean;
    knees: boolean;
    elbows: boolean;
  };
  filtering: {
    type: number;
    amount: number;
  };
  toggles: {
    extendedSpine: boolean;
    extendedPelvis: boolean;
    extendedKnee: boolean;
    forceArmsFromHmd: boolean;
    floorClip: boolean;
    skatingCorrection: boolean;
  };
  tapDetection: {
    enabled: boolean;
    delay: number;
  };
  legTweaks: {
    amount: number;
  };
  interface: {
    devmode: boolean;
    watchNewDevices: boolean;
  };
}

export function GeneralSettings() {
  const { config, setConfig } = useConfig();
  const { state } = useLocation();
  const pageRef = useRef<HTMLFormElement | null>(null);

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const { reset, control, watch, handleSubmit } = useForm<SettingsForm>({
    defaultValues: {
      trackers: {
        waist: false,
        chest: false,
        elbows: false,
        knees: false,
        feet: false,
      },
      toggles: {
        extendedSpine: true,
        extendedPelvis: true,
        extendedKnee: true,
        forceArmsFromHmd: false,
        floorClip: false,
        skatingCorrection: false,
      },
      filtering: { amount: 0.1, type: FilteringType.NONE },
      tapDetection: { enabled: false, delay: 0.2 },
      legTweaks: { amount: 0.3 },
      interface: { devmode: false, watchNewDevices: true },
    },
  });

  const onSubmit = (values: SettingsForm) => {
    const settings = new ChangeSettingsRequestT();

    if (values.trackers) {
      const trackers = new SteamVRTrackersSettingT();
      trackers.waist = values.trackers.waist;
      trackers.chest = values.trackers.chest;
      trackers.feet = values.trackers.feet;
      trackers.knees = values.trackers.knees;
      trackers.elbows = values.trackers.elbows;
      settings.steamVrTrackers = trackers;
    }

    const modelSettings = new ModelSettingsT();
    const toggles = new ModelTogglesT();
    const legTweaks = new LegTweaksSettingsT();
    toggles.floorClip = values.toggles.floorClip;
    toggles.skatingCorrection = values.toggles.skatingCorrection;
    toggles.extendedKnee = values.toggles.extendedKnee;
    toggles.extendedPelvis = values.toggles.extendedPelvis;
    toggles.extendedSpine = values.toggles.extendedSpine;
    toggles.forceArmsFromHmd = values.toggles.forceArmsFromHmd;

    modelSettings.toggles = toggles;
    modelSettings.legTweaks = legTweaks;
    settings.modelSettings = modelSettings;

    const tapDetection = new TapDetectionSettingsT();
    tapDetection.enabled = values.tapDetection.enabled;
    tapDetection.delay = values.tapDetection.delay;
    settings.tapDetection = tapDetection;

    const filtering = new FilteringSettingsT();
    filtering.type = values.filtering.type;
    filtering.amount = values.filtering.amount;
    settings.filtering = filtering;

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);

    setConfig({
      debug: values.interface.devmode,
      watchNewDevices: values.interface.watchNewDevices,
    });

    // if devmode was changed update the page
    const skeletonSettings = document.getElementById('skeletonSettings');
    if (skeletonSettings !== null) {
      skeletonSettings.style.display = values.interface.devmode
        ? 'block'
        : 'none';
    }
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    const formData: DefaultValues<SettingsForm> = {
      interface: {
        devmode: config?.debug,
        watchNewDevices: config?.watchNewDevices,
      },
    };

    if (settings.filtering) {
      formData.filtering = settings.filtering;
    }

    if (settings.steamVrTrackers) {
      formData.trackers = settings.steamVrTrackers;
    }

    if (settings.modelSettings?.toggles) {
      formData.toggles = Object.keys(settings.modelSettings?.toggles).reduce(
        (curr, key: string) => ({
          ...curr,
          [key]:
            (settings.modelSettings?.toggles &&
              (settings.modelSettings.toggles as any)[key]) ||
            false,
        }),
        {}
      );
    }

    if (settings.tapDetection) {
      formData.tapDetection = settings.tapDetection;
    }

    if (settings.modelSettings?.legTweaks) {
      formData.legTweaks = settings.modelSettings.legTweaks;
    }

    reset(formData);
  });

  // Handle scrolling to selected page
  useEffect(() => {
    const typedState: { scrollTo: string } = state as any;
    if (!pageRef.current || !typedState || !typedState.scrollTo) {
      return;
    }
    const elem = pageRef.current.querySelector(`#${typedState.scrollTo}`);
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth' });
    }
  }, [state]);

  return (
    <form className="flex flex-col gap-2 w-full" ref={pageRef}>
      <SettingsPageLayout icon={<SteamIcon></SteamIcon>} id="steamvr">
        <>
          <Typography variant="main-title">SteamVR</Typography>
          <Typography bold>SteamVR trackers</Typography>
          <div className="flex flex-col py-2">
            <Typography color="secondary">
              Enable or disable specific tracking parts.
            </Typography>
            <Typography color="secondary">
              Useful if you want more control over what SlimeVR does.
            </Typography>
          </div>
          <div className="grid grid-cols-2 gap-3 pt-3">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.waist"
              label="Waist"
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.chest"
              label="Chest"
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.feet"
              label="Feet"
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.knees"
              label="Knees"
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="trackers.elbows"
              label="Elbows"
            />
          </div>
        </>
      </SettingsPageLayout>
      <SettingsPageLayout icon={<WrenchIcon></WrenchIcon>} id="mechanics">
        <>
          <Typography variant="main-title">Tracker mechanics</Typography>
          <Typography bold>Filtering</Typography>
          <div className="flex flex-col pt-2 pb-4">
            <Typography color="secondary">
              Choose the filtering type for your trackers.
            </Typography>
            <Typography color="secondary">
              Prediction predicts movement while smoothing smoothens movement.
            </Typography>
          </div>
          <Typography>Filtering type</Typography>
          <div className="flex md:flex-row flex-col gap-3 pt-2">
            <Radio
              control={control}
              name="filtering.type"
              label="No filtering"
              desciption="Use rotations as is. Will not do any filtering."
              value={FilteringType.NONE}
            ></Radio>
            <Radio
              control={control}
              name="filtering.type"
              label="Smoothing"
              desciption="Smooths movements but adds some latency."
              value={FilteringType.SMOOTHING}
            ></Radio>
            <Radio
              control={control}
              name="filtering.type"
              label="Prediction"
              desciption="Reduces latency and makes movements more snappy, but may increase jitter."
              value={FilteringType.PREDICTION}
            ></Radio>
          </div>
          <div className="flex gap-5 pt-5 md:flex-row flex-col">
            <NumberSelector
              control={control}
              name="filtering.amount"
              label="Amount"
              valueLabelFormat={(value) => `${Math.round(value * 100)} %`}
              min={0.1}
              max={1.0}
              step={0.1}
            />
          </div>
        </>
      </SettingsPageLayout>
      <SettingsPageLayout icon={<WrenchIcon></WrenchIcon>} id="fksettings">
        <>
          <Typography variant="main-title">FK settings</Typography>
          <Typography bold>Leg tweaks</Typography>
          <div className="flex flex-col pt-2 pb-4">
            <Typography color="secondary">
              Floor-clip can Reduce or even eliminates clipping with the floor
              but may cause problems when on your knees. Skating-correction
              corrects for ice skating, but can decrease accuracy in certain
              movement patterns.
            </Typography>
          </div>
          <div className="grid sm:grid-cols-2 gap-3 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="toggles.floorClip"
              label="Floor clip"
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="toggles.skatingCorrection"
              label="Skating correction"
            />
          </div>

          <Typography bold>Arm FK</Typography>
          <div className="flex flex-col pt-2 pb-4">
            <Typography color="secondary">
              Change the way the arms are tracked.
            </Typography>
          </div>
          <div className="grid sm:grid-cols-2 pb-5">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="toggles.forceArmsFromHmd"
              label="Force arms from HMD"
            />
          </div>
          <div id="skeletonSettings">
            <Typography bold>Skeleton settings</Typography>
            <div className="flex flex-col pt-2 pb-4">
              <Typography color="secondary">
                Toggle skeleton settings on or off. It is recommended to leave
                these on.
              </Typography>
            </div>
            <div className="grid sm:grid-cols-2 gap-3 pb-5">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.extendedSpine"
                label="Extended spine"
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.extendedPelvis"
                label="Extended pelvis"
              />
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="toggles.extendedKnee"
                label="Extended knee"
              />
            </div>
          </div>
        </>
      </SettingsPageLayout>

      <SettingsPageLayout icon={<SquaresIcon></SquaresIcon>} id="interface">
        <>
          <Typography variant="main-title">Interface</Typography>
          <div className="gap-4 grid">
            <div className="grid sm:grid-cols-2">
              <div>
                <Typography bold>Developer Mode</Typography>
                <div className="flex flex-col">
                  <Typography color="secondary">
                    This mode can be useful if you need in-depth data or to
                    interact with connected trackers on a more advanced level
                  </Typography>
                </div>
                <div className="pt-2">
                  <CheckBox
                    variant="toggle"
                    control={control}
                    outlined
                    name="interface.devmode"
                    label="Developer mode"
                  />
                </div>
              </div>
            </div>
            <div className="grid sm:grid-cols-2">
              <div>
                <Typography bold>Serial device detection</Typography>
                <div className="flex flex-col">
                  <Typography color="secondary">
                    This option will show a pop-up every time you plug a new
                    serial device that could be a tracker. It helps improving
                    the setup process of a tracker
                  </Typography>
                </div>
                <div className="pt-2">
                  <CheckBox
                    variant="toggle"
                    control={control}
                    outlined
                    name="interface.watchNewDevices"
                    label="Serial device detection"
                  />
                </div>
              </div>
            </div>
          </div>
        </>
      </SettingsPageLayout>
    </form>
  );
}
