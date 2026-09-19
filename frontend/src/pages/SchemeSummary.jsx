import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';
import Lightbox from '../components/Lightbox.jsx';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

function SchemeRoomSummary({ room, onOpenLightbox }) {
  const roomIncVat = room.roomTotal ?? 0;
  const roomExVat = roomIncVat * 0.8;

  return (
    <div className="summary-section">
      <h2 className="summary-room-title">{room.name}</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th style={{ width: '36px' }}></th>
            <th>Item</th>
            <th style={{ textAlign: 'right' }}>Qty</th>
            <th style={{ textAlign: 'right' }}>Ex VAT</th>
            <th style={{ textAlign: 'right' }}>Inc VAT</th>
          </tr>
        </thead>
        <tbody>
          {room.items.map((item) => (
            <tr key={item.itemId}>
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
              <td style={{ textAlign: 'right' }}>{item.quantity}</td>
              <td style={{ textAlign: 'right' }}>{formatPrice(item.lineTotal * 0.8)}</td>
              <td style={{ textAlign: 'right' }}>{formatPrice(item.lineTotal)}</td>
            </tr>
          ))}
          {room.packs.flatMap((pack) => [
            <tr key={`pack-header-${pack.packId}`}>
              <td></td>
              <td
                colSpan={4}
                style={{
                  fontWeight: 600,
                  fontSize: '0.85rem',
                  color: 'var(--text-muted, #888)',
                  paddingTop: '0.5rem',
                }}
              >
                {pack.packName}
              </td>
            </tr>,
            ...pack.packItems.map((pi) => (
              <tr key={`pack-${pack.packId}-item-${pi.itemId}`}>
                <td>
                  <img
                    src={`/api/items/${pi.itemId}/image`}
                    className="item-thumbnail-sm"
                    alt=""
                    onClick={() => onOpenLightbox(pi.itemId)}
                    onError={(e) => { e.currentTarget.style.display = 'none'; }}
                  />
                </td>
                <td style={{ paddingLeft: '1rem' }}>{pi.itemName}</td>
                <td style={{ textAlign: 'right' }}>{pi.quantity}</td>
                <td style={{ textAlign: 'right' }}>{formatPrice(pi.itemPrice * pi.quantity * 0.8)}</td>
                <td style={{ textAlign: 'right' }}>{formatPrice(pi.itemPrice * pi.quantity)}</td>
              </tr>
            )),
          ])}
        </tbody>
        <tfoot>
          <tr style={{ fontWeight: 600 }}>
            <td
              colSpan={3}
              style={{ textAlign: 'right', paddingTop: '0.5rem', borderTop: '2px solid var(--border, #e5e7eb)' }}
            >
              Room Total
            </td>
            <td
              style={{ textAlign: 'right', paddingTop: '0.5rem', borderTop: '2px solid var(--border, #e5e7eb)' }}
            >
              {formatPrice(roomExVat)}
            </td>
            <td
              style={{ textAlign: 'right', paddingTop: '0.5rem', borderTop: '2px solid var(--border, #e5e7eb)' }}
            >
              {formatPrice(roomIncVat)}
            </td>
          </tr>
        </tfoot>
      </table>
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

  const itemsIncVat = summary.rooms.reduce((s, r) => s + (r.roomTotal ?? 0), 0);
  const itemsExVat = itemsIncVat * 0.8;
  const totalVat = itemsIncVat * 0.2;
  const otherCosts =
    (summary.transportCost ?? 0) + (summary.stagingCost ?? 0) + (summary.designCost ?? 0);
  const grandTotal = itemsExVat + otherCosts;

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
          <Link to={`/schemes/${id}/customer-summary`} className="btn btn-primary">
            Print Customer Summary
          </Link>
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

      {summary.rooms.length > 0 && (
        <div className="summary-section">
          <div className="line-item" style={{ fontWeight: 600 }}>
            <span>Items Total Ex VAT</span>
            <span className="price">{formatPrice(itemsExVat)}</span>
          </div>
          <div className="line-item" style={{ fontWeight: 600 }}>
            <span>Items VAT</span>
            <span className="price">{formatPrice(totalVat)}</span>
          </div>
          <div className="line-item" style={{ fontWeight: 600 }}>
            <span>Items Total Inc VAT</span>
            <span className="price">{formatPrice(itemsIncVat)}</span>
          </div>
        </div>
      )}

      <div className="summary-section">
        <h2 className="summary-room-title">Other Costs</h2>
        {[
          { label: 'Transport', value: summary.transportCost },
          { label: 'Staging', value: summary.stagingCost },
          { label: 'Design', value: summary.designCost },
        ].map(({ label, value }) => (
          <div key={label} className="line-item">
            <span>{label}</span>
            <span className="price">{formatPrice(value ?? 0)}</span>
          </div>
        ))}
      </div>

      <div className="summary-total-bar">
        <span>Total (Items Ex VAT + Other Costs)</span>
        <span>{formatPrice(grandTotal)}</span>
      </div>

      <Lightbox itemId={lightboxItemId} onClose={() => setLightboxItemId(null)} />
    </div>
  );
}
