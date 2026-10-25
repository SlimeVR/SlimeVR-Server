import { MemoryRouter, Route, useLocation } from '@solidjs/router';
import { cleanup, render, screen } from '@solidjs/testing-library';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { Button } from '@/ui/button';

describe('Button', () => {
  afterEach(cleanup);

  it('shows its label and fires onClick', async () => {
    const onClick = vi.fn();
    render(() => <Button label="dialog-cancel" onClick={onClick} />);
    await userEvent
      .setup()
      .click(screen.getByRole('button', { name: 'dialog-cancel' }));
    expect(onClick).toHaveBeenCalledOnce();
  });

  it('disabled stays focusable, ignores presses, describes the reason', async () => {
    const onClick = vi.fn();
    render(() => (
      <Button
        label="dialog-cancel"
        disabled
        disabledReason="status-loading"
        onClick={onClick}
      />
    ));
    const button = screen.getByRole('button');
    expect(button).toHaveAttribute('aria-disabled', 'true');
    button.focus();
    expect(button).toHaveFocus();
    await userEvent.setup().click(button);
    expect(onClick).not.toHaveBeenCalled();
    expect(button).toHaveAccessibleDescription('status-loading');
  });

  it('ignores presses while loading', async () => {
    const onClick = vi.fn();
    render(() => <Button label="dialog-cancel" loading onClick={onClick} />);
    await userEvent.setup().click(screen.getByRole('button'));
    expect(onClick).not.toHaveBeenCalled();
  });

  it('icon only: the label is the name, not visible text', () => {
    render(() => <Button label="dialog-cancel" icon={<span />} iconOnly />);
    const button = screen.getByRole('button', { name: 'dialog-cancel' });
    expect(button).not.toHaveTextContent('dialog-cancel');
    expect(screen.getByRole('tooltip', { hidden: true })).toHaveTextContent(
      'dialog-cancel'
    );
  });

  it('a disabled submit does not submit', async () => {
    const onSubmit = vi.fn((e: SubmitEvent) => e.preventDefault());
    render(() => (
      <form onSubmit={onSubmit}>
        <Button label="dialog-cancel" type="submit" disabled />
      </form>
    ));
    await userEvent.setup().click(screen.getByRole('button'));
    expect(onSubmit).not.toHaveBeenCalled();
  });

  describe('link', () => {
    function Path() {
      const location = useLocation();
      return <p data-testid="path">{location.pathname}</p>;
    }

    function renderLink(disabled = false) {
      render(() => (
        <MemoryRouter
          root={(p) => (
            <>
              <Button to="/next" label="dialog-cancel" disabled={disabled} />
              <Path />
              {p.children}
            </>
          )}
        >
          <Route path="*" component={() => null} />
        </MemoryRouter>
      ));
      return screen.getByRole('link', { name: 'dialog-cancel' });
    }

    it('navigates through the router', async () => {
      const link = renderLink();
      expect(link).toHaveAttribute('href', '/next');
      await userEvent.setup().click(link);
      expect(screen.getByTestId('path')).toHaveTextContent('/next');
    });

    it('disabled stays on the page', async () => {
      const link = renderLink(true);
      expect(link).toHaveAttribute('aria-disabled', 'true');
      await userEvent.setup().click(link);
      expect(screen.getByTestId('path')).toHaveTextContent('/');
      expect(screen.getByTestId('path')).not.toHaveTextContent('/next');
    });
  });

  it('types keep button and link props apart', () => {
    // Compile-time only, checked by typecheck
    void (() => (
      <>
        {/* @ts-expect-error links have no pressed state */}
        <Button to="/x" label="dialog-cancel" pressed />
        {/* @ts-expect-error links never submit */}
        <Button to="/x" label="dialog-cancel" type="submit" />
        {/* @ts-expect-error history state needs a link */}
        <Button label="dialog-cancel" state={{}} />
      </>
    ));
  });
});
