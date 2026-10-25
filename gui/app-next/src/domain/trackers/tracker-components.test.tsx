import { cleanup, render, screen } from '@solidjs/testing-library';
import {
  DataFeedUpdateT,
  DeviceDataT,
  HardwareStatusT,
  TrackerDataT,
  TrackerInfoT,
  TrackerStatus,
} from 'solarxr-protocol';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { debugPref } from '@/domain/developer';
import { createBundle, setL10nBundles, t } from '@/lib/l10n';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import {
  ingestTrackers,
  resetTrackers,
  TrackerBattery,
  trackerDisplayPref,
  trackerKey,
  TrackerStatusLabel,
  TrackerWifi,
} from '.';

const KEY = trackerKey(1, 0);

function feed(
  hw: Partial<HardwareStatusT>,
  status: TrackerStatus = TrackerStatus.OK
) {
  const tracker = new TrackerDataT();
  tracker.deviceId = 1;
  tracker.trackerId = 0;
  tracker.info = new TrackerInfoT();
  tracker.status = status;
  const device = new DeviceDataT();
  device.id = 1;
  device.hardwareStatus = Object.assign(new HardwareStatusT(), hw);
  device.trackers = [tracker];
  const update = new DataFeedUpdateT();
  update.devices = [device];
  ingestTrackers(update, 0);
}

beforeEach(() => {
  setPlatform(fakePlatform());
  resetTrackers();
  debugPref.set(false);
  trackerDisplayPref.set({
    showBatteryVoltage: false,
    showNumericSignal: false,
  });
  setL10nBundles([
    createBundle(
      'en',
      [
        'tracker-status-ok = OK',
        'tracker-battery-runtime = { $hours }h { $minutes }min',
        'tracker-infos-packet_loss = Packet Loss',
      ].join('\n')
    ),
  ]);
});

afterEach(cleanup);

describe('TrackerBattery', () => {
  it('renders nothing without a battery estimate', () => {
    feed({ batteryPctEstimate: null });
    const { container } = render(() => <TrackerBattery trackerKey={KEY} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('shows the percent when there is no runtime', () => {
    feed({ batteryPctEstimate: 55, batteryVoltage: 3.9 });
    render(() => <TrackerBattery trackerKey={KEY} />);
    expect(screen.getByText('55%')).toBeInTheDocument();
  });

  it('shows the runtime, the percent moves to the tooltip', () => {
    feed({
      batteryPctEstimate: 55,
      batteryVoltage: 3.9,
      batteryRuntimeEstimate: 2n * 3_600_000_000n + 5n * 60_000_000n,
    });
    render(() => <TrackerBattery trackerKey={KEY} />);
    expect(
      screen.getByText(t('tracker-battery-runtime', { hours: 2, minutes: 5 }))
    ).toBeInTheDocument();
    expect(screen.getByText('55%')).toHaveAttribute('role', 'tooltip');
  });

  it('debug shows both runtime and percent', () => {
    debugPref.set(true);
    feed({ batteryPctEstimate: 55, batteryRuntimeEstimate: 3_600_000_000n });
    render(() => <TrackerBattery trackerKey={KEY} />);
    expect(
      screen.getByText(t('tracker-battery-runtime', { hours: 1, minutes: 0 }))
    ).toBeInTheDocument();
    expect(screen.getByText('55%')).not.toHaveAttribute('role', 'tooltip');
  });

  it('hides the numbers while charging, unless the voltage pref is on', () => {
    feed({ batteryPctEstimate: 80, batteryVoltage: 4.4 });
    render(() => <TrackerBattery trackerKey={KEY} />);
    expect(screen.queryByText('80%')).not.toBeInTheDocument();
    expect(screen.queryByText('4.40 V')).not.toBeInTheDocument();
    trackerDisplayPref.set({
      showBatteryVoltage: true,
      showNumericSignal: false,
    });
    expect(screen.getByText('4.40 V')).toBeInTheDocument();
  });
});

describe('TrackerWifi', () => {
  it('shows ping, and dBm only when numeric', () => {
    feed({ rssi: -60, ping: 12 });
    render(() => <TrackerWifi trackerKey={KEY} />);
    expect(screen.getByText('12 ms')).toBeInTheDocument();
    expect(screen.queryByText('-60 dBm')).not.toBeInTheDocument();
    trackerDisplayPref.set({
      showBatteryVoltage: false,
      showNumericSignal: true,
    });
    expect(screen.getByText('-60 dBm')).toBeInTheDocument();
  });

  it('hides the text while disconnected', () => {
    feed({ rssi: -60, ping: 12 }, TrackerStatus.DISCONNECTED);
    render(() => <TrackerWifi trackerKey={KEY} numeric />);
    expect(screen.queryByText('12 ms')).not.toBeInTheDocument();
    expect(screen.queryByText('-60 dBm')).not.toBeInTheDocument();
  });

  it('shows packet loss with counts', () => {
    feed({ ping: 12, packetLoss: 0.25, packetsLost: 5, packetsReceived: 20 });
    render(() => <TrackerWifi trackerKey={KEY} packetLoss />);
    expect(screen.getByText('25% (5 / 20)')).toBeInTheDocument();
  });

  it('shows -- for unknown packet loss', () => {
    feed({ ping: 12, packetLoss: null });
    render(() => <TrackerWifi trackerKey={KEY} packetLoss />);
    expect(screen.getByText('--')).toBeInTheDocument();
  });
});

describe('TrackerStatusLabel', () => {
  it('shows the status name', () => {
    render(() => <TrackerStatusLabel status={TrackerStatus.OK} />);
    expect(screen.getByText('OK')).toBeInTheDocument();
  });
});
