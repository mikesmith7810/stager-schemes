import { useEffect, useRef, useState } from 'react';
import { itemsApi } from '../api/items.js';

function colorDist(data, idx, r, g, b) {
  return Math.sqrt(
    (data[idx] - r) ** 2 + (data[idx + 1] - g) ** 2 + (data[idx + 2] - b) ** 2
  );
}

async function stripBackground(imageUrl) {
  const img = new Image();
  await new Promise((resolve, reject) => {
    img.onload = resolve;
    img.onerror = reject;
    img.src = imageUrl;
  });

  const w = img.naturalWidth;
  const h = img.naturalHeight;
  const canvas = document.createElement('canvas');
  canvas.width = w;
  canvas.height = h;
  const ctx = canvas.getContext('2d');
  ctx.drawImage(img, 0, 0);

  const imageData = ctx.getImageData(0, 0, w, h);
  const { data } = imageData;

  // Sample the 4 corners to derive background colour
  const corners = [0, w - 1, (h - 1) * w, (h - 1) * w + (w - 1)];
  const [bgR, bgG, bgB] = corners
    .reduce(
      ([ar, ag, ab], p) => [ar + data[p * 4], ag + data[p * 4 + 1], ab + data[p * 4 + 2]],
      [0, 0, 0]
    )
    .map((v) => v / 4);

  // Iterative flood fill from all 4 corners
  const TOLERANCE = 50;
  const transparent = new Uint8Array(w * h);
  const stack = [];
  for (const p of corners) {
    if (colorDist(data, p * 4, bgR, bgG, bgB) <= TOLERANCE) stack.push(p);
  }
  while (stack.length > 0) {
    const p = stack.pop();
    if (transparent[p]) continue;
    if (colorDist(data, p * 4, bgR, bgG, bgB) > TOLERANCE) continue;
    transparent[p] = 1;
    const x = p % w;
    const y = (p / w) | 0;
    if (x > 0) stack.push(p - 1);
    if (x < w - 1) stack.push(p + 1);
    if (y > 0) stack.push(p - w);
    if (y < h - 1) stack.push(p + w);
  }

  for (let p = 0; p < w * h; p++) {
    if (transparent[p]) data[p * 4 + 3] = 0;
  }

  ctx.putImageData(imageData, 0, 0);
  return new Promise((resolve) => canvas.toBlob(resolve, 'image/png'));
}

export default function Lightbox({ itemId, onClose, onImageUpdated }) {
  const [phase, setPhase] = useState('view'); // view | removing | preview
  const [previewUrl, setPreviewUrl] = useState(null);
  const [previewBlob, setPreviewBlob] = useState(null);
  const [saving, setSaving] = useState(false);
  const [imgVersion, setImgVersion] = useState(0);
  const [error, setError] = useState(null);

  const stateRef = useRef({});
  stateRef.current = { phase, previewUrl };
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;

  useEffect(() => {
    setPhase('view');
    setPreviewUrl(null);
    setPreviewBlob(null);
    setError(null);
  }, [itemId]);

  useEffect(() => {
    if (!itemId) return;
    const handleKeyDown = (e) => {
      if (e.key !== 'Escape') return;
      const { phase, previewUrl } = stateRef.current;
      if (phase === 'preview') {
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setPreviewUrl(null);
        setPreviewBlob(null);
        setPhase('view');
      } else if (phase === 'view') {
        onCloseRef.current();
      }
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [itemId]);

  const handleRemoveBg = async () => {
    setPhase('removing');
    setError(null);
    await new Promise((resolve) => setTimeout(resolve, 20));
    try {
      const blob = await stripBackground(`/api/items/${itemId}/image`);
      const url = URL.createObjectURL(blob);
      setPreviewBlob(blob);
      setPreviewUrl(url);
      setPhase('preview');
    } catch {
      setError('Background removal failed. Try again.');
      setPhase('view');
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      const file = new File([previewBlob], 'image.png', { type: 'image/png' });
      await itemsApi.uploadImage(itemId, file);
      URL.revokeObjectURL(previewUrl);
      setPreviewUrl(null);
      setPreviewBlob(null);
      setPhase('view');
      setImgVersion((v) => v + 1);
      onImageUpdated?.(itemId);
    } catch {
      setError('Failed to save image.');
    } finally {
      setSaving(false);
    }
  };

  const handleDiscard = () => {
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(null);
    setPreviewBlob(null);
    setPhase('view');
  };

  if (!itemId) return null;

  const imgSrc =
    phase === 'preview' ? previewUrl : `/api/items/${itemId}/image?v=${imgVersion}`;

  return (
    <div
      className="lightbox-overlay"
      onClick={phase === 'view' ? onClose : undefined}
    >
      <div className="lightbox-content" onClick={(e) => e.stopPropagation()}>
        <div className={`lightbox-img-wrap${phase === 'preview' ? ' preview' : ''}`}>
          <img src={imgSrc} className="lightbox-img" alt="" draggable={false} />
          {phase === 'removing' && (
            <div className="lightbox-processing">Removing background…</div>
          )}
        </div>
        <div className="lightbox-actions">
          {error && <span className="lightbox-error">{error}</span>}
          {phase === 'view' && (
            <button className="btn btn-secondary btn-sm" onClick={handleRemoveBg}>
              Remove Background
            </button>
          )}
          {phase === 'preview' && (
            <>
              <button
                className="btn btn-primary btn-sm"
                onClick={handleSave}
                disabled={saving}
              >
                {saving ? 'Saving…' : 'Save'}
              </button>
              <button
                className="btn btn-secondary btn-sm"
                onClick={handleDiscard}
                disabled={saving}
              >
                Discard
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
