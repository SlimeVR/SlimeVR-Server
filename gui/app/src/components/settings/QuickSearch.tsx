import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import {
  Fragment,
  ReactNode,
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';
import { BaseModal } from '@/components/commons/BaseModal';
import { SearchIcon } from '@/components/commons/icon/SearchIcon';
import { GearIcon } from '@/components/commons/icon/GearIcon';
import { FileIcon } from '@/components/commons/icon/FileIcon';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Typography } from '@/components/commons/Typography';
import { SECTIONS, SearchEntry, listSearchEntries } from './search-entries';
import { useVRCConfig } from '@/hooks/vrc-config';
import { useElectron } from '@/hooks/electron';

const MAX_RESULTS = 30;

const SECTION_ICONS = {
  settings: <GearIcon size={16} />,
  page: <FileIcon width={16} />,
};

const SHORTCUT_LABEL = /Mac|iPhone|iPad/.test(navigator.platform)
  ? '⌘ K'
  : 'Ctrl K';

function escapeRegExp(text: string) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function Highlight({ text, words }: { text: string; words: string[] }) {
  if (!words.length) return <>{text}</>;
  const pattern = new RegExp(`(${words.map(escapeRegExp).join('|')})`, 'gi');
  return (
    <>
      {text.split(pattern).map((part, index) =>
        index % 2 === 1 ? (
          <span key={index} className="text-accent-background-10">
            {part}
          </span>
        ) : (
          <Fragment key={index}>{part}</Fragment>
        )
      )}
    </>
  );
}

function KeyHint({ keys, id }: { keys: string; id: string }) {
  return (
    <div className="flex items-center gap-2">
      <kbd className="px-1.5 py-0.5 rounded bg-background-60 text-background-10 text-xs">
        {keys}
      </kbd>
      <Typography id={id} />
    </div>
  );
}

function QuickSearchDialog({ onClose }: { onClose: () => void }) {
  const { l10n } = useLocalization();
  const navigate = useNavigate();
  const { isElectron } = useElectron();
  const { state: vrcConfigState } = useVRCConfig();
  const isVrcSupported = !!vrcConfigState?.isSupported;
  const [query, setQuery] = useState('');
  const [active, setActive] = useState(0);

  const localized = useMemo(() => {
    const text = (id: string | null) =>
      id ? l10n.getString(id).toLowerCase() : '';
    return listSearchEntries({ isElectron, isVrcSupported }).map((entry) => ({
      entry,
      haystack: [
        entry.title,
        entry.description,
        SECTIONS[entry.section].label,
        ...entry.crumbs,
      ]
        .map(text)
        .join('\n'),
    }));
  }, [l10n, isElectron, isVrcSupported]);

  const words = useMemo(
    () => query.toLowerCase().split(/\s+/).filter(Boolean),
    [query]
  );

  const results = useMemo(() => {
    if (!words.length) return [];
    return localized
      .filter(({ haystack }) => words.every((w) => haystack.includes(w)))
      .slice(0, MAX_RESULTS)
      .map(({ entry }) => entry);
  }, [localized, words]);

  const resultId = (index: number) => `quick-search-result-${index}`;

  useEffect(() => {
    document
      .getElementById(resultId(active))
      ?.scrollIntoView({ block: 'nearest' });
  }, [active]);

  const open = (entry: SearchEntry) => {
    onClose();
    navigate(entry.route, { state: entry.state });
  };

  const onKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
      if (!results.length) return;
      event.preventDefault();
      const step = event.key === 'ArrowDown' ? 1 : -1;
      setActive((active + step + results.length) % results.length);
      return;
    }
    if (event.key === 'Enter') {
      const entry = results[active];
      if (!entry) return;
      event.preventDefault();
      open(entry);
    }
  };

  return (
    <div className="flex flex-col min-h-0" onKeyDown={onKeyDown}>
      <div className="flex items-center gap-3 px-5 border-b border-background-60">
        <SearchIcon className="fill-accent-background-10 shrink-0" size={22} />
        <input
          autoFocus
          type="text"
          role="combobox"
          aria-expanded={results.length > 0}
          aria-controls="quick-search-results"
          aria-activedescendant={results.length ? resultId(active) : undefined}
          autoComplete="off"
          className="w-full py-5 bg-transparent border-none px-0 outline-none focus:outline-none focus:ring-0 shadow-none text-section-title text-background-10 placeholder:text-background-30"
          placeholder={l10n.getString('settings-sidebar-search')}
          value={query}
          onChange={(e) => {
            setQuery(e.currentTarget.value);
            setActive(0);
          }}
        />
        <kbd className="shrink-0 px-1.5 py-0.5 rounded bg-background-60 text-background-30 text-xs">
          Esc
        </kbd>
      </div>
      {!words.length && (
        <div className="flex flex-col items-center gap-2 px-4 py-10 text-center">
          <LoaderIcon slimeState={SlimeState.CURIOUS} size={72} lowPriority />
          <Typography id="settings-sidebar-search-hint" />
        </div>
      )}
      {words.length > 0 && !results.length && (
        <div className="flex flex-col items-center gap-2 px-4 py-10 text-center">
          <LoaderIcon slimeState={SlimeState.SAD} size={72} lowPriority />
          <Typography id="settings-sidebar-search-empty" />
        </div>
      )}
      {results.length > 0 && (
        <div
          id="quick-search-results"
          role="listbox"
          className="flex flex-col gap-1 p-2 overflow-y-auto max-h-[50vh]"
        >
          {results.map((entry, index) => (
            <div
              key={entry.id}
              id={resultId(index)}
              role="option"
              aria-selected={index === active}
              className={classNames(
                'flex items-center gap-3 px-3 py-2.5 rounded-xl cursor-pointer transition-colors',
                index === active && 'bg-background-60'
              )}
              onMouseMove={() => setActive(index)}
              onClick={() => open(entry)}
            >
              <div
                className={classNames(
                  'w-9 h-9 shrink-0 flex items-center justify-center rounded-lg fill-background-10 transition-colors',
                  index === active
                    ? 'bg-accent-background-40'
                    : 'bg-background-60'
                )}
              >
                {SECTION_ICONS[entry.section]}
              </div>
              <div className="flex flex-col items-start min-w-0 flex-grow">
                <Typography bold>
                  <Highlight text={l10n.getString(entry.title)} words={words} />
                </Typography>
                <div className="flex flex-wrap items-center gap-x-1.5">
                  {entry.crumbs.length === 0 && (
                    <Typography id={SECTIONS[entry.section].label} />
                  )}
                  {entry.crumbs.map((crumb, crumbIndex) => (
                    <Fragment key={crumb}>
                      {crumbIndex > 0 && <Typography>{'›'}</Typography>}
                      <Typography id={crumb} />
                    </Fragment>
                  ))}
                </div>
              </div>
              {index === active && (
                <kbd className="shrink-0 px-1.5 py-0.5 rounded bg-background-70 text-background-10 text-xs">
                  ↵
                </kbd>
              )}
            </div>
          ))}
        </div>
      )}
      <div className="flex items-center gap-5 px-5 py-2.5 border-t border-background-60 bg-background-80">
        <KeyHint keys="↑↓" id="settings-sidebar-search-navigate" />
        <KeyHint keys="↵" id="settings-sidebar-search-open" />
        <KeyHint keys="Esc" id="settings-sidebar-search-close" />
      </div>
    </div>
  );
}

