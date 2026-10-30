import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import AppLayout from "../components/layout/AppLayout";
import { useAuth } from "../context/AuthContext";

const statuses = [
  { key: "todoTasks", label: "To do", color: "blue" },
  { key: "inProgressTasks", label: "In progress", color: "violet" },
  { key: "inReviewTasks", label: "In review", color: "amber" },
  { key: "completedTasks", label: "Completed", color: "green" },
  { key: "cancelledTasks", label: "Cancelled", color: "red" },
];

function StatCard({ label, value, detail, tone }) {
  return (
    <article className={`saas-stat-card ${tone}`}>
      <div className="saas-stat-heading">
        <span>{label}</span>
        <span className="stat-dot" aria-hidden="true" />
      </div>
      <strong>{value ?? 0}</strong>
      <small>{detail}</small>
    </article>
  );
}

export default function DashboardHome() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboard = useCallback(async () => {
    setError("");
    try {
      const response = await api.get("/dashboard");
      setDashboard(response.data);
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to load your dashboard.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // The request synchronizes dashboard state with the API on mount.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void loadDashboard();
  }, [loadDashboard]);

  return (
    <AppLayout>
      <section className="page-heading dashboard-heading">
        <div>
          <p className="page-eyebrow">Overview</p>
          <h2>Good {new Date().getHours() < 12 ? "morning" : new Date().getHours() < 18 ? "afternoon" : "evening"}, {user?.name?.split(" ")[0] || "there"}</h2>
          <p>Here&apos;s what&apos;s happening across your workspace today.</p>
        </div>
        <div className="date-pill">{new Intl.DateTimeFormat("en", { month: "short", day: "numeric", year: "numeric" }).format(new Date())}</div>
      </section>

      {loading && <div className="state-card"><span className="spinner" /> Loading your workspace...</div>}
      {error && <div className="error-message" role="alert">{error}</div>}

      {!loading && dashboard && (
        <>
          <section className="saas-stats-grid" aria-label="Workspace summary">
            <StatCard label="Total projects" value={dashboard.totalProjects} detail="Across your organizations" tone="blue" />
            <StatCard label="Total tasks" value={dashboard.totalTasks} detail="Everything on your plate" tone="violet" />
            <StatCard label="My tasks" value={dashboard.myTasks} detail="Assigned to you" tone="amber" />
            <StatCard label="Completed tasks" value={dashboard.completedTasks} detail="Marked as done" tone="green" />
          </section>

          <section className="overview-card">
            <div className="section-heading">
              <div>
                <p className="page-eyebrow">Task health</p>
                <h3>Task overview</h3>
              </div>
              <span className="muted-label">{dashboard.totalTasks || 0} total tasks</span>
            </div>
            <div className="status-overview">
              {statuses.map(({ key, label, color }) => {
                const value = dashboard[key] || 0;
                const percentage = dashboard.totalTasks ? Math.round((value / dashboard.totalTasks) * 100) : 0;
                return (
                  <div className="status-row" key={key}>
                    <div className="status-row-label"><span className={`status-marker ${color}`} />{label}<strong>{value}</strong></div>
                    <div className="status-track"><span className={`status-fill ${color}`} style={{ width: `${percentage}%` }} /></div>
                    <small>{percentage}%</small>
                  </div>
                );
              })}
            </div>
          </section>
        </>
      )}
    </AppLayout>
  );
}
