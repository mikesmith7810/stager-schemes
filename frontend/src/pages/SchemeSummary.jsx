import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';
import Lightbox from '../components/Lightbox.jsx';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

function PackBreakdown({ pack, onOpenLightbox }) {
  return (
    <div style={{ paddingLeft: '1rem', marginBottom: '0.5rem' }}>
      <div style={{ fontWeight: 600, marginBottom: '0.25rem' }}>{pack.packName}</div>
      {pack.packItems.map((pi) => (
        <div key={pi.itemId} className="line-item" style={{ fontSize: '0.88rem' }}>
          <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
            <img
              src={`/api/items/${pi.itemId}/image`}
              className="item-thumbnail-sm"
              alt=""
              onClick={() => onOpenLightbox(pi.itemId)}
              onError={(e) => { e.currentTarget.style.display = 'none'; }}
            />
            <span>{pi.itemName} &times; {pi.quantity}</span>
          </div>
          <span className="price-muted">{formatPrice(pi.itemPrice * pi.quantity)}</span>
        </div>
      ))}
      <div
        style={{
          textAlign: 'right',
          fontSize: '0.9rem',
          fontWeight: 600,
          paddingTop: '0.25rem',
        }}
      >
        Pack total: {formatPrice(pack.packTotal)}
      </div>
    </div>
  );
}

function SchemeRoomSummary({ room, onOpenLightbox }) {
  return (
    <div className="summary-section">
      <h2 className="summary-room-title">{room.name}</h2>

      {room.items.length > 0 && (
        <div style={{ marginBottom: '1rem' }}>
          <p className="sub-section-title">Items</p>
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: '40px' }}></th>
                <th>Item</th>
                <th>Price</th>
                <th>Qty</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {room.items.map((item) => (
                <tr key={item.id}>
                  <td>
                    <img
                      src={`/api/items/${item.itemId}/image`}
                      className="item-thumbnail-sm"
                      alt=""
                      onClick={() => onOpenLightbox(item.itemId)}
                      onError={(e) => { e.currentTarget.style.display = 'none'; }}
                    />
                  </td>
                  <td>{item.itemName}</td>
                  <td>{formatPrice(item.itemPrice)}</td>
                  <td>{item.quantity}</td>
                  <td>
                    <span className="price">{formatPrice(item.lineTotal)}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {room.packs.length > 0 && (
        <div>
          <p className="sub-section-title">Packs</p>
          {room.packs.map((pack) => (
            <PackBreakdown key={pack.id} pack={pack} onOpenLightbox={onOpenLightbox} />
          ))}
        </div>
      )}

      <div style={{ textAlign: 'right', marginTop: '0.5rem', fontWeight: 600 }}>
        Room total: {formatPrice(room.roomTotal)}
      </div>
    </div>
  );
}

export default function SchemeSummary() {
  const { id } = useParams();
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [lightboxItemId, setLightboxItemId] = useState(null);

  useEffect(() => {
    schemesApi
      .getSummary(id)
      .then(setSummary)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return null;
  if (error) return <div className="error-msg">{error}</div>;
  if (!summary) return null;

  return (
    <div>
      <div className="page-header screen-only">
        <h1 className="page-title">{summary.name}</h1>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <Link to="/" className="btn btn-secondary">
            ← Back
          </Link>
          <Link to={`/schemes/${id}/edit`} className="btn btn-secondary">
            Edit
          </Link>
          <button className="btn btn-primary" onClick={() => window.print()}>
            Print
          </button>
        </div>
      </div>

      <h1 style={{ marginBottom: '1.5rem', display: 'none' }} className="print-header">
        {summary.name}
      </h1>

      {summary.rooms.length === 0 ? (
        <p className="empty-msg">This scheme has no rooms yet.</p>
      ) : (
        summary.rooms.map((room) => (
          <SchemeRoomSummary key={room.id} room={room} onOpenLightbox={setLightboxItemId} />
        ))
      )}

      <div className="summary-total-bar">
        <span>Total</span>
        <span>{formatPrice(summary.totalPrice)}</span>
      </div>
      <Lightbox itemId={lightboxItemId} onClose={() => setLightboxItemId(null)} />
    </div>
  );
}
