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
import { Typography } from '@/components/commons/Typography';
import { SECTIONS, SearchEntry, listSearchEntries } from './search-entries';
import { useVRCConfig } from '@/hooks/vrc-config';
import { useElectron } from '@/hooks/electron';

const MAX_RESULTS = 30;

const SHORTCUT_LABEL = /Mac|iPhone|iPad/.test(navigator.platform)
  ? '⌘ K'
  : 'Ctrl K';

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

  const results = useMemo(() => {
    const words = query.toLowerCase().split(/\s+/).filter(Boolean);
    if (!words.length) return [];
    return localized
      .filter(({ haystack }) => words.every((w) => haystack.includes(w)))
      .slice(0, MAX_RESULTS)
      .map(({ entry }) => entry);
  }, [localized, query]);

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
      <div className="flex items-center gap-3 px-4 border-b border-background-60">
        <SearchIcon className="fill-background-10 shrink-0" />
        <input
          autoFocus
          type="text"
          role="combobox"
          aria-expanded={results.length > 0}
          aria-controls="quick-search-results"
          aria-activedescendant={results.length ? resultId(active) : undefined}
          autoComplete="off"
          className="w-full py-4 bg-transparent border-none px-0 outline-none focus:outline-none focus:ring-0 shadow-none text-standard text-background-10 placeholder:text-background-30"
          placeholder={l10n.getString('settings-sidebar-search')}
          value={query}
          onChange={(e) => {
            setQuery(e.currentTarget.value);
            setActive(0);
          }}
        />
      </div>
      {query.trim() && !results.length && (
        <div className="p-4">
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
                'flex flex-col items-start px-3 py-2 rounded-lg cursor-pointer',
                index === active ? 'bg-background-50' : 'hover:bg-background-60'
              )}
              onMouseMove={() => setActive(index)}
              onClick={() => open(entry)}
            >
              <Typography bold id={entry.title} />
              <div className="flex gap-1">
                <Typography
                  bold
                  color={SECTIONS[entry.section].color}
                  id={SECTIONS[entry.section].label}
                />
                {entry.crumbs.map((crumb) => (
                  <Fragment key={crumb}>
                    <Typography>{'>'}</Typography>
                    <Typography id={crumb} />
                  </Fragment>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
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
        overlayClassName="fixed inset-0 z-50 flex flex-col items-center justify-start pt-[12vh] px-2 bg-background-90 bg-opacity-60"
        className="w-full max-w-xl max-h-[76vh] flex flex-col overflow-hidden rounded-xl bg-background-70 text-background-10 text-standard shadow-xl outline-none"
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
      className="flex items-center gap-3 w-full min-h-[40px] px-3 rounded-lg bg-background-60 hover:bg-background-50 transition-colors text-left"
      onClick={launcher.open}
    >
      <SearchIcon className="fill-background-10 shrink-0" />
      <Typography id="titlebar-search" whitespace="whitespace-nowrap" />
      <kbd className="ml-auto shrink-0 px-2 py-0.5 rounded bg-background-70 text-background-10 text-xs whitespace-nowrap">
        {SHORTCUT_LABEL}
      </kbd>
    </button>
  );
}
