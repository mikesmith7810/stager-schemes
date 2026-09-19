import { useEffect, useState } from 'react';
import { BrowserRouter, Navigate, Outlet, Route, Routes } from 'react-router-dom';

import Nav from './components/Nav.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import { authApi } from './api/auth.js';
import Login from './pages/Login.jsx';
import SchemeList from './pages/SchemeList.jsx';
import AddScheme from './pages/AddScheme.jsx';
import SchemeSummary from './pages/SchemeSummary.jsx';
import CustomerSummary from './pages/CustomerSummary.jsx';
import Rooms from './pages/Rooms.jsx';
import Packs from './pages/Packs.jsx';
import Items from './pages/Items.jsx';

function Layout({ onLogout, username }) {
  return (
    <>
      <Nav onLogout={onLogout} username={username} />
      <main className="main">
        <Outlet />
      </main>
    </>
  );
}

export default function App() {
  const [authStatus, setAuthStatus] = useState({ checking: true, user: null });

  useEffect(() => {
    authApi
      .getCurrentUser()
      .then((user) => setAuthStatus({ checking: false, user }))
      .catch(() => setAuthStatus({ checking: false, user: null }));
  }, []);

  const handleLogin = (user) => setAuthStatus({ checking: false, user });

  const handleLogout = () => {
    authApi.logout().finally(() => setAuthStatus({ checking: false, user: null }));
  };

  if (authStatus.checking) {
    return null;
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={
            authStatus.user ? (
              <Navigate to="/" replace />
            ) : (
              <Login onLogin={handleLogin} />
            )
          }
        />
        <Route element={<ProtectedRoute user={authStatus.user} />}>
          <Route
            element={
              <Layout onLogout={handleLogout} username={authStatus.user?.username} />
            }
          >
            <Route path="/" element={<SchemeList />} />
            <Route path="/schemes/new" element={<AddScheme />} />
            <Route path="/schemes/:id/edit" element={<AddScheme />} />
            <Route path="/schemes/:id" element={<SchemeSummary />} />
            <Route path="/schemes/:id/customer-summary" element={<CustomerSummary />} />
            <Route path="/rooms" element={<Rooms />} />
            <Route path="/packs" element={<Packs />} />
            <Route path="/items" element={<Items />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
