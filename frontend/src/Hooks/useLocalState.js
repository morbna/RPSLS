import { useState } from 'react';

export default function useLocalState(key, initVal) {
  const [stored, store] = useState(() => {
    const item = window.localStorage.getItem(key);
    return item ? JSON.parse(item) : initVal;
  });

  const setVal = (val) => {
    store(val);
    window.localStorage.setItem(key, JSON.stringify(val));
  };

  return [stored, setVal];
}
