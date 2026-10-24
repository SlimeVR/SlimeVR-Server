import { render, screen } from '@solidjs/testing-library';
import { expect, test } from 'vitest';
import { App } from '@/app/app';

test('renders the placeholder root', () => {
  render(() => <App />);
  expect(screen.getByRole('main')).toHaveTextContent('SlimeVR GUI next');
});
