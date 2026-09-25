import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { itemsApi } from '../api/items.js';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price ?? 0);
}

export default function Bin() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);

  const load = () =>
    itemsApi.findDeleted().then(setItems).catch((err) => setError(err.message));

  useEffect(() => {
    load();
  }, []);

  const handleRestore = async (item) => {
    setError(null);
    try {
      await itemsApi.restore(item.id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleEmptyBin = async () => {
    setError(null);
    try {
      await itemsApi.emptyBin();
      setItems([]);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Bin</h1>
        <Link to="/items" className="btn btn-ghost btn-sm">← Back to Items</Link>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {items.length === 0 ? (
        <p className="empty-msg">The bin is empty.</p>
      ) : (
        <>
          <div style={{ marginBottom: '1rem' }}>
            <button className="btn btn-danger btn-sm" onClick={handleEmptyBin}>
              Empty Bin
            </button>
          </div>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Price</th>
                  <th>Category</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td>{item.name}</td>
                    <td>
                      <span className="price">{formatPrice(item.price)}</span>
                    </td>
                    <td>
                      <span className="price-muted">{item.category || 'Unassigned'}</span>
                    </td>
                    <td>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => handleRestore(item)}
                      >
                        Restore
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
