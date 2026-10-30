import AppLayout from "../components/layout/AppLayout";
import { Workspace } from "./Dashboard";
import { useAuth } from "../context/AuthContext";

export default function WorkspacePage({ section }) {
  const { user } = useAuth();
  return (
    <AppLayout>
      <section className="page-heading">
        <div>
          <p className="page-eyebrow">Workspace</p>
          <h2>{section === "activity" ? "Audit logs" : section[0].toUpperCase() + section.slice(1)}</h2>
          <p>Manage your work and keep your team aligned.</p>
        </div>
      </section>
      <Workspace activeSection={section} currentUser={user} onDataChange={async () => {}} />
    </AppLayout>
  );
}
