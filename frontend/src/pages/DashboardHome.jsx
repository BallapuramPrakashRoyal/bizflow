import { useCallback, useEffect, useMemo, useState } from "react";
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

function ProgressRing({ value }) {
  const radius = 30;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (value / 100) * circumference;

  return (
    <div className="progress-ring" aria-label={`${value}% complete`}>
      <svg viewBox="0 0 76 76" role="img">
        <circle className="progress-ring-track" cx="38" cy="38" r={radius} />
        <circle className="progress-ring-value" cx="38" cy="38" r={radius} style={{ strokeDasharray: circumference, strokeDashoffset: offset }} />
      </svg>
      <strong>{value}%</strong>
    </div>
  );
}

export default function DashboardHome() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [workflows, setWorkflows] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const projectMetrics = useMemo(() => projects.map((project) => {
    const projectTasks = tasks.filter((task) => task.projectId === project.id);
    const completed = projectTasks.filter((task) => task.status === "DONE").length;
    return {
      ...project,
      taskCount: projectTasks.length,
      completed,
      progress: projectTasks.length ? Math.round((completed / projectTasks.length) * 100) : 0,
      stages: workflows[project.id] || [],
    };
  }), [projects, tasks, workflows]);
  const workflowStages = projectMetrics.flatMap((project) => project.stages);
  const completedWorkflowStages = workflowStages.filter((stage) => stage.status === "COMPLETED").length;
  const workflowCompletion = workflowStages.length
    ? Math.round((completedWorkflowStages / workflowStages.length) * 100)
    : 0;
  const currentWorkflowStage = workflowStages.find((stage) => stage.status === "IN_PROGRESS")
    || workflowStages.find((stage) => stage.status === "PLANNED");
  const nextTask = tasks.find((task) => task.status !== "DONE" && task.status !== "CANCELLED");

  const updateWorkflowStage = async (projectId, stageId, status) => {
    try {
      await api.put(`/projects/${projectId}/workflow/${stageId}`, { status });
      await loadDashboard();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to update workflow stage.");
    }
  };
  const actualCompletion = tasks.length
    ? Math.round((tasks.filter((task) => task.status === "DONE").length / tasks.length) * 100)
    : 0;

  const loadDashboard = useCallback(async () => {
    setError("");
    try {
      const [dashboardResponse, projectsResponse, tasksResponse] = await Promise.all([
        api.get("/dashboard"),
        api.get("/projects"),
        api.get("/tasks"),
      ]);
      setDashboard(dashboardResponse.data);
      setProjects(projectsResponse.data);
      setTasks(tasksResponse.data);
      const workflowResponses = await Promise.all(
        projectsResponse.data.map(async (project) => {
          const response = await api.get(`/projects/${project.id}/workflow`);
          return [project.id, response.data];
        })
      );
      setWorkflows(Object.fromEntries(workflowResponses));
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to load your dashboard.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // The request synchronizes dashboard state with the API on mount.
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

          <section className="dashboard-insights-grid" aria-label="Workspace analytics">
            <article className="analytics-card trend-card">
              <div className="section-heading">
                <div>
                  <p className="page-eyebrow">Momentum</p>
                  <h3>Project improvement</h3>
                </div>
                <span className="analytics-badge">Live tracking</span>
              </div>
              <div className="trend-summary">
                <div>
                  <strong>{actualCompletion}%</strong>
                  <span>work completed</span>
                </div>
                <span className="trend-positive">Actual task completion</span>
              </div>
              <div className="mini-chart" role="img" aria-label="Task completion trend">
                {projectMetrics.length === 0 && <p className="empty-analytics">Create projects and tasks to see actual improvement here.</p>}
                {projectMetrics.length > 0 && (
                  <div className="project-chart">
                    <div className="project-chart-y-axis" aria-hidden="true">
                      <span>100%</span><span>75%</span><span>50%</span><span>25%</span><span>0%</span>
                    </div>
                    <div className="project-chart-plot">
                      <div className="project-chart-grid" aria-hidden="true">
                        <span /><span /><span /><span /><span />
                      </div>
                      <div className="project-chart-bars">
                        {projectMetrics.slice(0, 6).map((project) => (
                          <div className="project-chart-column" key={project.id} title={`${project.name}: ${project.progress}% complete`}>
                            <strong>{project.progress}%</strong>
                            <span className="project-chart-bar" style={{ height: `${Math.max(project.progress, 3)}%` }} />
                            <small>{project.name}</small>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </article>

            <article className="analytics-card completion-card">
              <div className="section-heading">
                <div>
                  <p className="page-eyebrow">Delivery</p>
                  <h3>Workspace health</h3>
                </div>
                <ProgressRing value={workflowCompletion || actualCompletion} />
              </div>
              <div className="health-list">
                <div><span className="health-dot aqua" /><span>Completed</span><strong>{dashboard.completedTasks || 0}</strong></div>
                <div><span className="health-dot violet" /><span>In motion</span><strong>{(dashboard.inProgressTasks || 0) + (dashboard.inReviewTasks || 0)}</strong></div>
                <div><span className="health-dot pink" /><span>Needs attention</span><strong>{dashboard.todoTasks || 0}</strong></div>
              </div>
            </article>
          </section>

          <section className="project-command-card" aria-label="Project command center">
            <div>
              <p className="page-eyebrow">Command center</p>
              <h3>Where work is now</h3>
              <p>{currentWorkflowStage ? `Current stage: ${currentWorkflowStage.name} · Owner: ${currentWorkflowStage.ownerRole}` : "Design a workflow to start tracking delivery stages."}</p>
            </div>
            <div className="command-next">
              <span>Next task</span>
              <strong>{nextTask?.title || "No outstanding tasks"}</strong>
              <small>{nextTask ? "Move this forward to improve project completion." : "Your task plan is clear."}</small>
            </div>
            <div className="command-metric">
              <strong>{workflowCompletion}%</strong>
              <span>workflow complete</span>
            </div>
          </section>

          <section className="dashboard-bottom-grid">
            <article className="analytics-card workflow-card">
              <div className="section-heading">
                <div>
                  <p className="page-eyebrow">How work moves</p>
                  <h3>Feature workflow</h3>
                </div>
                <span className="muted-label">{workflowStages.length} workflow stages</span>
              </div>
              <div className="workflow">
                {workflowStages.length === 0 && <p className="empty-analytics">Create a project workflow to see the delivery path.</p>}
                {workflowStages.slice(0, 6).map((stage, index) => (
                  <div className="workflow-step" key={stage.id}>
                    <span className={`workflow-number ${["violet", "aqua", "pink", "gold"][index % 4]}`}>{String(index + 1).padStart(2, "0")}</span>
                  <div><strong>{stage.name}</strong><small>{stage.ownerRole}</small><select value={stage.status} onChange={(event) => updateWorkflowStage(stage.projectId, stage.id, event.target.value)} aria-label={`${stage.name} status`}><option value="PLANNED">Planned</option><option value="IN_PROGRESS">In progress</option><option value="BLOCKED">Blocked</option><option value="COMPLETED">Completed</option></select></div>
                    {index < Math.min(workflowStages.length, 6) - 1 && <span className="workflow-arrow">→</span>}
                  </div>
                ))}
              </div>
            </article>

            <article className="analytics-card projects-card">
              <div className="section-heading">
                <div>
                  <p className="page-eyebrow">Focus areas</p>
                  <h3>Project tracking</h3>
                </div>
                <span className="muted-label">By completion</span>
              </div>
              {projects.length === 0 && <p className="empty-analytics">Create a project to see progress here.</p>}
              <div className="project-progress-list">
                {projectMetrics.slice(0, 4).map((project) => {
                  return (
                    <div className="project-progress" key={project.id}>
                      <div><strong>{project.name}</strong><span>{project.progress}%</span></div>
                      <div className="project-track"><span style={{ width: `${project.progress}%` }} /></div>
                    </div>
                  );
                })}
              </div>
            </article>
          </section>
        </>
      )}
    </AppLayout>
  );
}
