import { ReactNode } from 'react';
import { TopBar } from './TopBar';
import './EmptyLayout.scss';

export function EmptyLayout({ children }: { children: ReactNode }) {
  return (
    <div className="empty-layout h-full">
      <div
        data-nav-region="shell"
        data-nav-area="topbar"
        style={{ gridArea: 't' }}
      >
        <TopBar />
      </div>
      <div
        data-nav-region="page"
        style={{ gridArea: 'c' }}
        className="mt-2 relative"
      >
        {children}
      </div>
    </div>
  );
}
