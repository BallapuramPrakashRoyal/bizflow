import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuth } from "../context/AuthContext";

function Dashboard() {
  const navigate = useNavigate();
  const { logout: signOut, user: authenticatedUser } = useAuth();

  const [dashboard, setDashboard] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [activeSection, setActiveSection] = useState("dashboard");

  const loadDashboard = useCallback(async () => {
    try {
      const dashboardResponse = await api.get("/dashboard");

      setDashboard(dashboardResponse.data);
      setUser(authenticatedUser);
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.error ||
        "Unable to load dashboard."
      );
    } finally {
      setLoading(false);
    }
  }, [authenticatedUser]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void loadDashboard();
  }, [loadDashboard]);

  const logout = () => {
    signOut();
    navigate("/login");
  };

  if (loading) {
    return (
      <div className="loading-screen">
        Loading BizFlow...
      </div>
    );
  }

  return (
    <div className="app-layout">

      <aside className="sidebar">

        <div className="brand">
          <h2>BizFlow</h2>
        </div>

        <nav>

          <button className={`nav-item ${activeSection === "dashboard" ? "active" : ""}`} onClick={() => setActiveSection("dashboard")}>
            Dashboard
          </button>

          <button className={`nav-item ${activeSection === "organizations" ? "active" : ""}`} onClick={() => setActiveSection("organizations")}>
            Organizations
          </button>

          <button className={`nav-item ${activeSection === "projects" ? "active" : ""}`} onClick={() => setActiveSection("projects")}>
            Projects
          </button>

          <button className={`nav-item ${activeSection === "tasks" ? "active" : ""}`} onClick={() => setActiveSection("tasks")}>
            Tasks
          </button>

          <button className={`nav-item ${activeSection === "my-tasks" ? "active" : ""}`} onClick={() => setActiveSection("my-tasks")}>
            My Tasks
          </button>

          <button className={`nav-item ${activeSection === "activity" ? "active" : ""}`} onClick={() => setActiveSection("activity")}>
            Audit Logs
          </button>

        </nav>

        <button
          className="logout-button"
          onClick={logout}
        >
          Logout
        </button>

      </aside>

      <main className="main-content">

        <header className="topbar">

          <div>
            <h1>{activeSection === "activity" ? "Activity" : activeSection[0].toUpperCase() + activeSection.slice(1)}</h1>
            <p>
              Welcome back{user?.name ? `, ${user.name}` : ""}.
            </p>
          </div>

          <div className="user-info">
            {user?.email}
          </div>

        </header>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {dashboard && activeSection === "dashboard" && (
          <>

            <section className="stats-grid">

              <StatCard
                title="Projects"
                value={dashboard.totalProjects}
              />

              <StatCard
                title="Total Tasks"
                value={dashboard.totalTasks}
              />

              <StatCard
                title="My Tasks"
                value={dashboard.myTasks}
              />

              <StatCard
                title="Completed"
                value={dashboard.completedTasks}
              />

            </section>

            <section className="dashboard-section">

              <h2>Task Overview</h2>

              <div className="task-stats">

                <TaskStat
                  title="To Do"
                  value={dashboard.todoTasks}
                />

                <TaskStat
                  title="In Progress"
                  value={dashboard.inProgressTasks}
                />

                <TaskStat
                  title="In Review"
                  value={dashboard.inReviewTasks}
                />

                <TaskStat
                  title="Completed"
                  value={dashboard.completedTasks}
                />

                <TaskStat
                  title="Cancelled"
                  value={dashboard.cancelledTasks}
                />

              </div>

            </section>

          </>
        )}

        {activeSection !== "dashboard" && (
          <Workspace activeSection={activeSection} currentUser={user} onDataChange={loadDashboard} />
        )}

      </main>

    </div>
  );
}

