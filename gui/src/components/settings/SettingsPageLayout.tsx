import classNames from 'classnames';
import { ReactNode, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { useBreakpoint } from '@/hooks/breakpoint';

export function SettingsPageLayout({
  children,
  className,
  ...props
}: {
  children: ReactNode;
} & React.HTMLAttributes<HTMLDivElement>) {
  const pageRef = useRef<HTMLDivElement | null>(null);
  const { state } = useLocation();
  const { isMobile } = useBreakpoint('mobile');

  useEffect(() => {
    const typedState: { scrollTo: string } = state;
    if (!pageRef.current || !typedState || !typedState.scrollTo) {
      return;
    }
    const elem = pageRef.current.querySelector(
      `#${typedState.scrollTo}`
    ) as HTMLElement | null;
    if (elem) {
      // stupid way of doing this, just get the closest overflow-y-auto
      // usually its just the parentElem
      const closestScroll = elem.closest(
        '.overflow-y-auto'
      ) as HTMLElement | null;
      if (closestScroll) {
        // The 40 is just enough padding for making the scroll look perfect
        const topPadding = isMobile ? 80 : 40;
        closestScroll.scroll({
          top: elem.offsetTop - topPadding,
          behavior: 'smooth',
        });
      }
    }
  }, [state]);

  return (
    <div ref={pageRef} className={className} {...props}>
      {children}
    </div>
  );
}

export function SettingsPagePaneLayout({
  children,
  className,
  icon,
  ...props
}: {
  children: ReactNode;
  icon: ReactNode;
} & React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={classNames(
        'mobile:scroll-mt-7 bg-background-70 rounded-lg px-4 py-8 flex xs:gap-4 w-full relative',
        className
      )}
      {...props}
    >
      <div className="flex mobile:absolute mobile:right-4">
        <div className=" w-10 h-10 bg-accent-background-40 flex justify-center items-center rounded-full fill-background-10">
          {icon}
        </div>
      </div>
      <div className="flex-col w-full">{children}</div>
    </div>
  );
}
