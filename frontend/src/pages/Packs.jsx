import { useEffect, useState } from 'react';
import { itemsApi } from '../api/items.js';
import { packsApi } from '../api/packs.js';
import Lightbox from '../components/Lightbox.jsx';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

export default function Packs() {
  const [packs, setPacks] = useState([]);
  const [allItems, setAllItems] = useState([]);
  const [error, setError] = useState(null);
  const [newPackName, setNewPackName] = useState('');
  const [expandedPackId, setExpandedPackId] = useState(null);
  const [selectedItemId, setSelectedItemId] = useState('');
  const [selectedQty, setSelectedQty] = useState(1);
  const [lightboxItemId, setLightboxItemId] = useState(null);

  const load = () =>
    Promise.all([packsApi.findAll(), itemsApi.findAll()])
      .then(([p, i]) => {
        setPacks(p);
        setAllItems(i);
      })
      .catch((err) => setError(err.message));

  useEffect(() => {
    load();
  }, []);

  const handleAddPack = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await packsApi.create({ name: newPackName, items: [] });
      setNewPackName('');
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDeletePack = async (pack) => {
    setError(null);
    try {
      await packsApi.delete(pack.id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAddItem = async (packId) => {
    if (!selectedItemId) return;
    setError(null);
    try {
      await packsApi.addItem(packId, {
        itemId: Number(selectedItemId),
        quantity: Number(selectedQty),
      });
      setSelectedItemId('');
      setSelectedQty(1);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemoveItem = async (packId, itemId) => {
    setError(null);
    try {
      await packsApi.removeItem(packId, itemId);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Packs</h1>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleAddPack} className="form-row" style={{ marginBottom: '1.5rem' }}>
        <div className="field">
          <label className="field-label">Pack name</label>
          <input
            className="input"
            value={newPackName}
            onChange={(e) => setNewPackName(e.target.value)}
            placeholder="Pack name"
            required
          />
        </div>
        <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-end' }}>
          + Add Pack
        </button>
      </form>

      {packs.length === 0 ? (
        <p className="empty-msg">No packs yet.</p>
      ) : (
        packs.map((pack) => {
          const packTotal = pack.items.reduce(
            (sum, pi) => sum + pi.itemPrice * pi.quantity,
            0
          );
          return (
          <div key={pack.id} className="panel">
            <div className="panel-header">
              <button
                className="panel-title"
                style={{ background: 'none', border: 'none', cursor: 'pointer', font: 'inherit', color: 'inherit' }}
                onClick={() =>
                  setExpandedPackId(expandedPackId === pack.id ? null : pack.id)
                }
              >
                {expandedPackId === pack.id ? '▾' : '▸'} {pack.name}
                <span className="price-muted" style={{ marginLeft: '0.5rem' }}>
                  ({pack.items.length} {pack.items.length === 1 ? 'item' : 'items'} — {formatPrice(packTotal)})
                </span>
              </button>
              <button
                className="btn btn-danger btn-sm"
                onClick={() => handleDeletePack(pack)}
              >
                Delete
              </button>
            </div>
            {expandedPackId === pack.id && (
              <div className="panel-body">
                {pack.items.length === 0 && (
                  <p className="price-muted" style={{ marginBottom: '0.75rem' }}>
                    No items in this pack.
                  </p>
                )}
                {pack.items.map((pi) => (
                  <div key={pi.itemId} className="line-item">
                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                      <img
                        src={`/api/items/${pi.itemId}/image`}
                        className="item-thumbnail-sm"
                        alt=""
                        onClick={() => setLightboxItemId(pi.itemId)}
                        onError={(e) => { e.currentTarget.style.display = 'none'; }}
                      />
                      <span>
                        {pi.itemName} &times; {pi.quantity}
                      </span>
                    </div>
                    <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                      <span className="price-muted">{formatPrice(pi.itemPrice)}</span>
                      <button
                        className="btn btn-ghost btn-sm"
                        onClick={() => handleRemoveItem(pack.id, pi.itemId)}
                      >
                        &times;
                      </button>
                    </div>
                  </div>
                ))}
                <div className="form-row" style={{ marginTop: '0.75rem' }}>
                  <select
                    className="select"
                    value={selectedItemId}
                    onChange={(e) => setSelectedItemId(e.target.value)}
                  >
                    <option value="">Add item…</option>
                    {allItems.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name} ({formatPrice(item.price)})
                      </option>
                    ))}
                  </select>
                  <input
                    className="input input-sm"
                    type="number"
                    min="1"
                    value={selectedQty}
                    onChange={(e) => setSelectedQty(e.target.value)}
                  />
                  <button
                    className="btn btn-ghost btn-sm"
                    onClick={() => handleAddItem(pack.id)}
                    disabled={!selectedItemId}
                  >
                    Add
                  </button>
                </div>
              </div>
            )}
          </div>
        );})
      )}
      <Lightbox itemId={lightboxItemId} onClose={() => setLightboxItemId(null)} />
    </div>
  );
}
