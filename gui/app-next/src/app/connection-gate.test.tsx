import { cleanup, render, screen } from '@solidjs/testing-library';
import {
  ErrorReportingConsent,
  ErrorReportingSettingsResponseT,
} from 'solarxr-protocol';
import { afterEach, beforeAll, describe, expect, it } from 'vitest';
import { ConnectionGate } from '@/app/connection-gate';
import { setPlatform } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';
import { setClient } from '@/solarxr/client';
import { createFakeClient, type FakeClient } from '@/solarxr/testing';

let fake: FakeClient;

beforeAll(() => {
  setPlatform(fakePlatform());
  fake = createFakeClient({ connected: false });
  setClient(fake);
});

function consent(value: ErrorReportingConsent) {
  fake.push(new ErrorReportingSettingsResponseT(value, 'user-1', 'session-1'));
}

function renderGate() {
  render(() => (
    <ConnectionGate>
      <p>app</p>
    </ConnectionGate>
  ));
}

// Resources are module state, so the cases run as one story in order
describe('ConnectionGate', () => {
  afterEach(cleanup);

  it('shows the timeout error when the first connect takes too long', () => {
    renderGate();
    expect(screen.getByText('websocket-connecting')).toBeInTheDocument();
    fake.setTimedOut(true);
    expect(screen.getByText('websocket-timedout')).toBeInTheDocument();
    expect(screen.queryByText('app')).toBeNull();
    fake.setTimedOut(false);
  });

  it('waits for consent, asks it, then shows the app and covers it on loss', () => {
    renderGate();
    fake.connect();
    expect(screen.getByText('websocket-connecting')).toBeInTheDocument();
    expect(screen.queryByText('app')).toBeNull();

    consent(ErrorReportingConsent.UNDECIDED);
    expect(
      screen.getByText('error_collection_modal-title')
    ).toBeInTheDocument();
    expect(screen.queryByText('app')).toBeNull();

    consent(ErrorReportingConsent.ALLOWED);
    expect(screen.queryByText('error_collection_modal-title')).toBeNull();
    expect(screen.getByText('app').parentElement).not.toHaveClass('hidden');

    fake.disconnect();
    expect(screen.getByText('websocket-connection_lost')).toBeInTheDocument();
    // Kept mounted under the screen
    expect(screen.getByText('app').parentElement).toHaveClass('hidden');

    fake.connect();
    consent(ErrorReportingConsent.ALLOWED);
    expect(screen.queryByText('websocket-connection_lost')).toBeNull();
    expect(screen.getByText('app').parentElement).not.toHaveClass('hidden');
  });
});
