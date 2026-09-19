import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { itemsApi } from '../api/items.js';
import { packsApi } from '../api/packs.js';
import { roomsApi } from '../api/rooms.js';
import { schemesApi } from '../api/schemes.js';
import Lightbox from '../components/Lightbox.jsx';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

function SchemeRoomEditor({ schemeRoom, schemeId, allItems, allPacks, onUpdate, onOpenLightbox }) {
  const [selectedItemId, setSelectedItemId] = useState('');
  const [itemQuantity, setItemQuantity] = useState(1);
  const [selectedPackId, setSelectedPackId] = useState('');
  const [error, setError] = useState(null);

  const handleAddItem = async () => {
    if (!selectedItemId) return;
    setError(null);
    try {
      await schemesApi.addItemToRoom(schemeId, schemeRoom.id, {
        itemId: Number(selectedItemId),
        quantity: Number(itemQuantity),
      });
      setSelectedItemId('');
      setItemQuantity(1);
      onUpdate();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemoveItem = async (itemId) => {
    setError(null);
    try {
      await schemesApi.removeItemFromRoom(schemeId, schemeRoom.id, itemId);
      onUpdate();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAddPack = async () => {
    if (!selectedPackId) return;
    setError(null);
    try {
      await schemesApi.addPackToRoom(schemeId, schemeRoom.id, {
        packId: Number(selectedPackId),
        quantity: 1,
      });
      setSelectedPackId('');
      onUpdate();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemovePack = async (packId) => {
    setError(null);
    try {
      await schemesApi.removePackFromRoom(schemeId, schemeRoom.id, packId);
      onUpdate();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemoveRoom = async () => {
    setError(null);
    try {
      await schemesApi.removeRoom(schemeId, schemeRoom.id);
      onUpdate();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="scheme-room">
      <div className="scheme-room-header">
        <span className="scheme-room-name">{schemeRoom.name}</span>
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <span className="price">{formatPrice(schemeRoom.roomTotal)}</span>
          <button className="btn btn-danger btn-sm" onClick={handleRemoveRoom}>
            Remove
          </button>
        </div>
      </div>

      <div className="scheme-room-body">
        {error && <div className="error-msg">{error}</div>}

        <div>
          <p className="sub-section-title">Items</p>
          {schemeRoom.items.length === 0 && (
            <p className="price-muted">No items added yet.</p>
          )}
          {schemeRoom.items.map((item) => (
            <div key={item.id} className="line-item">
              <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <img
                  src={`/api/items/${item.itemId}/image`}
                  className="item-thumbnail-sm"
                  alt=""
                  onClick={() => onOpenLightbox(item.itemId)}
                  onError={(e) => { e.currentTarget.style.display = 'none'; }}
                />
                <span>{item.itemName} &times; {item.quantity}</span>
              </div>
              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                <span className="price-muted">{formatPrice(item.lineTotal)}</span>
                <button
                  className="btn btn-ghost btn-sm"
                  onClick={() => handleRemoveItem(item.itemId)}
                >
                  &times;
                </button>
              </div>
            </div>
          ))}
          <div className="form-row" style={{ marginTop: '0.6rem' }}>
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
              value={itemQuantity}
              onChange={(e) => setItemQuantity(e.target.value)}
            />
            <button
              className="btn btn-ghost btn-sm"
              onClick={handleAddItem}
              disabled={!selectedItemId}
            >
              Add
            </button>
          </div>
        </div>

        <div>
          <p className="sub-section-title">Packs</p>
          {schemeRoom.packs.length === 0 && (
            <p className="price-muted">No packs added yet.</p>
          )}
          {schemeRoom.packs.map((pack) => (
            <div key={pack.id}>
              <div className="line-item">
                <span style={{ fontWeight: 600 }}>{pack.packName}</span>
                <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                  <span className="price-muted">{formatPrice(pack.packTotal)}</span>
                  <button
                    className="btn btn-ghost btn-sm"
                    onClick={() => handleRemovePack(pack.packId)}
                  >
                    &times;
                  </button>
                </div>
              </div>
              {pack.packItems.map((pi) => (
                <div key={pi.itemId} className="line-item" style={{ paddingLeft: '1rem' }}>
                  <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                    <img
                      src={`/api/items/${pi.itemId}/image`}
                      className="item-thumbnail-sm"
                      alt=""
                      onClick={() => onOpenLightbox(pi.itemId)}
                      onError={(e) => { e.currentTarget.style.display = 'none'; }}
                    />
                    <span className="price-muted">{pi.itemName} &times; {pi.quantity}</span>
                  </div>
                  <span className="price-muted">{formatPrice(pi.itemPrice * pi.quantity)}</span>
                </div>
              ))}
            </div>
          ))}
          <div className="form-row" style={{ marginTop: '0.6rem' }}>
            <select
              className="select"
              value={selectedPackId}
              onChange={(e) => setSelectedPackId(e.target.value)}
            >
              <option value="">Add pack…</option>
              {allPacks.map((pack) => (
                <option key={pack.id} value={pack.id}>
                  {pack.name}
                </option>
              ))}
            </select>
            <button
              className="btn btn-ghost btn-sm"
              onClick={handleAddPack}
              disabled={!selectedPackId}
            >
              Add
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function AddScheme() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [schemeName, setSchemeName] = useState('');
  const [scheme, setScheme] = useState(null);
  const [summary, setSummary] = useState(null);
  const [roomTemplates, setRoomTemplates] = useState([]);
  const [allItems, setAllItems] = useState([]);
  const [allPacks, setAllPacks] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [lightboxItemId, setLightboxItemId] = useState(null);

  useEffect(() => {
    if (!id) return;
    schemesApi
      .findById(id)
      .then(setScheme)
      .catch((err) => setError(err.message));
  }, [id]);

  useEffect(() => {
    if (!scheme) return;
    Promise.all([roomsApi.findAll(), itemsApi.findAll(), packsApi.findAll()])
      .then(([rooms, items, packs]) => {
        setRoomTemplates(rooms);
        setAllItems(items);
        setAllPacks(packs);
      })
      .catch((err) => setError(err.message));
    reloadSummary();
  }, [scheme]);

  const reloadSummary = () => {
    if (!scheme) return;
    schemesApi
      .getSummary(scheme.id)
      .then(setSummary)
      .catch((err) => setError(err.message));
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const created = await schemesApi.create({ name: schemeName });
      setScheme(created);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddRoom = async (roomId) => {
    setError(null);
    try {
      await schemesApi.addRoom(scheme.id, { roomId });
      reloadSummary();
    } catch (err) {
      setError(err.message);
    }
  };

  if (id && !scheme && !error) return null;

  if (!scheme) {
    return (
      <div>
        <div className="page-header">
          <h1 className="page-title">New Scheme</h1>
        </div>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleCreate} style={{ maxWidth: 420 }}>
          <div className="field" style={{ marginBottom: '1rem' }}>
            <label className="field-label" htmlFor="scheme-name">
              Scheme name
            </label>
            <input
              id="scheme-name"
              className="input"
              type="text"
              value={schemeName}
              onChange={(e) => setSchemeName(e.target.value)}
              placeholder="e.g. 14 Maple Street"
              required
              autoFocus
            />
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating…' : 'Create Scheme'}
          </button>
        </form>
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">{scheme.name}</h1>
        <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
          <span className="price">
            Total: {formatPrice(summary?.totalPrice ?? 0)}
          </span>
          <button
            className="btn btn-primary"
            onClick={() => navigate(`/schemes/${scheme.id}`)}
          >
            View Summary
          </button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="section">
        <h2 className="section-title">Add a Room</h2>
        {roomTemplates.length === 0 ? (
          <p className="empty-msg">
            No room templates found. Add some in{' '}
            <a href="/rooms">Rooms</a>.
          </p>
        ) : (
          <div className="room-grid">
            {roomTemplates.map((room) => (
              <div key={room.id} className="room-tile">
                <span className="room-tile-name">{room.name}</span>
                <button
                  className="btn btn-ghost btn-sm"
                  onClick={() => handleAddRoom(room.id)}
                >
                  + Add
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="section">
        <h2 className="section-title">Scheme Rooms</h2>
        {!summary || summary.rooms.length === 0 ? (
          <p className="empty-msg">No rooms added yet.</p>
        ) : (
          summary.rooms.map((schemeRoom) => (
            <SchemeRoomEditor
              key={schemeRoom.id}
              schemeRoom={schemeRoom}
              schemeId={scheme.id}
              allItems={allItems}
              allPacks={allPacks}
              onUpdate={reloadSummary}
              onOpenLightbox={setLightboxItemId}
            />
          ))
        )}
      </div>
      <Lightbox itemId={lightboxItemId} onClose={() => setLightboxItemId(null)} />
    </div>
  );
}
