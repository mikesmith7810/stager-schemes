import { useEffect, useRef, useState } from 'react';
import { itemsApi } from '../api/items.js';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

export default function Items() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);
  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [webLink, setWebLink] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editName, setEditName] = useState('');
  const [editPrice, setEditPrice] = useState('');
  const [editWebLink, setEditWebLink] = useState('');
  const [pasteTargetId, setPasteTargetId] = useState(null);
  const [lightboxItemId, setLightboxItemId] = useState(null);
  const [imageVersion, setImageVersion] = useState({});
  const [uploading, setUploading] = useState(false);
  const [formPasteActive, setFormPasteActive] = useState(false);
  const [pendingImage, setPendingImage] = useState(null);
  const [pendingImagePreview, setPendingImagePreview] = useState(null);
  const pasteZoneRef = useRef(null);

  const load = () =>
    itemsApi.findAll().then(setItems).catch((err) => setError(err.message));

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (!pasteTargetId) return;

    const handlePaste = async (e) => {
      const imageItem = Array.from(e.clipboardData.items).find((i) =>
        i.type.startsWith('image/')
      );
      if (!imageItem) return;
      const file = imageItem.getAsFile();
      setUploading(true);
      try {
        await itemsApi.uploadImage(pasteTargetId, file);
        setImageVersion((prev) => ({ ...prev, [pasteTargetId]: (prev[pasteTargetId] || 0) + 1 }));
        setPasteTargetId(null);
        load();
      } catch (err) {
        setError(err.message);
      } finally {
        setUploading(false);
      }
    };

    const handleKeyDown = (e) => {
      if (e.key === 'Escape') setPasteTargetId(null);
    };

    document.addEventListener('paste', handlePaste);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('paste', handlePaste);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [pasteTargetId]);

  useEffect(() => {
    if (!formPasteActive) return;

    const handlePaste = (e) => {
      const imageItem = Array.from(e.clipboardData.items).find((i) =>
        i.type.startsWith('image/')
      );
      if (!imageItem) return;
      const file = imageItem.getAsFile();
      if (pendingImagePreview) URL.revokeObjectURL(pendingImagePreview);
      const preview = URL.createObjectURL(file);
      setPendingImage(file);
      setPendingImagePreview(preview);
      setFormPasteActive(false);
    };

    const handleKeyDown = (e) => {
      if (e.key === 'Escape') setFormPasteActive(false);
    };

    document.addEventListener('paste', handlePaste);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('paste', handlePaste);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [formPasteActive]);

  useEffect(() => {
    if (!lightboxItemId) return;
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') setLightboxItemId(null);
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [lightboxItemId]);

  const clearPendingImage = () => {
    if (pendingImagePreview) URL.revokeObjectURL(pendingImagePreview);
    setPendingImage(null);
    setPendingImagePreview(null);
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const created = await itemsApi.create({ name, price: parseFloat(price), webLink: webLink || null });
      if (pendingImage) {
        await itemsApi.uploadImage(created.id, pendingImage);
        clearPendingImage();
      }
      setName('');
      setPrice('');
      setWebLink('');
      setFormPasteActive(false);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setEditName(item.name);
    setEditPrice(String(item.price));
    setEditWebLink(item.webLink ?? '');
  };

  const handleSaveEdit = async (id) => {
    setError(null);
    try {
      await itemsApi.update(id, {
        name: editName,
        price: parseFloat(editPrice),
        webLink: editWebLink || null,
      });
      setEditingId(null);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (item) => {
    setError(null);
    try {
      await itemsApi.delete(item.id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDeleteImage = async (itemId) => {
    setError(null);
    try {
      await itemsApi.deleteImage(itemId);
      setImageVersion((prev) => ({ ...prev, [itemId]: (prev[itemId] || 0) + 1 }));
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const activateFormPaste = () => {
    setPasteTargetId(null);
    setFormPasteActive((prev) => !prev);
  };

  const activateRowPaste = (itemId) => {
    setFormPasteActive(false);
    setPasteTargetId((prev) => (prev === itemId ? null : itemId));
  };

  const imageCell = (item) => {
    if (item.hasImage) {
      const version = imageVersion[item.id] || 0;
      return (
        <div className="item-image-cell">
          <img
            src={`/api/items/${item.id}/image?v=${version}`}
            className="item-thumbnail"
            alt={item.name}
            onClick={() => setLightboxItemId(item.id)}
          />
          <button
            className="item-image-remove"
            onClick={() => handleDeleteImage(item.id)}
            title="Remove image"
          >
            &times;
          </button>
        </div>
      );
    }
    const isActive = pasteTargetId === item.id;
    return (
      <button
        ref={isActive ? pasteZoneRef : null}
        className={`paste-zone${isActive ? ' active' : ''}`}
        onClick={() => activateRowPaste(item.id)}
        title={isActive ? 'Press Ctrl+V to paste image, Esc to cancel' : 'Click then paste an image'}
        disabled={uploading && isActive}
      >
        {isActive ? (uploading ? '…' : 'Ctrl+V') : '+ img'}
      </button>
    );
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Items</h1>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleAdd} className="form-row" style={{ marginBottom: '1.5rem' }}>
        <div className="field">
          <label className="field-label">Name</label>
          <input
            className="input"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Item name"
            required
          />
        </div>
        <div className="field">
          <label className="field-label">Price (£)</label>
          <input
            className="input input-sm"
            type="number"
            min="0"
            step="0.01"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            placeholder="0.00"
            required
          />
        </div>
        <div className="field">
          <label className="field-label">Web link</label>
          <input
            className="input"
            type="url"
            value={webLink}
            onChange={(e) => setWebLink(e.target.value)}
            placeholder="https://…"
          />
        </div>
        <div className="field">
          <label className="field-label">Image</label>
          {pendingImage ? (
            <div className="item-image-cell">
              <img
                src={pendingImagePreview}
                className="item-thumbnail"
                alt="Preview"
                onClick={() => setLightboxItemId('__preview__')}
              />
              <button
                type="button"
                className="item-image-remove"
                onClick={clearPendingImage}
                title="Remove image"
              >
                &times;
              </button>
            </div>
          ) : (
            <button
              type="button"
              className={`paste-zone${formPasteActive ? ' active' : ''}`}
              onClick={activateFormPaste}
              title={formPasteActive ? 'Press Ctrl+V to paste image, Esc to cancel' : 'Click then paste an image'}
            >
              {formPasteActive ? 'Ctrl+V' : '+ img'}
            </button>
          )}
        </div>
        <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-end' }}>
          + Add Item
        </button>
      </form>

      {items.length === 0 ? (
        <p className="empty-msg">No items yet.</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: '64px' }}>Image</th>
                <th>Name</th>
                <th>Price</th>
                <th>Web Link</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) =>
                editingId === item.id ? (
                  <tr key={item.id}>
                    <td>{imageCell(item)}</td>
                    <td>
                      <input
                        className="input"
                        value={editName}
                        onChange={(e) => setEditName(e.target.value)}
                      />
                    </td>
                    <td>
                      <input
                        className="input input-sm"
                        type="number"
                        min="0"
                        step="0.01"
                        value={editPrice}
                        onChange={(e) => setEditPrice(e.target.value)}
                      />
                    </td>
                    <td>
                      <input
                        className="input"
                        type="url"
                        value={editWebLink}
                        onChange={(e) => setEditWebLink(e.target.value)}
                      />
                    </td>
                    <td>
                      <div className="td-actions">
                        <button
                          className="btn btn-primary btn-sm"
                          onClick={() => handleSaveEdit(item.id)}
                        >
                          Save
                        </button>
                        <button
                          className="btn btn-ghost btn-sm"
                          onClick={() => setEditingId(null)}
                        >
                          Cancel
                        </button>
                      </div>
                    </td>
                  </tr>
                ) : (
                  <tr key={item.id}>
                    <td>{imageCell(item)}</td>
                    <td>{item.name}</td>
                    <td>
                      <span className="price">{formatPrice(item.price)}</span>
                    </td>
                    <td>
                      {item.webLink ? (
                        <a href={item.webLink} target="_blank" rel="noreferrer">
                          Link
                        </a>
                      ) : (
                        <span className="price-muted">—</span>
                      )}
                    </td>
                    <td>
                      <div className="td-actions">
                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => startEdit(item)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(item)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                )
              )}
            </tbody>
          </table>
        </div>
      )}

      {lightboxItemId && lightboxItemId !== '__preview__' && (
        <div className="lightbox-overlay" onClick={() => setLightboxItemId(null)}>
          <img
            src={`/api/items/${lightboxItemId}/image`}
            className="lightbox-img"
            alt="Item image"
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}

      {lightboxItemId === '__preview__' && pendingImagePreview && (
        <div className="lightbox-overlay" onClick={() => setLightboxItemId(null)}>
          <img
            src={pendingImagePreview}
            className="lightbox-img"
            alt="Image preview"
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  );
}
