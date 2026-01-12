function hsvToRGB(h: number, s: number, v: number): [number, number, number] {
  let r = 0,
    g = 0,
    b = 0;
  const i = Math.floor(h * 6);
  const f = h * 6 - i;
  const p = v * (1 - s);
  const q = v * (1 - f * s);
  const t = v * (1 - (1 - f) * s);
  switch (i % 6) {
    case 0:
      r = v;
      g = t;
      b = p;
      break;
    case 1:
      r = q;
      g = v;
      b = p;
      break;
    case 2:
      r = p;
      g = v;
      b = t;
      break;
    case 3:
      r = p;
      g = q;
      b = v;
      break;
    case 4:
      r = t;
      g = p;
      b = v;
      break;
    case 5:
      r = v;
      g = p;
      b = q;
      break;
  }
  return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
}

export function applyColors(theme: string, hue: number) {
  if (theme == 'custom-bright') {
    document.documentElement.style.setProperty(
      '--background-20',
      hsvToRGB(hue, 0.06, 0.91).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-30',
      hsvToRGB(hue, 0.06, 0.83).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-40',
      hsvToRGB(hue, 0.1, 0.61).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-50',
      hsvToRGB(hue, 0.21, 0.38).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-60',
      hsvToRGB(hue, 0.25, 0.28).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-70',
      hsvToRGB(hue, 0.27, 0.22).join(',')
    );
    document.documentElement.style.setProperty(
      '--background-80',
      hsvToRGB(hue, 0.29, 0.15).join(',')
    );
  } else {
    document.documentElement.style.setProperty('--background-20', null);
    document.documentElement.style.setProperty('--background-30', null);
    document.documentElement.style.setProperty('--background-40', null);
    document.documentElement.style.setProperty('--background-50', null);
    document.documentElement.style.setProperty('--background-60', null);
    document.documentElement.style.setProperty('--background-70', null);
    document.documentElement.style.setProperty('--background-80', null);
  }
  if (theme == 'custom-bright' || theme == 'custom-dark') {
    document.documentElement.style.setProperty(
      '--accent-background-10',
      hsvToRGB(hue, 0.2, 1).join(',')
    );
    document.documentElement.style.setProperty(
      '--accent-background-20',
      hsvToRGB(hue, 0.51, 0.92).join(',')
    );
    document.documentElement.style.setProperty(
      '--accent-background-30',
      hsvToRGB(hue, 0.62, 0.72).join(',')
    );
    document.documentElement.style.setProperty(
      '--accent-background-40',
      hsvToRGB(hue, 0.62, 0.56).join(',')
    );
    document.documentElement.style.setProperty(
      '--accent-background-50',
      hsvToRGB(hue, 0.7, 0.36).join(',')
    );
    document.documentElement.style.setProperty(
      '--window-icon-stroke',
      hsvToRGB(hue, 0.43, 0.92).join(',')
    );
  } else {
    document.documentElement.style.setProperty('--accent-background-10', null);
    document.documentElement.style.setProperty('--accent-background-20', null);
    document.documentElement.style.setProperty('--accent-background-30', null);
    document.documentElement.style.setProperty('--accent-background-40', null);
    document.documentElement.style.setProperty('--accent-background-50', null);
    document.documentElement.style.setProperty('--window-icon-stroke', null);
  }
}
