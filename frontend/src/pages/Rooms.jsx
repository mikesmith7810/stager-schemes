import { useEffect, useState } from 'react';
import { itemsApi } from '../api/items.js';
import { packsApi } from '../api/packs.js';
import { roomsApi } from '../api/rooms.js';
import Lightbox from '../components/Lightbox.jsx';
import MoodBoard from '../components/MoodBoard.jsx';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

export default function Rooms() {
  const [rooms, setRooms] = useState([]);
  const [allItems, setAllItems] = useState([]);
  const [allPacks, setAllPacks] = useState([]);
  const [error, setError] = useState(null);
  const [newRoomName, setNewRoomName] = useState('');
  const [expandedRoomId, setExpandedRoomId] = useState(null);
  const [selectedItemId, setSelectedItemId] = useState('');
  const [selectedItemQty, setSelectedItemQty] = useState(1);
  const [selectedPackId, setSelectedPackId] = useState('');
  const [lightboxItemId, setLightboxItemId] = useState(null);
  const [moodBoardRoom, setMoodBoardRoom] = useState(null);

  const load = () =>
    Promise.all([roomsApi.findAll(), itemsApi.findAll(), packsApi.findAll()])
      .then(([r, i, p]) => {
        setRooms(r);
        setAllItems(i);
        setAllPacks(p);
      })
      .catch((err) => setError(err.message));

  useEffect(() => {
    load();
  }, []);

  const handleAddRoom = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await roomsApi.create({ name: newRoomName, items: [], packs: [] });
      setNewRoomName('');
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDeleteRoom = async (room) => {
    setError(null);
    try {
      await roomsApi.delete(room.id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAddItemToRoom = async (roomId) => {
    if (!selectedItemId) return;
    setError(null);
    try {
      await roomsApi.addItem(roomId, {
        itemId: Number(selectedItemId),
        quantity: Number(selectedItemQty),
      });
      setSelectedItemId('');
      setSelectedItemQty(1);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemoveItemFromRoom = async (roomId, itemId) => {
    setError(null);
    try {
      await roomsApi.removeItem(roomId, itemId);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAddPackToRoom = async (roomId) => {
    if (!selectedPackId) return;
    setError(null);
    try {
      await roomsApi.addPack(roomId, { packId: Number(selectedPackId), quantity: 1 });
      setSelectedPackId('');
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRemovePackFromRoom = async (roomId, packId) => {
    setError(null);
    try {
      await roomsApi.removePack(roomId, packId);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Rooms</h1>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleAddRoom} className="form-row" style={{ marginBottom: '1.5rem' }}>
        <div className="field">
          <label className="field-label">Room name</label>
          <input
            className="input"
            value={newRoomName}
            onChange={(e) => setNewRoomName(e.target.value)}
            placeholder="e.g. Master Bedroom"
            required
          />
        </div>
        <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-end' }}>
          + Add Room
        </button>
      </form>

      {rooms.length === 0 ? (
        <p className="empty-msg">No rooms yet.</p>
      ) : (
        rooms.map((room) => {
          const itemsTotal = room.items.reduce(
            (sum, ri) => sum + ri.itemPrice * ri.quantity,
            0
          );
          const packsTotal = room.packs.reduce(
            (sum, rp) => sum + rp.packTotal * rp.quantity,
            0
          );
          const roomTotal = itemsTotal + packsTotal;
          return (
          <div key={room.id} className="panel">
            <div className="panel-header">
              <button
                className="panel-title"
                style={{ background: 'none', border: 'none', cursor: 'pointer', font: 'inherit', color: 'inherit' }}
                onClick={() =>
                  setExpandedRoomId(expandedRoomId === room.id ? null : room.id)
                }
              >
                {expandedRoomId === room.id ? '▾' : '▸'} {room.name}
                <span className="price-muted" style={{ marginLeft: '0.5rem' }}>
                  ({room.items.length} items, {room.packs.length} packs — {formatPrice(roomTotal)})
                </span>
              </button>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => setMoodBoardRoom(room)}
                >
                  Mood Board
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDeleteRoom(room)}
                >
                  Delete
                </button>
              </div>
            </div>
            {expandedRoomId === room.id && (
              <div className="panel-body" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                  <p className="sub-section-title">Default Items</p>
                  {room.items.length === 0 && (
                    <p className="price-muted" style={{ marginBottom: '0.5rem' }}>No items.</p>
                  )}
                  {room.items.map((ri) => (
                    <div key={ri.itemId} className="line-item">
                      <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                        <img
                          src={`/api/items/${ri.itemId}/image`}
                          className="item-thumbnail-sm"
                          alt=""
                          onClick={() => setLightboxItemId(ri.itemId)}
                          onError={(e) => { e.currentTarget.style.display = 'none'; }}
                        />
                        <span>
                          {ri.itemName} &times; {ri.quantity}
                        </span>
                      </div>
                      <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                        <span className="price-muted">{formatPrice(ri.itemPrice)}</span>
                        <button
                          className="btn btn-ghost btn-sm"
                          onClick={() => handleRemoveItemFromRoom(room.id, ri.itemId)}
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
                          {item.name}
                        </option>
                      ))}
                    </select>
                    <input
                      className="input input-sm"
                      type="number"
                      min="1"
                      value={selectedItemQty}
                      onChange={(e) => setSelectedItemQty(e.target.value)}
                    />
                    <button
                      className="btn btn-ghost btn-sm"
                      onClick={() => handleAddItemToRoom(room.id)}
                      disabled={!selectedItemId}
                    >
                      Add
                    </button>
                  </div>
                </div>

                <div>
                  <p className="sub-section-title">Default Packs</p>
                  {room.packs.length === 0 && (
                    <p className="price-muted" style={{ marginBottom: '0.5rem' }}>No packs.</p>
                  )}
                  {room.packs.map((rp) => (
                    <div key={rp.packId}>
                      <div className="line-item">
                        <span style={{ fontWeight: 600 }}>{rp.packName}</span>
                        <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                          <span className="price-muted">{formatPrice(rp.packTotal)}</span>
                          <button
                            className="btn btn-ghost btn-sm"
                            onClick={() => handleRemovePackFromRoom(room.id, rp.packId)}
                          >
                            &times;
                          </button>
                        </div>
                      </div>
                      {rp.items.map((pi) => (
                        <div key={pi.itemId} className="line-item" style={{ paddingLeft: '1rem' }}>
                          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                            <img
                              src={`/api/items/${pi.itemId}/image`}
                              className="item-thumbnail-sm"
                              alt=""
                              onClick={() => setLightboxItemId(pi.itemId)}
                              onError={(e) => { e.currentTarget.style.display = 'none'; }}
                            />
                            <span className="price-muted">
                              {pi.itemName} &times; {pi.quantity}
                            </span>
                          </div>
                          <span className="price-muted">{formatPrice(pi.itemPrice)}</span>
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
                      onClick={() => handleAddPackToRoom(room.id)}
                      disabled={!selectedPackId}
                    >
                      Add
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        );})
      )}
      <Lightbox itemId={lightboxItemId} onClose={() => setLightboxItemId(null)} />
      {moodBoardRoom && (
        <MoodBoard room={moodBoardRoom} onClose={() => setMoodBoardRoom(null)} />
      )}
    </div>
  );
}
