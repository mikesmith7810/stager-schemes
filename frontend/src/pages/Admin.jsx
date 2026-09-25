import { useEffect, useState } from 'react';
import { categoriesApi } from '../api/categories.js';
import { suppliersApi } from '../api/suppliers.js';
import { coloursApi } from '../api/colours.js';

function ReferenceSection({ title, items, onAdd, onDelete, error }) {
  const [newName, setNewName] = useState('');

  const handleAdd = async (e) => {
    e.preventDefault();
    await onAdd(newName);
    setNewName('');
  };

  return (
    <details className="category-section" style={{ marginBottom: '0.5rem' }}>
      <summary className="category-summary">
        {title}
        <span className="category-count">{items.length}</span>
      </summary>
      {error && <div className="error-msg" style={{ marginBottom: '0.5rem' }}>{error}</div>}
      {items.length > 0 && (
        <div className="table-container" style={{ marginBottom: '1rem' }}>
          <table className="data-table">
            <tbody>
              {items.map((item) => (
                <tr key={item.id}>
                  <td>{item.name}</td>
                  <td style={{ width: '80px', textAlign: 'right' }}>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => onDelete(item.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {items.length === 0 && (
        <p className="empty-msg" style={{ marginBottom: '1rem' }}>No entries yet.</p>
      )}
      <form onSubmit={handleAdd} className="form-row" style={{ marginBottom: '1rem' }}>
        <div className="field">
          <input
            className="input"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder={`New ${title.slice(0, -1).toLowerCase()} name`}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary btn-sm" style={{ alignSelf: 'flex-end' }}>
          Add
        </button>
      </form>
    </details>
  );
}

export default function Admin() {
  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [colours, setColours] = useState([]);
  const [categoryError, setCategoryError] = useState(null);
  const [supplierError, setSupplierError] = useState(null);
  const [colourError, setColourError] = useState(null);

  const loadCategories = () =>
    categoriesApi.findAll().then(setCategories).catch((err) => setCategoryError(err.message));

  const loadSuppliers = () =>
    suppliersApi.findAll().then(setSuppliers).catch((err) => setSupplierError(err.message));

  const loadColours = () =>
    coloursApi.findAll().then(setColours).catch((err) => setColourError(err.message));

  useEffect(() => {
    loadCategories();
    loadSuppliers();
    loadColours();
  }, []);

  const handleAddCategory = async (name) => {
    setCategoryError(null);
    try {
      await categoriesApi.create(name);
      loadCategories();
    } catch (err) {
      setCategoryError(err.message);
    }
  };

  const handleDeleteCategory = async (id) => {
    setCategoryError(null);
    try {
      await categoriesApi.remove(id);
      loadCategories();
    } catch (err) {
      setCategoryError(err.message);
    }
  };

  const handleAddSupplier = async (name) => {
    setSupplierError(null);
    try {
      await suppliersApi.create(name);
      loadSuppliers();
    } catch (err) {
      setSupplierError(err.message);
    }
  };

  const handleDeleteSupplier = async (id) => {
    setSupplierError(null);
    try {
      await suppliersApi.remove(id);
      loadSuppliers();
    } catch (err) {
      setSupplierError(err.message);
    }
  };

  const handleAddColour = async (name) => {
    setColourError(null);
    try {
      await coloursApi.create(name);
      loadColours();
    } catch (err) {
      setColourError(err.message);
    }
  };

  const handleDeleteColour = async (id) => {
    setColourError(null);
    try {
      await coloursApi.remove(id);
      loadColours();
    } catch (err) {
      setColourError(err.message);
    }
  };

  return (
    <div>
      <h1 className="page-title" style={{ marginBottom: '1.5rem' }}>Admin</h1>
      <ReferenceSection
        title="Categories"
        items={categories}
        onAdd={handleAddCategory}
        onDelete={handleDeleteCategory}
        error={categoryError}
      />
      <ReferenceSection
        title="Suppliers"
        items={suppliers}
        onAdd={handleAddSupplier}
        onDelete={handleDeleteSupplier}
        error={supplierError}
      />
      <ReferenceSection
        title="Colours"
        items={colours}
        onAdd={handleAddColour}
        onDelete={handleDeleteColour}
        error={colourError}
      />
    </div>
  );
}
