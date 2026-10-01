import { useLocalization } from '@fluent/react';
import { useState, useEffect, ChangeEvent } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { DropdownInside, DropdownItem } from '@/components/commons/Dropdown';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { InputInside } from '@/components/commons/Input';
import { StatusBadge, StatusRow } from '@/components/commons/StatusBadge';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import classNames from 'classnames';

export interface CustomOscParamMapping {
  axis: string;
  address: string;
}

export interface CustomOscTrackerMapping {
  bodyPart: string;
  params: CustomOscParamMapping[];
}

export interface CustomOscProfile {
  id: string;
  name: string;
  enabled: boolean;
  address: string;
  port: number;
  trackers: CustomOscTrackerMapping[];
}

export interface CustomOscConfig {
  enabled: boolean;
  profiles: CustomOscProfile[];
}

const BODY_PART_OPTIONS: DropdownItem[] = [
  { value: 'HEAD', label: 'Head' },
  { value: 'NECK', label: 'Neck' },
  { value: 'UPPER_CHEST', label: 'Upper Chest' },
  { value: 'LOWER_CHEST', label: 'Lower Chest' },
  { value: 'UPPER_WAIST', label: 'Upper Waist' },
  { value: 'LOWER_WAIST', label: 'Lower Waist' },
  { value: 'HIP', label: 'Hip' },
  { value: 'LEFT_SHOULDER', label: 'Left Shoulder' },
  { value: 'RIGHT_SHOULDER', label: 'Right Shoulder' },
  { value: 'LEFT_UPPER_ARM', label: 'Left Upper Arm' },
  { value: 'RIGHT_UPPER_ARM', label: 'Right Upper Arm' },
  { value: 'LEFT_LOWER_ARM', label: 'Left Lower Arm' },
  { value: 'RIGHT_LOWER_ARM', label: 'Right Lower Arm' },
  { value: 'LEFT_HAND', label: 'Left Hand' },
  { value: 'RIGHT_HAND', label: 'Right Hand' },
  { value: 'LEFT_UPPER_LEG', label: 'Left Upper Leg' },
  { value: 'RIGHT_UPPER_LEG', label: 'Right Upper Leg' },
  { value: 'LEFT_LOWER_LEG', label: 'Left Lower Leg' },
  { value: 'RIGHT_LOWER_LEG', label: 'Right Lower Leg' },
  { value: 'LEFT_FOOT', label: 'Left Foot' },
  { value: 'RIGHT_FOOT', label: 'Right Foot' },
];

const AXIS_OPTIONS: DropdownItem[] = [
  { value: 'POSITION_X', label: 'Position X (m)' },
  { value: 'POSITION_Y', label: 'Position Y (m)' },
  { value: 'POSITION_Z', label: 'Position Z (m)' },
  { value: 'ROTATION_PITCH', label: 'Rotation Pitch (°)' },
  { value: 'ROTATION_YAW', label: 'Rotation Yaw (°)' },
  { value: 'ROTATION_ROLL', label: 'Rotation Roll (°)' },
  { value: 'QUAT_X', label: 'Quaternion X' },
  { value: 'QUAT_Y', label: 'Quaternion Y' },
  { value: 'QUAT_Z', label: 'Quaternion Z' },
  { value: 'QUAT_W', label: 'Quaternion W' },
];

const LOCAL_STORAGE_KEY = 'slimevr_custom_osc_config';

