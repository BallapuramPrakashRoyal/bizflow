import AppLayout from "../components/layout/AppLayout";
import { useAuth } from "../context/AuthContext";

export default function Profile() {
  const { user } = useAuth();
  const initials = user?.name?.split(" ").map((part) => part[0]).join("").slice(0, 2).toUpperCase() || "B";

  return (
    <AppLayout>
      <section className="page-heading">
        <div>
          <p className="page-eyebrow">Account</p>
          <h2>Your profile</h2>
          <p>Manage your BizFlow account information.</p>
        </div>
      </section>
      <section className="profile-card">
        <div className="profile-avatar">{initials}</div>
        <div>
          <h3>{user?.name || "BizFlow user"}</h3>
          <p>{user?.email || "No email available"}</p>
          {user?.createdAt && <small>Member since {new Date(user.createdAt).toLocaleDateString()}</small>}
        </div>
      </section>
    </AppLayout>
  );
}
