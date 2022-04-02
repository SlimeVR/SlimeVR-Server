import { useEffect, useRef, useState } from "react";



export function useLayout() {
    const [layoutHeight, setLayoutHeigt] = useState(window.innerHeight);
    const ref = useRef<HTMLDivElement>(null);
  
    const computeLayoutHeight = (windowHeight: number) => {
      if (ref.current) {
        setLayoutHeigt(windowHeight - ref.current.getBoundingClientRect().top)
      }
    }
  
    const onWindowResize = () => {
      computeLayoutHeight(window.innerHeight)
    }

    useEffect(() => {
      window.addEventListener('resize', onWindowResize);
      computeLayoutHeight(window.innerHeight)
      return () => {
        window.removeEventListener('resize', onWindowResize);
      }
    }, [])


    return {
        layoutHeight,
        ref
    }
}