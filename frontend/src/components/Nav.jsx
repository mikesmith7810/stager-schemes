import { NavLink } from 'react-router-dom';
import logo from '../../assets/logo-white-transparent.png';

export default function Nav({ onLogout, username }) {
  return (
    <nav className="nav">
      <div className="nav-brand-bar">
        <img src={logo} alt="Sea Glass Home Designs" className="nav-logo" />
        <span className="nav-brand-title">Sea Glass Home Designs — Stager Schemes</span>
      </div>
      <div className="nav-links-bar">
        <div className="nav-links-inner">
          <div className="nav-links">
            <NavLink
              to="/"
              end
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
            >
              Schemes
            </NavLink>
            <NavLink
              to="/rooms"
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
            >
              Rooms
            </NavLink>
            <NavLink
              to="/packs"
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
            >
              Packs
            </NavLink>
            <NavLink
              to="/items"
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
            >
              Items
            </NavLink>
          </div>
          <div className="nav-user">
            <NavLink
              to="/admin"
              className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
            >
              Admin
            </NavLink>
            <button className="btn btn-ghost btn-sm" onClick={onLogout}>
              Sign out
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
