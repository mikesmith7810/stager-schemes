import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { schemesApi } from '../api/schemes.js';

function formatPrice(price) {
  return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(price);
}

function SchemeRow({ scheme, onDelete, onToggleTemplate, onDuplicate, onRename }) {
  const [renaming, setRenaming] = useState(false);
  const [renameName, setRenameName] = useState(scheme.name);
  const [duplicating, setDuplicating] = useState(false);
  const [duplicateName, setDuplicateName] = useState('');
  const [saving, setSaving] = useState(false);

  const handleRenameSubmit = async (e) => {
    e.preventDefault();
    if (!renameName.trim()) return;
    setSaving(true);
    await onRename(scheme, renameName.trim());
    setSaving(false);
    setRenaming(false);
  };

  const handleRenameCancel = () => {
    setRenameName(scheme.name);
    setRenaming(false);
  };

  const handleDuplicateSubmit = async (e) => {
    e.preventDefault();
    if (!duplicateName.trim()) return;
    setSaving(true);
    await onDuplicate(scheme, duplicateName.trim());
    setSaving(false);
    setDuplicating(false);
    setDuplicateName('');
  };

  const handleDuplicateCancel = () => {
    setDuplicating(false);
    setDuplicateName('');
  };

  return (
    <>
      <tr>
        <td>
          {renaming ? (
            <form onSubmit={handleRenameSubmit} className="inline-rename-form">
              <input
                className="input input-sm"
                value={renameName}
                onChange={(e) => setRenameName(e.target.value)}
                autoFocus
                required
              />
              <button type="submit" className="btn btn-primary btn-sm" disabled={saving}>
                {saving ? 'Saving…' : 'Save'}
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={handleRenameCancel}
                disabled={saving}
              >
                Cancel
              </button>
            </form>
          ) : (
            <span className="scheme-name-cell">
              <Link to={`/schemes/${scheme.id}`}>{scheme.name}</Link>
              <button
                className="btn-icon"
                title="Rename"
                onClick={() => {
                  setRenameName(scheme.name);
                  setRenaming(true);
                }}
              >
                ✎
              </button>
            </span>
          )}
        </td>
        <td>
          <span className="price">{formatPrice(scheme.totalPrice)}</span>
        </td>
        <td>
          <div className="td-actions">
            {scheme.template && !duplicating && (
              <button className="btn btn-primary btn-sm" onClick={() => setDuplicating(true)}>
                Duplicate
              </button>
            )}
            <Link to={`/schemes/${scheme.id}/edit`} className="btn btn-secondary btn-sm">
              Edit
            </Link>
            <Link to={`/schemes/${scheme.id}`} className="btn btn-secondary btn-sm">
              View
            </Link>
            <button className="btn btn-secondary btn-sm" onClick={() => onToggleTemplate(scheme)}>
              {scheme.template ? 'Remove Template' : 'Make Template'}
            </button>
            <button className="btn btn-danger btn-sm" onClick={() => onDelete(scheme)}>
              Delete
            </button>
          </div>
        </td>
      </tr>
      {duplicating && (
        <tr className="duplicate-name-row">
          <td colSpan={3}>
            <form onSubmit={handleDuplicateSubmit} className="inline-rename-form">
              <label className="field-label" style={{ marginRight: '0.5rem' }}>
                New scheme name:
              </label>
              <input
                className="input input-sm"
                value={duplicateName}
                onChange={(e) => setDuplicateName(e.target.value)}
                placeholder="e.g. 14 Maple Street"
                autoFocus
                required
              />
              <button type="submit" className="btn btn-primary btn-sm" disabled={saving}>
                {saving ? 'Creating…' : 'Create'}
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={handleDuplicateCancel}
                disabled={saving}
              >
                Cancel
              </button>
            </form>
          </td>
        </tr>
      )}
    </>
  );
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
    if (!window.confirm(`Delete scheme "${scheme.name}"? This cannot be undone.`)) return;
    try {
      await schemesApi.delete(scheme.id);
      setSchemes((prev) => prev.filter((s) => s.id !== scheme.id));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleToggleTemplate = async (scheme) => {
    try {
      const updated = await schemesApi.setTemplate(scheme.id, !scheme.template);
      setSchemes((prev) => prev.map((s) => (s.id === scheme.id ? updated : s)));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDuplicate = async (scheme, name) => {
    setError(null);
    try {
      const created = await schemesApi.duplicate(scheme.id, name);
      navigate(`/schemes/${created.id}/edit`);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRename = async (scheme, name) => {
    setError(null);
    try {
      const updated = await schemesApi.rename(scheme.id, name);
      setSchemes((prev) => prev.map((s) => (s.id === scheme.id ? updated : s)));
    } catch (err) {
      setError(err.message);
    }
  };

  const templates = schemes.filter((s) => s.template);
  const regular = schemes.filter((s) => !s.template);

  const schemeTable = (rows) => (
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
          {rows.map((scheme) => (
            <SchemeRow
              key={scheme.id}
              scheme={scheme}
              onDelete={handleDelete}
              onToggleTemplate={handleToggleTemplate}
              onDuplicate={handleDuplicate}
              onRename={handleRename}
            />
          ))}
        </tbody>
      </table>
    </div>
  );

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

      {!loading && (
        <>
          {templates.length > 0 && (
            <div style={{ marginBottom: '2rem' }}>
              <h2 className="section-title">Templates</h2>
              {schemeTable(templates)}
            </div>
          )}

          <div>
            <h2 className="section-title">Schemes</h2>
            {regular.length === 0 ? (
              <p className="empty-msg">No schemes yet. Create your first one above.</p>
            ) : (
              schemeTable(regular)
            )}
          </div>
        </>
      )}
    </div>
  );
}
