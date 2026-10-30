import { useState } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const navigation = [
  { label: "Dashboard", path: "/dashboard", icon: "▦" },
  { label: "Organizations", path: "/organizations", icon: "◫" },
  { label: "Projects", path: "/projects", icon: "◇" },
  { label: "Tasks", path: "/tasks", icon: "✓" },
  { label: "Audit Logs", path: "/audit-logs", icon: "≡" },
];

function getPageTitle(pathname) {
  const item = navigation.find(({ path }) => pathname.startsWith(path));
  return item?.label || "Workspace";
}

function Avatar({ name }) {
  const initials = name
    ?.split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase() || "B";

  return <span className="avatar" aria-hidden="true">{initials}</span>;
}

export default function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <div className="app-shell">
      <button
        className={`sidebar-backdrop ${mobileOpen ? "is-visible" : ""}`}
        type="button"
        aria-label="Close navigation"
        onClick={() => setMobileOpen(false)}
      />
      <aside className={`app-sidebar ${mobileOpen ? "is-open" : ""}`}>
        <div className="sidebar-brand">
          <span className="brand-mark">B</span>
          <span>BizFlow</span>
        </div>

        <div className="sidebar-label">Workspace</div>
        <nav className="sidebar-nav" aria-label="Main navigation">
          {navigation.map(({ label, path, icon }) => (
            <NavLink
              key={path}
              to={path}
              className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
              onClick={() => setMobileOpen(false)}
            >
              <span className="sidebar-link-icon" aria-hidden="true">{icon}</span>
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <NavLink className="sidebar-link" to="/profile" onClick={() => setMobileOpen(false)}>
            <span className="sidebar-link-icon" aria-hidden="true">◉</span>
            <span>Profile</span>
          </NavLink>
          <div className="sidebar-user">
            <Avatar name={user?.name} />
            <div className="sidebar-user-copy">
              <strong>{user?.name || "BizFlow user"}</strong>
              <span>{user?.email || "Signed in"}</span>
            </div>
            <button className="logout-icon" type="button" onClick={handleLogout} aria-label="Log out">↪</button>
          </div>
        </div>
      </aside>

      <div className="app-main">
        <header className="app-topbar">
          <button
            className="menu-button"
            type="button"
            aria-label="Open navigation"
            aria-expanded={mobileOpen}
            onClick={() => setMobileOpen(true)}
          >
            ☰
          </button>
          <div>
            <p className="topbar-kicker">BizFlow workspace</p>
            <h1>{getPageTitle(location.pathname)}</h1>
          </div>
          <div className="topbar-actions">
            <button className="notification-button" type="button" aria-label="Notifications">♢</button>
            <div className="topbar-user">
              <Avatar name={user?.name} />
              <span>{user?.name || "Account"}</span>
            </div>
          </div>
        </header>
        <main className="app-content">{children}</main>
      </div>
    </div>
  );
}