export function CustomOSCSettings() {
  const { l10n } = useLocalization();
  const [config, setConfig] = useState<CustomOscConfig>(() => {
    const saved = localStorage.getItem(LOCAL_STORAGE_KEY);
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (_) { }
    }
    return {
      enabled: true,
      profiles: [
        {
          id: 'profile-default',
          name: 'My Custom Profile',
          enabled: true,
          address: '127.0.0.1',
          port: 9000,
          trackers: [
            {
              bodyPart: 'UPPER_CHEST',
              params: [
                { axis: 'ROTATION_PITCH', address: '/slimevr/chest/pitch' },
                { axis: 'ROTATION_YAW', address: '/slimevr/chest/yaw' },
                { axis: 'ROTATION_ROLL', address: '/slimevr/chest/roll' },
              ],
            },
          ],
        },
      ],
    };
  });

  const [selectedProfileId, setSelectedProfileId] = useState<string | null>(() => {
    return config.profiles.length > 0 ? config.profiles[0].id : null;
  });

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(config));
  }, [config]);

  const activeSelectedProfileId =
    selectedProfileId && config.profiles.some((p) => p.id === selectedProfileId)
      ? selectedProfileId
      : config.profiles.length > 0
      ? config.profiles[0].id
      : null;

  const focusedProfile = config.profiles.find((p) => p.id === activeSelectedProfileId) || null;

  const toggleGlobalEnable = (enabled: boolean) => {
    setConfig((prev) => ({ ...prev, enabled }));
  };

  const toggleProfileEnable = (profileId: string) => {
    setConfig((prev) => ({
      ...prev,
      profiles: prev.profiles.map((p) =>
        p.id === profileId ? { ...p, enabled: !p.enabled } : p
      ),
    }));
  };

  const handleDeleteProfile = (profileId: string) => {
    setConfig((prev) => {
      const remaining = prev.profiles.filter((p) => p.id !== profileId);
      return { ...prev, profiles: remaining };
    });
  };

  const handleAddProfile = () => {
    const newId = `profile-${Date.now()}`;
    const newProfile: CustomOscProfile = {
      id: newId,
      name: `Custom Profile ${config.profiles.length + 1}`,
      enabled: true,
      address: '127.0.0.1',
      port: 9000,
      trackers: [
        {
          bodyPart: 'UPPER_CHEST',
          params: [
            { axis: 'ROTATION_PITCH', address: '/slimevr/chest/pitch' },
            { axis: 'ROTATION_YAW', address: '/slimevr/chest/yaw' },
            { axis: 'ROTATION_ROLL', address: '/slimevr/chest/roll' },
          ],
        },
      ],
    };
    setConfig((prev) => ({
      ...prev,
      profiles: [...prev.profiles, newProfile],
    }));
    setSelectedProfileId(newId);
  };

  const updateFocusedProfile = (updater: (prev: CustomOscProfile) => CustomOscProfile) => {
    if (!activeSelectedProfileId) return;
    setConfig((prev) => ({
      ...prev,
      profiles: prev.profiles.map((p) => (p.id === activeSelectedProfileId ? updater(p) : p)),
    }));
  };

  const handleExportProfile = (profile: CustomOscProfile) => {
    const jsonStr = JSON.stringify(profile, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${profile.name.toLowerCase().replace(/[^a-z0-9]/g, '_')}_custom_osc.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleImportProfile = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (evt) => {
      try {
        const imported = JSON.parse(evt.target?.result as string);
        if (imported.name && Array.isArray(imported.trackers)) {
          const newId = `profile-${Date.now()}`;
          const newProfile: CustomOscProfile = {
            ...imported,
            id: newId,
          };
          setConfig((prev) => ({
            ...prev,
            profiles: [...prev.profiles, newProfile],
          }));
          setSelectedProfileId(newId);
        }
      } catch (err) {
        console.error('Failed to parse imported custom OSC profile', err);
      }
    };
    reader.readAsText(file);
    e.target.value = '';
  };

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout
        icon={
          <div className="w-6 h-6 rounded bg-accent flex items-center justify-center text-xs font-bold text-background-10">
            OSC
          </div>
        }
        id="custom"
      >
        <Typography variant="main-title">
          {l10n.getString('settings-osc-custom')}
        </Typography>
        <div className="flex flex-col pt-2 pb-4">
          <Typography>
            {l10n.getString('settings-osc-custom-description')}
          </Typography>
        </div>

        <Typography variant="section-title">
          {l10n.getString('settings-osc-custom-enable')}
        </Typography>
        <div className="flex flex-col pb-2">
          <Typography>
            {l10n.getString('settings-osc-custom-enable-description')}
          </Typography>
        </div>
        <div className="grid sm:grid-cols-2 gap-3 pb-5">
          <CheckboxInternal
            name="global-enable"
            variant="toggle"
            outlined
            label="Enable"
            checked={config.enabled}
            onChange={(e: ChangeEvent<HTMLInputElement>) => toggleGlobalEnable(e.target.checked)}
          />
        </div>

        {/* Status Section */}
        <Typography variant="section-title">
          Status
        </Typography>
        <div className="flex flex-col bg-background-80 px-4 py-2 mb-5 rounded-md divide-y divide-background-60">
          <StatusRow
            label={<Typography variant="section-title">Output</Typography>}
            badge={
              <StatusBadge
                variant={config.enabled ? 'success' : 'neutral'}
                id={config.enabled ? 'Ready' : 'Disabled'}
              />
            }
          >
            <Typography color="secondary">
              {config.enabled
                ? `Sending custom OSC to ${config.profiles.filter((p) => p.enabled).length} active profile(s).`
                : 'Custom OSC output disabled.'}
            </Typography>
          </StatusRow>
        </div>

        <Typography variant="section-title">
          {l10n.getString('settings-osc-custom-profiles')}
        </Typography>

        {/* Master-Detail Side-by-Side Panel */}
        <div className="flex gap-4 mt-2 items-start min-h-[500px]">
          {/* Left Column: List Box of Profiles */}
          <div className="w-80 flex flex-col gap-3 shrink-0">
            <div className="flex gap-2">
              <Button variant="primary" className="flex-1 text-xs py-1.5" onClick={handleAddProfile}>
                + Add
              </Button>
              <label className="flex-1 text-center cursor-pointer bg-background-60 hover:bg-background-50 text-background-10 py-1.5 px-2 rounded text-xs font-medium transition-colors flex items-center justify-center">
                {l10n.getString('settings-osc-custom-import-profile')}
                <input
                  type="file"
                  accept=".json"
                  className="hidden"
                  onChange={handleImportProfile}
                />
              </label>
            </div>

            <div className="flex flex-col gap-2 max-h-[550px] overflow-y-auto pr-1">
              {config.profiles.length === 0 ? (
                <div className="p-2 text-center text-xs bg-background-60 rounded-lg">
                  <Typography variant="standard" italic color="secondary">
                    {l10n.getString('settings-osc-custom-no-profiles')}
                  </Typography>
                </div>
              ) : (
                config.profiles.map((profile) => {
                  const isSelected = profile.id === activeSelectedProfileId;
                  return (
                    <div
                      key={profile.id}
                      onClick={() => setSelectedProfileId(profile.id)}
                      className={classNames(
                        'flex items-center justify-between p-3 rounded-lg cursor-pointer transition-all',
                        isSelected
                          ? 'bg-background-50 shadow-sm'
                          : 'bg-background-60 hover:bg-background-50/60'
                      )}
                    >
                      <div className="flex items-center gap-3 min-w-0 flex-1 pr-2">
                        <div className="shrink-0">
                          <CheckboxInternal
                            name={`profile-enable-${profile.id}`}
                            variant="toggle"
                            checked={profile.enabled}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => {
                              e.stopPropagation();
                              toggleProfileEnable(profile.id);
                            }}
                          />
                        </div>
                        <div className="min-w-0 flex-1 overflow-hidden">
                          <Typography variant="standard" bold whitespace="whitespace-nowrap" truncate>
                            {profile.name}
                          </Typography>
                          <div className="text-xs">
                            <Typography variant="standard" color="secondary" whitespace="whitespace-nowrap" truncate>
                              {profile.address}:{profile.port}
                            </Typography>
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>

          {/* Right Column: Focused Profile Editor Panel */}
          <div className="flex-1 p-5 bg-background-70 rounded-lg flex flex-col gap-5">
            {focusedProfile ? (
              <>
                <div className="flex justify-between items-center pb-3">
                  <div>
                    <Typography variant="main-title">{focusedProfile.name}</Typography>
                    <div className="text-xs">
                      <Typography variant="standard" color="secondary">
                        Target: {focusedProfile.address}:{focusedProfile.port} | Trackers: {focusedProfile.trackers.length}
                      </Typography>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="secondary" onClick={() => handleExportProfile(focusedProfile)}>
                      {l10n.getString('settings-osc-custom-export-profile')}
                    </Button>
                    <Button variant="tertiary" onClick={() => handleDeleteProfile(focusedProfile.id)}>
                      {l10n.getString('settings-osc-custom-delete')}
                    </Button>
                  </div>
                </div>

                {/* Network & Profile Name Fields */}
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <InputInside
                      label={l10n.getString('settings-osc-custom-profile-name')}
                      name="profile-name"
                      type="text"
                      value={focusedProfile.name}
                      onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        updateFocusedProfile((prev) => ({ ...prev, name: e.target.value }))
                      }
                    />
                  </div>

                  <div>
                    <InputInside
                      label={l10n.getString('settings-osc-custom-address')}
                      name="profile-address"
                      type="text"
                      value={focusedProfile.address}
                      onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        updateFocusedProfile((prev) => ({ ...prev, address: e.target.value }))
                      }
                    />
                  </div>

                  <div>
                    <InputInside
                      label={l10n.getString('settings-osc-custom-port')}
                      name="profile-port"
                      type="number"
                      value={focusedProfile.port.toString()}
                      onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        updateFocusedProfile((prev) => ({
                          ...prev,
                          port: parseInt(e.target.value, 10) || 9000,
                        }))
                      }
                    />
                  </div>
                </div>

                {/* Tracker Mappings Section */}
                <div className="flex flex-col gap-3 mt-2">
                  <div className="flex justify-between items-center">
                    <Typography variant="section-title">Tracker Mappings</Typography>
                    <Button
                      variant="secondary"
                      className="text-xs"
                      onClick={() =>
                        updateFocusedProfile((prev) => ({
                          ...prev,
                          trackers: [
                            ...prev.trackers,
                            { bodyPart: 'UPPER_CHEST', params: [] },
                          ],
                        }))
                      }
                    >
                      + {l10n.getString('settings-osc-custom-add-tracker')}
                    </Button>
                  </div>

                  {focusedProfile.trackers.length === 0 ? (
                    <div className="p-4 text-center rounded-lg">
                      <Typography variant="standard" italic color="secondary">
                        No tracker mappings configured yet. Click "+ Add Tracker Mapping" above.
                      </Typography>
                    </div>
                  ) : (
                    focusedProfile.trackers.map((tracker, tIdx) => (
                      <div
                        key={tIdx}
                        className="py-3 flex flex-col gap-3"
                      >
                        <div className="flex justify-between items-center pb-2">
                          <div className="w-64">
                            <DropdownInside
                              name={`tracker-bodypart-${tIdx}`}
                              display="block"
                              placeholder="Select Tracker Role"
                              items={BODY_PART_OPTIONS}
                              value={tracker.bodyPart}
                              onChange={(val: string) => {
                                updateFocusedProfile((prev) => {
                                  const updatedTrackers = [...prev.trackers];
                                  updatedTrackers[tIdx].bodyPart = val;
                                  return { ...prev, trackers: updatedTrackers };
                                });
                              }}
                            />
                          </div>
                          <Button
                            variant="tertiary"
                            className="text-xs text-red-400 hover:text-red-300"
                            onClick={() => {
                              updateFocusedProfile((prev) => ({
                                ...prev,
                                trackers: prev.trackers.filter((_, i) => i !== tIdx),
                              }));
                            }}
                          >
                            Remove Tracker
                          </Button>
                        </div>

                        {/* Parameter Mapping Rows */}
                        <div className="flex flex-col gap-2 mt-1">
                          {tracker.params.map((param, pIdx) => (
                            <div key={pIdx} className="flex gap-3 items-center">
                              <div className="w-1/2">
                                <DropdownInside
                                  name={`axis-${tIdx}-${pIdx}`}
                                  display="block"
                                  placeholder="Select Axis"
                                  items={AXIS_OPTIONS}
                                  value={param.axis}
                                  onChange={(val: string) => {
                                    updateFocusedProfile((prev) => {
                                      const updatedTrackers = [...prev.trackers];
                                      updatedTrackers[tIdx].params[pIdx].axis = val;
                                      return { ...prev, trackers: updatedTrackers };
                                    });
                                  }}
                                />
                              </div>

                              <div className="w-1/2">
                                <InputInside
                                  name={`param-address-${tIdx}-${pIdx}`}
                                  label={AXIS_OPTIONS.find((opt) => opt.value === param.axis)?.label as string ?? 'OSC Address'}
                                  placeholder="/slimevr/chest/pitch"
                                  value={param.address}
                                  onChange={(e: ChangeEvent<HTMLInputElement>) => {
                                    updateFocusedProfile((prev) => {
                                      const updatedTrackers = [...prev.trackers];
                                      updatedTrackers[tIdx].params[pIdx].address = e.target.value;
                                      return { ...prev, trackers: updatedTrackers };
                                    });
                                  }}
                                />
                              </div>

                              <Button
                                variant="tertiary"
                                className="px-2 text-xs"
                                onClick={() => {
                                  updateFocusedProfile((prev) => {
                                    const updatedTrackers = [...prev.trackers];
                                    updatedTrackers[tIdx].params = updatedTrackers[tIdx].params.filter(
                                      (_, i) => i !== pIdx
                                    );
                                    return { ...prev, trackers: updatedTrackers };
                                  });
                                }}
                              >
                                ✕
                              </Button>
                            </div>
                          ))}

                          <Button
                            variant="tertiary"
                            className="self-start text-xs mt-1"
                            onClick={() => {
                              updateFocusedProfile((prev) => {
                                const updatedTrackers = [...prev.trackers];
                                updatedTrackers[tIdx].params.push({
                                  axis: 'ROTATION_PITCH',
                                  address: `/slimevr/${tracker.bodyPart.toLowerCase()}/pitch`,
                                });
                                return { ...prev, trackers: updatedTrackers };
                              });
                            }}
                          >
                            + {l10n.getString('settings-osc-custom-add-param')}
                          </Button>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </>
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-center py-20">
                <Typography variant="standard" italic color="secondary">
                  Select a profile from the left list box, or click "+ Add" to create a new profile.
                </Typography>
              </div>
            )}
          </div>
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
