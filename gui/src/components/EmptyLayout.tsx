import { ReactNode } from 'react';
import { TopBar } from '@/components/TopBar';
import './EmptyLayout.scss';

export function EmptyLayout({ children }: { children: ReactNode }) {
  return (
    <div className="empty-layout h-full">
      <div style={{ gridArea: 't' }}>
        <TopBar></TopBar>
      </div>
      <div style={{ gridArea: 'c' }} className="mt-2 relative">
        {children}
      </div>
    </div>
  )
}