interface QuickSearchLauncher {
  open: () => void;
}

const LauncherContext = createContext<QuickSearchLauncher | null>(null);

export function useQuickSearch() {
  return useContext(LauncherContext);
}

export function QuickSearchProvider({ children }: { children: ReactNode }) {
  const [isOpen, setIsOpen] = useState(false);
  const launcher = useMemo(() => ({ open: () => setIsOpen(true) }), []);

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (!(event.ctrlKey || event.metaKey) || event.key.toLowerCase() !== 'k')
        return;
      event.preventDefault();
      setIsOpen(true);
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, []);

  return (
    <LauncherContext.Provider value={launcher}>
      {children}
      <BaseModal
        isOpen={isOpen}
        onRequestClose={() => setIsOpen(false)}
        important
        overlayClassName="fixed inset-0 z-50 flex flex-col items-center justify-start pt-[12vh] px-2 bg-background-90/85 backdrop-blur-sm"
        className="w-full max-w-xl max-h-[76vh] flex flex-col overflow-hidden rounded-2xl border border-background-60 bg-background-70 text-background-10 text-standard shadow-2xl outline-none"
      >
        <QuickSearchDialog onClose={() => setIsOpen(false)} />
      </BaseModal>
    </LauncherContext.Provider>
  );
}

export function QuickSearchField() {
  const launcher = useQuickSearch();
  if (!launcher) return null;

  return (
    <button
      type="button"
      className="flex items-center gap-2 w-full min-w-0 min-h-[40px] px-3 rounded-lg bg-background-60 hover:bg-background-50 transition-colors text-left"
      onClick={launcher.open}
    >
      <SearchIcon className="fill-background-10 shrink-0" />
      <span className="min-w-0 truncate">
        <Typography
          id="titlebar-search"
          whitespace="whitespace-nowrap"
          tag="span"
        />
      </span>
      <kbd className="hidden lg:inline ml-auto shrink-0 px-2 py-0.5 rounded bg-background-70 text-background-10 text-xs whitespace-nowrap">
        {SHORTCUT_LABEL}
      </kbd>
    </button>
  );
}