export function Workspace({ activeSection, currentUser, onDataChange }) {
  const [organizations, setOrganizations] = useState([]);
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [organizationName, setOrganizationName] = useState("");
  const [project, setProject] = useState({ name: "", description: "", organizationId: "" });
  const [task, setTask] = useState({ title: "", description: "", projectId: "", assigneeEmail: "" });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [activity, setActivity] = useState([]);
  const [selectedOrganizationId, setSelectedOrganizationId] = useState("");
  const [members, setMembers] = useState([]);
  const [memberEmail, setMemberEmail] = useState("");
  const [memberRole, setMemberRole] = useState("MEMBER");
  const [selectedTaskId, setSelectedTaskId] = useState("");
  const [comments, setComments] = useState([]);
  const [commentContent, setCommentContent] = useState("");

  const loadWorkspace = async () => {
    try {
      const [organizationResponse, projectResponse, taskResponse, activityResponse] = await Promise.all([
        api.get("/organizations"), api.get("/projects"), api.get("/tasks"), api.get("/audit-logs/me"),
      ]);
      setOrganizations(organizationResponse.data);
      setProjects(projectResponse.data);
      setTasks(taskResponse.data);
      setActivity(activityResponse.data);
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to load your workspace.");
    }
  };

  useEffect(() => {
    const initializeWorkspace = async () => {
      await loadWorkspace();
    };
    void initializeWorkspace();
  }, []);

  useEffect(() => {
    if (!selectedOrganizationId) {
      return;
    }
    api.get(`/organizations/${selectedOrganizationId}/members`)
      .then((response) => setMembers(response.data))
      .catch((requestError) => setError(requestError.response?.data?.error || "Unable to load members."));
  }, [selectedOrganizationId]);

  useEffect(() => {
    if (!selectedTaskId) return;
    api.get(`/comments/task/${selectedTaskId}`)
      .then((response) => setComments(response.data))
      .catch((requestError) => setError(requestError.response?.data?.error || "Unable to load comments."));
  }, [selectedTaskId]);

  const submit = async (event, endpoint, payload, success, reset) => {
    event.preventDefault();
    setError("");
    setMessage("");
    try {
      await api.post(endpoint, payload);
      reset();
      setMessage(success);
      await Promise.all([loadWorkspace(), onDataChange()]);
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to save your changes.");
    }
  };

  const runAction = async (action, success) => {
    setError("");
    setMessage("");
    try {
      await action();
      setMessage(success);
      await Promise.all([loadWorkspace(), onDataChange()]);
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to save your changes.");
    }
  };

  const addMember = async (event) => {
    event.preventDefault();
    await runAction(async () => {
      const userResponse = await api.get("/users/lookup", { params: { email: memberEmail } });
      await api.post(`/organizations/${selectedOrganizationId}/members`, {
        userId: userResponse.data.id, role: memberRole,
      });
      setMemberEmail("");
    }, "Member added to the organization.");
  };

  const addComment = async (event) => {
    event.preventDefault();
    await runAction(async () => {
      await api.post("/comments", {
        taskId: Number(selectedTaskId),
        content: commentContent,
      });
      const response = await api.get(`/comments/task/${selectedTaskId}`);
      setComments(response.data);
      setCommentContent("");
    }, "Comment added.");
  };

  const createTask = async (event) => {
    event.preventDefault();
    await runAction(async () => {
      let assignedUserId = null;
      if (task.assigneeEmail.trim()) {
        const userResponse = await api.get("/users/lookup", {
          params: { email: task.assigneeEmail.trim() },
        });
        assignedUserId = userResponse.data.id;
      }
      await api.post("/tasks", {
        title: task.title,
        description: task.description,
        projectId: Number(task.projectId),
        assignedUserId,
      });
      setTask({ title: "", description: "", projectId: "", assigneeEmail: "" });
    }, "Task created.");
  };

  return (
    <section className="dashboard-section workspace-section">
      <h2>{activeSection === "activity" ? "Recent activity" : `${activeSection[0].toUpperCase() + activeSection.slice(1)} workspace`}</h2>
      <p className="workspace-help">{activeSection === "organizations" ? "Create organizations and add the people who collaborate with you." : "Manage your work without leaving BizFlow."}</p>
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}
      {activeSection === "organizations" && <div className="workspace-grid">
        <form className="workspace-card" onSubmit={(event) => submit(event, "/organizations", { name: organizationName }, "Organization created.", () => setOrganizationName(""))}>
          <h3>Organization</h3>
          <input value={organizationName} onChange={(event) => setOrganizationName(event.target.value)} placeholder="Organization name" required />
          <button className="primary-button" type="submit">Create organization</button>
          <ul>{organizations.map((organization) => <li key={organization.id}><button className="text-button" type="button" onClick={() => setSelectedOrganizationId(String(organization.id))}>{organization.name}</button></li>)}</ul>
        </form>

        <form className="workspace-card" onSubmit={addMember}>
          <h3>Members {selectedOrganizationId && `(${members.length})`}</h3>
          <select value={selectedOrganizationId} onChange={(event) => setSelectedOrganizationId(event.target.value)} required><option value="">Choose organization</option>{organizations.map((organization) => <option key={organization.id} value={organization.id}>{organization.name}</option>)}</select>
          <input value={memberEmail} onChange={(event) => setMemberEmail(event.target.value)} placeholder="Member email" type="email" required />
          <select value={memberRole} onChange={(event) => setMemberRole(event.target.value)}><option value="MEMBER">Member</option><option value="MANAGER">Manager</option><option value="ADMIN">Admin</option></select>
          <button className="primary-button" type="submit" disabled={!selectedOrganizationId}>Add member</button>
          <ul>{members.map((member) => <li key={member.id}>User #{member.userId} <select value={member.role} onChange={(event) => runAction(() => api.put(`/organizations/${selectedOrganizationId}/members/${member.userId}/role`, { role: event.target.value }), "Member role updated.")}><option value="MEMBER">Member</option><option value="MANAGER">Manager</option><option value="ADMIN">Admin</option></select></li>)}</ul>
        </form>
      </div>}

      {activeSection === "projects" && <div className="workspace-grid">
        <form className="workspace-card" onSubmit={(event) => submit(event, "/projects", { ...project, organizationId: Number(project.organizationId) }, "Project created.", () => setProject({ name: "", description: "", organizationId: "" }))}>
          <h3>New project</h3><input value={project.name} onChange={(event) => setProject({ ...project, name: event.target.value })} placeholder="Project name" required /><textarea value={project.description} onChange={(event) => setProject({ ...project, description: event.target.value })} placeholder="Description (optional)" /><select value={project.organizationId} onChange={(event) => setProject({ ...project, organizationId: event.target.value })} required><option value="">Choose organization</option>{organizations.map((organization) => <option key={organization.id} value={organization.id}>{organization.name}</option>)}</select><button className="primary-button" type="submit" disabled={!organizations.length}>Create project</button>
        </form>
        <div className="workspace-card workspace-list"><h3>All projects</h3><ul>{projects.map((item) => <li key={item.id}><strong>{item.name}</strong><span>{item.description || "No description"}</span><div><button className="text-button" onClick={() => { const name = window.prompt("Project name", item.name); if (name) runAction(() => api.put(`/projects/${item.id}`, { name, description: item.description || "" }), "Project updated."); }}>Edit</button><button className="text-button danger" onClick={() => window.confirm(`Delete ${item.name}?`) && runAction(() => api.delete(`/projects/${item.id}`), "Project deleted.")}>Delete</button></div></li>)}</ul></div>
      </div>}

      {(activeSection === "tasks" || activeSection === "my-tasks") && <div className="workspace-grid">
        {activeSection === "tasks" && <form className="workspace-card" onSubmit={createTask}>
          <h3>New task</h3><input value={task.title} onChange={(event) => setTask({ ...task, title: event.target.value })} placeholder="Task title" required /><textarea value={task.description} onChange={(event) => setTask({ ...task, description: event.target.value })} placeholder="Description (optional)" /><select value={task.projectId} onChange={(event) => setTask({ ...task, projectId: event.target.value })} required><option value="">Choose project</option>{projects.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select><input value={task.assigneeEmail} onChange={(event) => setTask({ ...task, assigneeEmail: event.target.value })} placeholder="Assignee email (optional)" type="email" /><button className="primary-button" type="submit" disabled={!projects.length}>Create task</button>
        </form>}
        <div className="workspace-card workspace-list"><h3>{activeSection === "my-tasks" ? "Tasks assigned to me" : "All tasks"}</h3><ul>{tasks.filter((item) => activeSection !== "my-tasks" || item.assignedUserId === currentUser?.id).map((item) => <li key={item.id}><strong>{item.title}</strong><select value={item.status} onChange={(event) => runAction(() => api.put(`/tasks/${item.id}`, { title: item.title, description: item.description || "", status: event.target.value, assignedUserId: item.assignedUserId }), "Task status updated.")}><option value="TODO">To do</option><option value="IN_PROGRESS">In progress</option><option value="IN_REVIEW">In review</option><option value="DONE">Done</option><option value="CANCELLED">Cancelled</option></select><div><button className="text-button" onClick={() => { setSelectedTaskId(String(item.id)); setComments([]); }}>Discuss</button><button className="text-button" onClick={() => { const title = window.prompt("Task title", item.title); if (title) runAction(() => api.put(`/tasks/${item.id}`, { title, description: item.description || "", status: item.status, assignedUserId: item.assignedUserId }), "Task updated."); }}>Edit</button><button className="text-button danger" onClick={() => window.confirm(`Delete ${item.title}?`) && runAction(() => api.delete(`/tasks/${item.id}`), "Task deleted.")}>Delete</button></div></li>)}</ul></div>
        <form className="workspace-card comments-card" onSubmit={addComment}><h3>Task discussion</h3><select value={selectedTaskId} onChange={(event) => { setSelectedTaskId(event.target.value); setComments([]); }} required><option value="">Choose task</option>{tasks.map((item) => <option key={item.id} value={item.id}>{item.title}</option>)}</select><textarea value={commentContent} onChange={(event) => setCommentContent(event.target.value)} placeholder="Write a comment" required /><button className="primary-button" type="submit" disabled={!selectedTaskId}>Post comment</button><ul>{comments.map((comment) => <li key={comment.id}><strong>User #{comment.userId}</strong><span>{comment.content}</span></li>)}</ul>{selectedTaskId && !comments.length && <p>No comments yet.</p>}</form>
      </div>}

      {activeSection === "activity" && <div className="workspace-card workspace-list"><ul>{activity.map((entry) => <li key={entry.id}><strong>{entry.action.replaceAll("_", " ")}</strong><span>{entry.entityType} · {new Date(entry.createdAt).toLocaleString()}</span></li>)}</ul>{!activity.length && <p>No activity yet.</p>}</div>}
    </section>
  );
}

function StatCard({ title, value }) {
  return (
    <div className="stat-card">

      <div className="stat-title">
        {title}
      </div>

      <div className="stat-value">
        {value}
      </div>

    </div>
  );
}

function TaskStat({ title, value }) {
  return (
    <div className="task-stat">

      <span>{title}</span>

      <strong>{value}</strong>

    </div>
  );
}

export default Dashboard;
