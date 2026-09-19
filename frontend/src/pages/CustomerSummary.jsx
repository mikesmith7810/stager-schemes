import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

function parseVal(v) {
  return parseFloat(v) || 0;
}

function buildItemKey(roomId, itemId) {
  return `room-${roomId}-item-${itemId}`;
}

function buildPackItemKey(roomId, packId, itemId) {
  return `room-${roomId}-pack-${packId}-item-${itemId}`;
}

function initPrices(rooms) {
  const prices = {};
  rooms.forEach((room) => {
    room.items.forEach((item) => {
      prices[buildItemKey(room.id, item.itemId)] = (item.lineTotal * 0.8).toFixed(2);
    });
    room.packs.forEach((pack) => {
      pack.packItems.forEach((pi) => {
        prices[buildPackItemKey(room.id, pack.packId, pi.itemId)] = (
          pi.itemPrice * pi.quantity * 0.8
        ).toFixed(2);
      });
    });
  });
  return prices;
}

export default function CustomerSummary() {
  const { id } = useParams();
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [stagingCost, setStagingCost] = useState('0');
  const [designCost, setDesignCost] = useState('0');
  const [itemPrices, setItemPrices] = useState(null);
  const [costsInitialized, setCostsInitialized] = useState(false);

  useEffect(() => {
    schemesApi
      .getSummary(id)
      .then((data) => {
        setSummary(data);
        if (!costsInitialized) {
          setStagingCost(
            ((data.transportCost ?? 0) + (data.stagingCost ?? 0)).toFixed(2)
          );
          setDesignCost((data.designCost ?? 0).toFixed(2));
          setItemPrices(initPrices(data.rooms));
          setCostsInitialized(true);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  const setItemPrice = (key, value) =>
    setItemPrices((prev) => ({ ...prev, [key]: value }));

  const handleSaveCosts = async () => {
    if (!summary) return;
    try {
      await schemesApi.update(id, {
        name: summary.name,
        transportCost: 0,
        stagingCost: parseVal(stagingCost),
        designCost: parseVal(designCost),
      });
    } catch (err) {
      setError(err.message);
    }
  };

  if (loading || !itemPrices) return null;
  if (error) return <div className="error-msg">{error}</div>;
  if (!summary) return null;

  const stagingVal = parseVal(stagingCost);
  const designVal = parseVal(designCost);
  const itemsExVat = Object.values(itemPrices).reduce((s, v) => s + parseVal(v), 0);
  const totalExVat = itemsExVat + stagingVal + designVal;
  const totalVat = totalExVat * 0.2;
  const totalIncVat = totalExVat * 1.2;

  return (
    <div>
      <div className="page-header screen-only">
        <h1 className="page-title">{summary.name} — Customer Summary</h1>
        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button className="btn btn-primary" onClick={() => window.print()}>
            Print to PDF
          </button>
          <Link to={`/schemes/${id}`} className="btn btn-secondary">
            ← Back
          </Link>
        </div>
      </div>

      <h1 style={{ marginBottom: '1.5rem', display: 'none' }} className="print-header">
        {summary.name}
      </h1>

      <div className="cs-col-header">
        <span>Item</span>
        <span>Ex VAT</span>
        <span>VAT (20%)</span>
      </div>

      {summary.rooms.length === 0 ? (
        <p className="empty-msg">This scheme has no rooms yet.</p>
      ) : (
        summary.rooms.map((room) => (
          <div key={room.id} className="summary-section">
            <h2 className="summary-room-title">{room.name}</h2>

            {room.items.map((item) => {
              const key = buildItemKey(room.id, item.itemId);
              const exVat = parseVal(itemPrices[key]);
              return (
                <div key={key} className="cs-row">
                  <span>
                    {item.itemName} &times; {item.quantity}
                  </span>
                  <div>
                    <input
                      className="input input-sm screen-only"
                      type="number"
                      min="0"
                      step="0.01"
                      value={itemPrices[key]}
                      onChange={(e) => setItemPrice(key, e.target.value)}
                      style={{ width: 100 }}
                    />
                    <span className="print-only">{formatPrice(exVat)}</span>
                  </div>
                  <span className="price-muted">{formatPrice(exVat * 0.2)}</span>
                </div>
              );
            })}

            {room.packs.flatMap((pack) => [
              <div key={`pack-label-${pack.packId}`} className="cs-pack-label">
                {pack.packName}
              </div>,
              ...pack.packItems.map((pi) => {
                const key = buildPackItemKey(room.id, pack.packId, pi.itemId);
                const exVat = parseVal(itemPrices[key]);
                return (
                  <div key={key} className="cs-row cs-row-indent">
                    <span>
                      {pi.itemName} &times; {pi.quantity}
                    </span>
                    <div>
                      <input
                        className="input input-sm screen-only"
                        type="number"
                        min="0"
                        step="0.01"
                        value={itemPrices[key]}
                        onChange={(e) => setItemPrice(key, e.target.value)}
                        style={{ width: 100 }}
                      />
                      <span className="print-only">{formatPrice(exVat)}</span>
                    </div>
                    <span className="price-muted">{formatPrice(exVat * 0.2)}</span>
                  </div>
                );
              }),
            ])}
          </div>
        ))
      )}

      <div className="summary-section">
        <h2 className="summary-room-title">Costs</h2>
        {[
          { label: 'Staging', value: stagingCost, set: setStagingCost },
          { label: 'Design', value: designCost, set: setDesignCost },
        ].map(({ label, value, set }) => {
          const exVat = parseVal(value);
          return (
            <div key={label} className="cs-row">
              <span>{label}</span>
              <div>
                <input
                  className="input input-sm screen-only"
                  type="number"
                  min="0"
                  step="0.01"
                  value={value}
                  onChange={(e) => set(e.target.value)}
                  onBlur={handleSaveCosts}
                  style={{ width: 100 }}
                />
                <span className="print-only">{formatPrice(exVat)}</span>
              </div>
              <span className="price-muted">{formatPrice(exVat * 0.2)}</span>
            </div>
          );
        })}
      </div>

      <div className="cs-totals">
        <div className="cs-total-row">
          <span>Total Ex VAT</span>
          <span>{formatPrice(totalExVat)}</span>
        </div>
        <div className="cs-total-row">
          <span>Total VAT (20%)</span>
          <span>{formatPrice(totalVat)}</span>
        </div>
        <div className="cs-total-row cs-grand-total">
          <span>Total Inc VAT</span>
          <span>{formatPrice(totalIncVat)}</span>
        </div>
      </div>
    </div>
  );
}
