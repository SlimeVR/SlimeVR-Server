export function a11yClick(event: React.KeyboardEvent | React.MouseEvent) {
  if (event.type === 'click') {
    return true;
  } else if (event.type === 'keydown') {
    const keyboard = event as React.KeyboardEvent;
    return keyboard.key === 'Enter' || keyboard.key === ' ';
  }
}
