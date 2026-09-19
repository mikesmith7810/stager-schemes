import { Fragment, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';
import logo from '../../assets/logo-black-write-trans.png';

const TODAY = new Intl.DateTimeFormat('en-GB').format(new Date());
const VAT_NUMBER = '526 6353 83';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

function parseVal(v) {
  return parseFloat(v) || 0;
}

export default function CustomerSummary() {
  const { id } = useParams();
  const [summary, setSummary] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [stagingCost, setStagingCost] = useState('0');
  const [designCost, setDesignCost] = useState('0');
  const [roomTotals, setRoomTotals] = useState({});
  const [itemNames, setItemNames] = useState({});
  const [costsInitialized, setCostsInitialized] = useState(false);

  useEffect(() => {
    schemesApi
      .getSummary(id)
      .then((data) => {
        setSummary(data);
        if (!costsInitialized) {
          setStagingCost(((data.transportCost ?? 0) + (data.stagingCost ?? 0)).toFixed(2));
          setDesignCost((data.designCost ?? 0).toFixed(2));
          const overrides = data.customerSummaryOverrides
            ? JSON.parse(data.customerSummaryOverrides)
            : null;
          const initialRoomTotals = {};
          const initialItemNames = {};
          data.rooms.forEach((r) => {
            initialRoomTotals[r.id] =
              overrides?.roomTotals?.[r.id] ?? ((r.roomTotal ?? 0) * 0.8).toFixed(2);
            r.items.forEach((item) => {
              const key = `r${r.id}-i${item.itemId}`;
              initialItemNames[key] = overrides?.itemNames?.[key] ?? item.itemName;
            });
            r.packs.forEach((pack) => {
              pack.packItems.forEach((pi) => {
                const key = `r${r.id}-p${pack.packId}-i${pi.itemId}`;
                initialItemNames[key] = overrides?.itemNames?.[key] ?? pi.itemName;
              });
            });
          });
          setRoomTotals(initialRoomTotals);
          setItemNames(initialItemNames);
          setCostsInitialized(true);
        }
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id]);

  useEffect(() => {
    if (!costsInitialized) return;
    const timer = setTimeout(() => {
      schemesApi.saveCustomerSummaryOverrides(id, JSON.stringify({ roomTotals, itemNames }));
    }, 600);
    return () => clearTimeout(timer);
  }, [roomTotals, itemNames, costsInitialized, id]);

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

  if (loading) return null;
  if (error) return <div className="error-msg">{error}</div>;
  if (!summary) return null;

  const stagingVal = parseVal(stagingCost);
  const designVal = parseVal(designCost);
  const itemsExVat = summary.rooms.reduce((s, r) => s + parseVal(roomTotals[r.id]), 0);
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

      <div className="inv-page">
        {/* Header: logo left, company details right */}
        <div className="inv-header">
          <img src={logo} alt="Sea Glass Home Designs" className="inv-logo" />
          <div className="inv-company-details">
            <div className="inv-company-name">Sea Glass Home Designs Limited</div>
            <div>Weyside</div>
            <div>Nottington</div>
            <div>Weymouth</div>
            <div>DT3 4BN</div>
            <div>Tel 01305 566203</div>
            <div>hello@seaglasshomedesigns.com</div>
            <div>VAT Number: {VAT_NUMBER}</div>
            <div>Company Number: 17173676</div>
          </div>
        </div>

        {/* Date + Quote Details */}
        <div className="inv-meta">
          <div>
            <div className="inv-meta-row">
              <span className="inv-meta-label">Date :</span>
              <span>{TODAY}</span>
            </div>
          </div>
          <div>
            <div className="inv-meta-section-title">Quote Details:</div>
            <div className="inv-meta-value">{summary.name}</div>
          </div>
        </div>

        {/* Service table */}
        <table className="inv-table">
          <thead>
            <tr>
              <th className="inv-th-service">Service</th>
              <th className="inv-th-num">Qty</th>
              <th className="inv-th-num">Ex VAT</th>
              <th className="inv-th-num">VAT</th>
              <th className="inv-th-num">Inc VAT</th>
            </tr>
          </thead>
          <tbody>
            {summary.rooms.map((room) => {
              const roomExVat = parseVal(roomTotals[room.id]);
              const roomVat = roomExVat * 0.2;
              const roomIncVat = roomExVat * 1.2;

              const allItems = [
                ...room.items.map((item) => ({
                  key: `r${room.id}-i${item.itemId}`,
                  name: item.itemName,
                  qty: item.quantity,
                })),
                ...room.packs.flatMap((pack) =>
                  pack.packItems.map((pi) => ({
                    key: `r${room.id}-p${pack.packId}-i${pi.itemId}`,
                    name: pi.itemName,
                    qty: pi.quantity,
                  }))
                ),
              ];

              return (
                <Fragment key={room.id}>
                  <tr className="inv-room-header">
                    <td colSpan={5}>{room.name}</td>
                  </tr>
                  {allItems.map((item) => (
                    <tr key={item.key} className="inv-item-row">
                      <td className="inv-item-indent">
                        <input
                          className="input input-sm screen-only"
                          type="text"
                          value={itemNames[item.key] ?? item.name}
                          onChange={(e) =>
                            setItemNames((prev) => ({ ...prev, [item.key]: e.target.value }))
                          }
                          style={{ width: '100%' }}
                        />
                        <span className="print-only">{itemNames[item.key] ?? item.name}</span>
                      </td>
                      <td className="inv-num">{item.qty}</td>
                      <td></td>
                      <td></td>
                      <td></td>
                    </tr>
                  ))}
                  <tr className="inv-room-total">
                    <td>Room Total</td>
                    <td></td>
                    <td className="inv-num">
                      <input
                        className="input input-sm screen-only"
                        type="number"
                        min="0"
                        step="0.01"
                        value={roomTotals[room.id] ?? ''}
                        onChange={(e) =>
                          setRoomTotals((prev) => ({ ...prev, [room.id]: e.target.value }))
                        }
                        style={{ width: 90, textAlign: 'right' }}
                      />
                      <span className="print-only">{formatPrice(roomExVat)}</span>
                    </td>
                    <td className="inv-num">{formatPrice(roomVat)}</td>
                    <td className="inv-num">{formatPrice(roomIncVat)}</td>
                  </tr>
                </Fragment>
              );
            })}

            {/* Staging */}
            <tr className="inv-cost-row">
              <td>Staging</td>
              <td></td>
              <td className="inv-num">
                <input
                  className="input input-sm screen-only"
                  type="number"
                  min="0"
                  step="0.01"
                  value={stagingCost}
                  onChange={(e) => setStagingCost(e.target.value)}
                  onBlur={handleSaveCosts}
                  style={{ width: 90, textAlign: 'right' }}
                />
                <span className="print-only">{formatPrice(stagingVal)}</span>
              </td>
              <td className="inv-num">{formatPrice(stagingVal * 0.2)}</td>
              <td className="inv-num">{formatPrice(stagingVal * 1.2)}</td>
            </tr>

            {/* Design */}
            <tr className="inv-cost-row">
              <td>Design</td>
              <td></td>
              <td className="inv-num">
                <input
                  className="input input-sm screen-only"
                  type="number"
                  min="0"
                  step="0.01"
                  value={designCost}
                  onChange={(e) => setDesignCost(e.target.value)}
                  onBlur={handleSaveCosts}
                  style={{ width: 90, textAlign: 'right' }}
                />
                <span className="print-only">{formatPrice(designVal)}</span>
              </td>
              <td className="inv-num">{formatPrice(designVal * 0.2)}</td>
              <td className="inv-num">{formatPrice(designVal * 1.2)}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr className="inv-total-row">
              <td>Total</td>
              <td></td>
              <td className="inv-num">{formatPrice(totalExVat)}</td>
              <td className="inv-num">{formatPrice(totalVat)}</td>
              <td className="inv-num">{formatPrice(totalIncVat)}</td>
            </tr>
          </tfoot>
        </table>

        <p className="inv-disclaimer">
          This quote is a guide and subject to change.
        </p>
      </div>
    </div>
  );
}
