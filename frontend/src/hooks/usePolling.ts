import { useEffect, useRef } from 'react';

/**
 * Custom hook for periodic background polling with clean unmounting.
 * @param callback Callback function to invoke periodically
 * @param intervalMs Interval in milliseconds (e.g. 30000 for 30s)
 * @param enabled Whether polling is active
 */
export const usePolling = (callback: () => void | Promise<void>, intervalMs: number = 30000, enabled: boolean = true) => {
  const savedCallback = useRef(callback);

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    if (!enabled) return;

    // Run immediately once
    savedCallback.current();

    const id = setInterval(() => {
      savedCallback.current();
    }, intervalMs);

    return () => clearInterval(id);
  }, [intervalMs, enabled]);
};
