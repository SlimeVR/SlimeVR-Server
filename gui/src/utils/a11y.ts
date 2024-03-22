export function a11yClick(event: React.KeyboardEvent | React.MouseEvent) {
  if (event.type === 'click') {
    return true;
  } else if (event.type === 'keydown') {
    const keyboard = event as React.KeyboardEvent;
    return keyboard.key === 'Enter' || keyboard.key === ' ';
  }
}

export function waitUntil(
  condition: () => boolean,
  time: number,
  tries?: number
): Promise<void> {
  return new Promise((resolve) => {
    const interval = setInterval(() => {
      if (tries && --tries === 0) {
        clearInterval(interval);
      }
      if (condition()) {
        resolve();
        clearInterval(interval);
      }
    }, time);
  });
}
