import { useEffect, useRef } from 'react';

export default function Lightbox({ itemId, onClose }) {
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    if (!itemId) return;
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') onCloseRef.current();
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [itemId]);

  if (!itemId) return null;

  return (
    <div className="lightbox-overlay" onClick={onClose}>
      <img
        src={`/api/items/${itemId}/image`}
        className="lightbox-img"
        alt="Item image"
        onClick={(e) => e.stopPropagation()}
      />
    </div>
  );
}
