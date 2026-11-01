import { useMemo, useRef, useState } from "react";
import api from "../api/api";

const TYPES = {
  trigger: { label: "Trigger", hint: "Starts the flow", color: "#3ef0c0", title: "New project", detail: "Planning starts" },
  action: { label: "Action", hint: "Does the work", color: "#8f7bff", title: "Build feature", detail: "Engineering" },
  condition: { label: "Condition", hint: "Splits yes or no", color: "#ffc247", title: "Ready for review?", detail: "Quality gate" },
  approval: { label: "Approval", hint: "Waits for a person", color: "#ff6b8a", title: "Stakeholder approval", detail: "Product" },
  delay: { label: "Delay", hint: "Pauses the flow", color: "#4db8ff", title: "Release window", detail: "Deployment" },
  notify: { label: "Notify", hint: "Sends a message", color: "#d58bff", title: "Notify team", detail: "Communication" },
};

const SAMPLE_NODES = [
  { id: "n1", type: "trigger", x: 30, y: 180, title: "Project kickoff", detail: "Planning starts" },
  { id: "n2", type: "action", x: 280, y: 180, title: "Build feature", detail: "Engineering" },
  { id: "n3", type: "condition", x: 530, y: 180, title: "Ready for review?", detail: "Quality gate" },
  { id: "n4", type: "approval", x: 780, y: 70, title: "Stakeholder approval", detail: "Product" },
  { id: "n5", type: "notify", x: 1030, y: 180, title: "Deploy release", detail: "DevOps" },
];

const SAMPLE_EDGES = [
  { from: "n1", to: "n2" },
  { from: "n2", to: "n3" },
  { from: "n3", to: "n4", branch: "Yes" },
  { from: "n4", to: "n5" },
];

