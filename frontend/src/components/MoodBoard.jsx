import { useEffect, useRef, useState } from 'react';
import logoSrc from '../../assets/logo-black-write-trans.png';

const INITIAL_IMG_SIZE = 160;
const COLS = 4;
const COL_GAP = 24;
const ROW_GAP = 24;
const CANVAS_PADDING = 32;
const MIN_SIZE = 60;
const STORAGE_PREFIX = 'moodboard-';

function loadSavedLayout(roomId) {
  try {
    const raw = localStorage.getItem(`${STORAGE_PREFIX}${roomId}`);
    return raw ? JSON.parse(raw) : {};
  } catch {
    return {};
  }
}

function saveLayout(roomId, tiles) {
  try {
    const layout = {};
    tiles.forEach((t) => {
      layout[t.key] = {
        x: t.x,
        y: t.y,
        width: t.width,
        height: t.height,
        zIndex: t.zIndex,
        hidden: t.hidden || false,
      };
    });
    localStorage.setItem(`${STORAGE_PREFIX}${roomId}`, JSON.stringify(layout));
  } catch {
    // ignore storage errors
  }
}

function collectTiles(room, savedLayout) {
  const tiles = [];

  room.items.forEach((ri, entryIdx) => {
    for (let i = 0; i < (ri.quantity || 1); i++) {
      tiles.push({
        src: `/api/items/${ri.itemId}/image`,
        key: `item-${ri.itemId}-${entryIdx}-${i}`,
      });
    }
  });

  (room.packs || []).forEach((rp) =>
    (rp.packItems || rp.items || []).forEach((pi) =>
      tiles.push({
        src: `/api/items/${pi.itemId}/image`,
        key: `pack-${rp.packId}-item-${pi.itemId}`,
      })
    )
  );

  tiles.push({ src: logoSrc, key: 'logo' });

  return tiles.map((tile, i) => {
    const saved = savedLayout[tile.key];
    return {
      ...tile,
      x: saved?.x ?? CANVAS_PADDING + (i % COLS) * (INITIAL_IMG_SIZE + COL_GAP),
      y: saved?.y ?? CANVAS_PADDING + Math.floor(i / COLS) * (INITIAL_IMG_SIZE + ROW_GAP),
      width: saved?.width ?? INITIAL_IMG_SIZE,
      height: saved?.height ?? INITIAL_IMG_SIZE,
      zIndex: saved?.zIndex ?? i + 1,
      hidden: saved?.hidden ?? false,
    };
  });
}

export default function MoodBoard({ room, onClose }) {
  const [tiles, setTiles] = useState(() => collectTiles(room, loadSavedLayout(room.id)));
  const canvasRef = useRef(null);
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    const handleKey = (e) => {
      if (e.key === 'Escape') onCloseRef.current();
    };
    document.addEventListener('keydown', handleKey);
    return () => document.removeEventListener('keydown', handleKey);
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => saveLayout(room.id, tiles), 300);
    return () => clearTimeout(timer);
  }, [tiles, room.id]);

  const bringToFront = (key) => {
    setTiles((prev) => {
      const maxZ = Math.max(...prev.map((t) => t.zIndex));
      return prev.map((t) => (t.key === key ? { ...t, zIndex: maxZ + 1 } : t));
    });
  };

  const removeTile = (key) => {
    setTiles((prev) => prev.map((t) => (t.key === key ? { ...t, hidden: true } : t)));
  };

  const handleReset = () => {
    localStorage.removeItem(`${STORAGE_PREFIX}${room.id}`);
    setTiles(collectTiles(room, {}));
  };

  const startDrag = (e, key) => {
    if (e.button !== 0) return;
    e.preventDefault();
    bringToFront(key);
    const tile = tiles.find((t) => t.key === key);
    const startMouseX = e.clientX;
    const startMouseY = e.clientY;
    const startX = tile.x;
    const startY = tile.y;

    const onMove = (moveEvent) => {
      setTiles((prev) =>
        prev.map((t) =>
          t.key === key
            ? { ...t, x: startX + moveEvent.clientX - startMouseX, y: startY + moveEvent.clientY - startMouseY }
            : t
        )
      );
    };

    const onUp = () => {
      document.removeEventListener('mousemove', onMove);
      document.removeEventListener('mouseup', onUp);
    };

    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseup', onUp);
  };

  const startResize = (e, key) => {
    if (e.button !== 0) return;
    e.preventDefault();
    e.stopPropagation();
    const tile = tiles.find((t) => t.key === key);
    const startMouseX = e.clientX;
    const startMouseY = e.clientY;
    const startW = tile.width;
    const startH = tile.height;

    const onMove = (moveEvent) => {
      setTiles((prev) =>
        prev.map((t) =>
          t.key === key
            ? {
                ...t,
                width: Math.max(MIN_SIZE, startW + moveEvent.clientX - startMouseX),
                height: Math.max(MIN_SIZE, startH + moveEvent.clientY - startMouseY),
              }
            : t
        )
      );
    };

    const onUp = () => {
      document.removeEventListener('mousemove', onMove);
      document.removeEventListener('mouseup', onUp);
    };

    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseup', onUp);
  };

  const handleSavePdf = () => {
    const maxBottom =
      tiles.reduce((max, t) => Math.max(max, t.y + t.height), 0) + CANVAS_PADDING;
    const canvas = canvasRef.current;
    if (canvas) {
      canvas.style.height = `${maxBottom}px`;
      canvas.style.overflow = 'visible';
    }
    document.body.classList.add('moodboard-printing');
    window.addEventListener(
      'afterprint',
      () => {
        document.body.classList.remove('moodboard-printing');
        if (canvas) {
          canvas.style.height = '';
          canvas.style.overflow = '';
        }
      },
      { once: true }
    );
    window.print();
  };

  const visibleTiles = tiles.filter((t) => !t.hidden);

  return (
    <div className="moodboard-overlay">
      <div className="moodboard-header screen-only">
        <span className="moodboard-title">{room.name} — Mood Board</span>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button className="btn btn-secondary btn-sm" onClick={handleReset}>
            Reset
          </button>
          <button className="btn btn-secondary btn-sm" onClick={handleSavePdf}>
            Save as PDF
          </button>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>
            Close
          </button>
        </div>
      </div>
      <div className="moodboard-canvas" ref={canvasRef}>
        {visibleTiles.map((tile) => (
          <div
            key={tile.key}
            className="moodboard-tile"
            style={{
              left: tile.x,
              top: tile.y,
              width: tile.width,
              height: tile.height,
              zIndex: tile.zIndex,
            }}
            onMouseDown={(e) => startDrag(e, tile.key)}
          >
            <img
              src={tile.src}
              className="moodboard-img"
              alt=""
              draggable={false}
              onError={(e) => { e.currentTarget.style.opacity = '0.1'; }}
            />
            <button
              className="moodboard-remove"
              onMouseDown={(e) => e.stopPropagation()}
              onClick={() => removeTile(tile.key)}
            >
              &times;
            </button>
            <div
              className="moodboard-resize-handle"
              onMouseDown={(e) => startResize(e, tile.key)}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
