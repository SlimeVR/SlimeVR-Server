import { useLayoutEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

/**
 * @deprecated now we just use choose option
 */
export function useBodyProportions() {
  const { pathname } = useLocation();
  const [lastUsedPage, setLastUsedPage] = useState(
    '/onboarding/body-proportions/choose'
  );

  useLayoutEffect(() => {
    const lastpage = sessionStorage.getItem('lastBodyProportionsPage');
    if (lastpage) setLastUsedPage(lastpage);
  }, []);

  return {
    lastUsedPage,
    onPageOpened: () => {
      sessionStorage.setItem('lastBodyProportionsPage', pathname);
      setLastUsedPage(pathname);
    },
  };
}
