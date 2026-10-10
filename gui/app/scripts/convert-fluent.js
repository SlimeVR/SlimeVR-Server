#!/usr/bin/env node
// eslint-disable-next-line @typescript-eslint/no-var-requires
const fs = require('fs');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const translation = require('./translation.json');

const sections = {
  websocket: 'Websocket status',
  tips: 'Tips',
  'body-part': 'Body parts',
  'skeleton-bone': 'Skeleton stuff',
  reset: 'Tracker reset buttons',
  'serial-detection': 'Serial detection stuff',
  navbar: 'Navigation bar',
  bvh: 'Bounding volume hierarchy recording',
  overlay: 'Overlay settings',
  tracker: {
    status: 'Tracker status',
    table: {
      column: 'Tracker status columns',
    },
    rotation: 'Tracker rotation',
    infos: 'Tracker information',
    settings: 'Tracker settings',
    'part-card': 'Tracker part card info',
  },
  'body-assignment-menu': 'Body assignment menu',
  'tracker-selection-menu': 'Tracker assignment menu',
  'mounting-selection-menu': 'Mounting menu',
  settings: {
    sidebar: 'Sidebar settings',
    general: {
      steamvr: 'SteamVR settings',
      'tracker-mechanics': 'Tracker mechanics',
      'fk-settings': 'FK settings',
      'gesture-control': 'Gesture control settings (tracker tapping)',
      interface: 'Interface settings',
    },
    serial: 'Serial settings',
    osc: {
      router: 'OSC router settings',
      vrchat: 'OSC VRChat settings',
    },
  },
  onboarding: {
    default: 'Setup/onboarding menu',
    'wifi-creds': 'WiFi setup',
    'reset-tutorial': 'Mounting setup',
    home: 'Setup start',
    'enter-vr': 'Enter VR part of setup',
    done: 'Setup done',
    'connect-tracker': 'Tracker connection setup',
    'assign-trackers': 'Tracker assignment setup',
    'manual-mounting': 'Tracker manual mounting setup',
    'automatic-mounting': 'Tracker automatic mounting setup',
    'manual-proportions': 'Tracker manual proportions setup',
    'automatic-proportions': 'Tracker automatic proportions setup',
  },
  home: 'Home',
};

function goTo(obj, property) {
  const props = property.split('.');
  if (props.length === 1) {
    const prop = props.shift();
    if (typeof obj[prop] === 'string') {
      return obj[prop];
    } else if (obj[prop] && obj[prop].default) {
      return obj[prop].default;
    }
  } else {
    const prop = props.shift();
    if (obj[prop]) return goTo(obj[prop], props.join('.'));
  }
  return null;
}

function recurseObject(obj, prefix = '') {
  const array = [];
  let first = false;
  for (const prop in obj) {
    if (typeof obj[prop] === 'string') {
      if (!first) {
        first = true;
        const section = goTo(sections, prefix.slice(0, -1).replace(/\0/g, '.'));
        if (section) {
          array.push(`\n## ${section}`);
        }
      }

      array.push(
        `${prefix.replace(/-/g, '_').replace(/\0/g, '-')}${prop
          .replace(/-/g, '_')
          .replace(/\./g, '-')} = ${obj[prop]
          .replace(/{{/g, '{ $')
          .replace(/}}/g, ' }')}`
      );
    } else {
      array.push(...recurseObject(obj[prop], `${prefix}${prop}\0`));
    }
  }
  return array;
}

fs.writeFileSync(
  './translation.ftl',
  recurseObject(translation).join('\n').trim() + '\n'
);