function WorkflowDesigner({ organizations, onSaved }) {
  const [projectName, setProjectName] = useState("");
  const [organizationId, setOrganizationId] = useState("");
  const [nodes, setNodes] = useState([]);
  const [edges, setEdges] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [connectingId, setConnectingId] = useState("");
  const [log, setLog] = useState([]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [running, setRunning] = useState(false);
  const nodeCounter = useRef(0);

  const selectedNode = nodes.find((node) => node.id === selectedId);
  const orderedNodes = useMemo(() => [...nodes].sort((a, b) => a.x - b.x), [nodes]);

  const addNode = (type) => {
    const meta = TYPES[type];
    const node = {
      id: `n${nodeCounter.current += 1}`,
      type,
      x: 30 + (nodes.length % 3) * 250,
      y: 80 + Math.floor(nodes.length / 3) * 120,
      title: meta.title,
      detail: meta.detail,
    };
    setNodes((current) => [...current, node]);
    setSelectedId(node.id);
  };

  const loadSample = () => {
    setNodes(SAMPLE_NODES.map((node) => ({ ...node })));
    setEdges(SAMPLE_EDGES.map((edge) => ({ ...edge })));
    setSelectedId("");
    setMessage("");
  };

  const clearDesigner = () => {
    setNodes([]);
    setEdges([]);
    setSelectedId("");
    setLog([]);
    setMessage("");
  };

  const connectNode = (nodeId) => {
    if (!connectingId) {
      setConnectingId(nodeId);
      return;
    }
    if (connectingId !== nodeId && !edges.some((edge) => edge.from === connectingId && edge.to === nodeId)) {
      setEdges((current) => [...current, { from: connectingId, to: nodeId }]);
    }
    setConnectingId("");
  };

  const updateNode = (key, value) => {
    setNodes((current) => current.map((node) => node.id === selectedId ? { ...node, [key]: value } : node));
  };

  const runTest = async () => {
    if (running) return;
    setRunning(true);
    setLog([]);
    for (const node of orderedNodes) {
      setLog((current) => [...current, `${TYPES[node.type].label}: ${node.title}`]);
      await new Promise((resolve) => setTimeout(resolve, 350));
    }
    setLog((current) => [...current, "Flow test finished successfully."]);
    setRunning(false);
  };

  const saveWorkflow = async () => {
    setError("");
    setMessage("");
    if (!projectName.trim() || !organizationId || nodes.length === 0) {
      setError("Enter a project name, choose an organization, and add at least one workflow step.");
      return;
    }
    try {
      const projectResponse = await api.post("/projects", {
        name: projectName.trim(),
        description: "Designed with BizFlow Workflow Designer",
        organizationId: Number(organizationId),
      });
      const stages = orderedNodes.map((node) => ({
        name: node.title,
        description: `${TYPES[node.type].label} · ${node.detail}`,
        ownerRole: node.detail,
        techStack: "",
      }));
      await api.post(`/projects/${projectResponse.data.id}/workflow`, stages);
      setMessage("Workflow and project saved.");
      onSaved();
    } catch (requestError) {
      setError(requestError.response?.data?.error || "Unable to save this workflow.");
    }
  };

  const exportWorkflow = () => {
    const data = JSON.stringify({ name: projectName, nodes, edges }, null, 2);
    const blob = new Blob([data], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `${projectName || "bizflow-workflow"}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <section className="workflow-designer">
      <header className="workflow-designer-top">
        <div className="workflow-designer-brand"><img src="/bizflow-logo.png" alt="BizFlow" /> <strong>Workflow designer</strong></div>
        <input value={projectName} onChange={(event) => setProjectName(event.target.value)} placeholder="Workflow / project name" aria-label="Workflow name" />
        <select value={organizationId} onChange={(event) => setOrganizationId(event.target.value)} aria-label="Workflow organization">
          <option value="">Choose organization</option>
          {organizations.map((organization) => <option key={organization.id} value={organization.id}>{organization.name}</option>)}
        </select>
        <div className="workflow-designer-actions">
          <button type="button" onClick={loadSample}>Sample</button>
          <button type="button" onClick={clearDesigner}>Clear</button>
          <button type="button" onClick={exportWorkflow}>Export</button>
          <button type="button" className="workflow-run" onClick={runTest} disabled={running}>{running ? "Running..." : "Run test"}</button>
          <button type="button" className="workflow-save" onClick={saveWorkflow}>Save project</button>
        </div>
      </header>
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}
      <div className="workflow-designer-body">
        <aside className="workflow-palette">
          <h3>Steps</h3>
          {Object.entries(TYPES).map(([type, meta]) => (
            <button type="button" key={type} onClick={() => addNode(type)}>
              <span style={{ "--step-color": meta.color }}>{type === "condition" ? "◇" : type === "trigger" ? "⚡" : type === "approval" ? "✓" : type === "delay" ? "◷" : type === "notify" ? "✉" : "▶"}</span>
              <strong>{meta.label}<small>{meta.hint}</small></strong>
            </button>
          ))}
          <p>Click a step to add it. Select a node, then select another node to connect them.</p>
        </aside>
        <div className="workflow-canvas" role="application" aria-label="Workflow canvas">
          <div className="workflow-grid" />
          <svg className="workflow-edges" aria-hidden="true">
            {edges.map((edge) => {
              const from = nodes.find((node) => node.id === edge.from);
              const to = nodes.find((node) => node.id === edge.to);
              if (!from || !to) return null;
              return <path key={`${edge.from}-${edge.to}`} d={`M ${from.x + 190} ${from.y + 38} C ${from.x + 220} ${from.y + 38}, ${to.x - 30} ${to.y + 38}, ${to.x} ${to.y + 38}`} />;
            })}
          </svg>
          {nodes.length === 0 && <div className="workflow-empty"><strong>Start with a trigger</strong><span>Add a step from the left to design your flow.</span></div>}
          {nodes.map((node) => (
            <button
              type="button"
              className={`workflow-node ${selectedId === node.id ? "selected" : ""} ${connectingId === node.id ? "connecting" : ""}`}
              style={{ left: node.x, top: node.y, "--node-color": TYPES[node.type].color }}
              key={node.id}
              onClick={() => { setSelectedId(node.id); connectNode(node.id); }}
            >
              <span className="workflow-node-icon">{node.type === "condition" ? "◇" : node.type === "trigger" ? "⚡" : "▶"}</span>
              <span><strong>{node.title}</strong><small>{node.detail}</small></span>
              <i />
            </button>
          ))}
          <div className="workflow-canvas-status">{nodes.length} steps · {edges.length} connections</div>
          {log.length > 0 && <div className="workflow-run-log"><strong>Test run</strong>{log.map((entry, index) => <span key={`${entry}-${index}`}>● {entry}</span>)}</div>}
        </div>
        <aside className="workflow-inspector">
          <h3>Properties</h3>
          {!selectedNode && <p>Select a step to edit its name, owner layer, and connection point.</p>}
          {selectedNode && <><label>Step name<input value={selectedNode.title} onChange={(event) => updateNode("title", event.target.value)} /></label><label>Owner / layer<input value={selectedNode.detail} onChange={(event) => updateNode("detail", event.target.value)} /></label><button type="button" className="workflow-delete" onClick={() => { setNodes((current) => current.filter((node) => node.id !== selectedId)); setEdges((current) => current.filter((edge) => edge.from !== selectedId && edge.to !== selectedId)); setSelectedId(""); }}>Delete step</button></>}
        </aside>
      </div>
    </section>
  );
}

export default WorkflowDesigner;
