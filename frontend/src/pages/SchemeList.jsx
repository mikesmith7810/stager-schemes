import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price);
}

export default function SchemeList() {
  const [schemes, setSchemes] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [newSchemeName, setNewSchemeName] = useState('');
  const [creating, setCreating] = useState(false);
  const navigate = useNavigate();

  const loadSchemes = () => {
    setLoading(true);
    schemesApi
      .findAll()
      .then(setSchemes)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSchemes();
  }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    setError(null);
    setCreating(true);
    try {
      const created = await schemesApi.create({ name: newSchemeName });
      navigate(`/schemes/${created.id}/edit`);
    } catch (err) {
      setError(err.message);
      setCreating(false);
    }
  };

  const handleDelete = async (scheme) => {
    try {
      await schemesApi.delete(scheme.id);
      setSchemes((prev) => prev.filter((s) => s.id !== scheme.id));
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Schemes</h1>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <form onSubmit={handleAdd} className="form-row" style={{ marginBottom: '1.5rem' }}>
        <div className="field">
          <label className="field-label">Scheme name</label>
          <input
            className="input"
            value={newSchemeName}
            onChange={(e) => setNewSchemeName(e.target.value)}
            placeholder="e.g. 14 Maple Street"
            required
          />
        </div>
        <button
          type="submit"
          className="btn btn-primary"
          style={{ alignSelf: 'flex-end' }}
          disabled={creating}
        >
          {creating ? 'Creating…' : '+ Add Scheme'}
        </button>
      </form>

      {loading ? null : schemes.length === 0 ? (
        <p className="empty-msg">No schemes yet. Create your first one above.</p>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Total Price</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {schemes.map((scheme) => (
                <tr key={scheme.id}>
                  <td>
                    <Link to={`/schemes/${scheme.id}`}>{scheme.name}</Link>
                  </td>
                  <td>
                    <span className="price">{formatPrice(scheme.totalPrice)}</span>
                  </td>
                  <td>
                    <div className="td-actions">
                      <Link
                        to={`/schemes/${scheme.id}/edit`}
                        className="btn btn-secondary btn-sm"
                      >
                        Edit
                      </Link>
                      <Link
                        to={`/schemes/${scheme.id}`}
                        className="btn btn-secondary btn-sm"
                      >
                        View
                      </Link>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(scheme)}
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
