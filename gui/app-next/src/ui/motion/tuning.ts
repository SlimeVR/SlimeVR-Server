// Every feel value of directional motion lives here
export const motionTuning = {
  // Held direction: first repeat after the delay, then the interval shrinks per repeat down to the minimum
  repeatDelayMs: 350,
  repeatStartMs: 100,
  repeatMinMs: 50,
  repeatRampMs: 10,
  // Stick deadzone with hysteresis: commit above enter, release below exit
  deadzoneEnter: 0.35,
  deadzoneExit: 0.25,
  // A held diagonal switches axis only when the other axis leads by this much
  axisSwitchRatio: 1.4,
  // Part of the visible span scrolled when a move stays inside a scroller
  scrollFraction: 0.6,
  ringGlideMs: 120,
  ringPaddingPx: 3,
  bumpPx: 4,
  bumpMs: 160,
};
