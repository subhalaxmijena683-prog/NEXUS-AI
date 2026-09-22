
import {
  LayoutDashboard,
  MessageSquare,
  Bot,
  BookOpen,
  Workflow,
  BarChart3,
  Settings as SettingsIcon,
  Search,
  Database,
  BrainCircuit,
  Play,
  Plus,
  LogOut,
  FileText,
  Upload,
  ShieldCheck,
  Bell,
  RefreshCw,
  Palette,
  Sparkles,
  CheckCircle2,
  X,
} from "lucide-react";

import Login from "./Login";
import Register from "./Register";
import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import "./App.css";
//import "./index.css";

const API_BASE = "https://nexus-ai-n3oc.onrender.com";

function Knowledge() {
  const [file, setFile] = useState(null);
  const [documents, setDocuments] = useState([]);
  const [question, setQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");

  const token =
    localStorage.getItem("token") ||
    localStorage.getItem("jwt") ||
    localStorage.getItem("accessToken");

  async function loadDocuments() {
    try {
      const response = await fetch(`${API_BASE}/api/documents`, {
        headers: token
          ? {
              Authorization: `Bearer ${token}`,
            }
          : {},
      });

      if (!response.ok) {
        throw new Error("Could not load documents");
      }

      const data = await response.json();

      setDocuments(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Load documents error:", err);
    }
  }

  useEffect(() => {
    loadDocuments();
  }, []);

  async function uploadDocument() {
    if (!file) {
      setError("Please select a PDF file.");
      return;
    }

    if (!file.name.toLowerCase().endsWith(".pdf")) {
      setError("Only PDF files are supported.");
      return;
    }

    setUploading(true);
    setError("");

    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await fetch(`${API_BASE}/api/documents/upload`, {
        method: "POST",
        headers: token
          ? {
              Authorization: `Bearer ${token}`,
            }
          : {},
        body: formData,
      });

      let data = {};

      try {
        data = await response.json();
      } catch {
        data = {};
      }

      if (!response.ok) {
        throw new Error(data?.message || "Document upload failed");
      }

      setFile(null);

      await loadDocuments();

      alert("PDF uploaded and indexed successfully.");
    } catch (err) {
      console.error("Upload error:", err);

      setError(err.message || "Unable to upload document.");
    } finally {
      setUploading(false);
    }
  }

  async function askKnowledge() {
    if (!question.trim()) {
      setError("Please enter a question.");
      return;
    }

    setLoading(true);
    setAnswer("");
    setError("");

    try {
      const response = await fetch(`${API_BASE}/api/agents/rag`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token
            ? {
                Authorization: `Bearer ${token}`,
              }
            : {}),
        },
        body: JSON.stringify({
          question: question.trim(),
        }),
      });

      let data = {};

      try {
        data = await response.json();
      } catch {
        data = {};
      }

      if (!response.ok) {
        throw new Error(data?.message || "RAG request failed");
      }

      setAnswer(
        data?.result ||
          data?.answer ||
          data?.response ||
          "No answer generated."
      );
    } catch (err) {
      console.error("RAG error:", err);

      setError(
        err.message || "Unable to connect to RAG Knowledge Agent."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-panel">
      <span className="section-label">ENTERPRISE KNOWLEDGE</span>

      <h2>Knowledge Base</h2>

      <p>
        Upload enterprise PDF documents and ask NEXUS AI questions
        using Retrieval-Augmented Generation.
      </p>

      {/* Upload */}
      <div className="feature-card">
        <h3>Upload Document</h3>

        <p>
          Upload a PDF to extract, chunk and index its knowledge
          into the enterprise knowledge base.
        </p>

        <input
          type="file"
          accept=".pdf,application/pdf"
          onChange={(e) => {
            setFile(e.target.files?.[0] || null);
            setError("");
          }}
        />

        {file && (
          <p>
            Selected: <strong>{file.name}</strong>
          </p>
        )}

        <button
          className="primary-btn"
          onClick={uploadDocument}
          disabled={uploading}
        >
          {uploading ? "Processing PDF..." : "Upload & Index"}
        </button>
      </div>

      {/* Documents */}
      <div className="feature-card">
        <h3>Indexed Documents</h3>

        {documents.length === 0 ? (
          <p>No documents indexed yet.</p>
        ) : (
          documents.map((doc, index) => (
            <div
              key={doc.id || doc.documentId || index}
              className="agent"
            >
              <div className="agent-icon">PDF</div>

              <div>
                <strong>
                  {doc.fileName ||
                    doc.filename ||
                    doc.name ||
                    "Document"}
                </strong>

                <span>
                  {doc.fileType || "PDF"}
                </span>
              </div>
            </div>
          ))
        )}
      </div>

      {/* RAG Search */}
      <div className="feature-card">
        <h3>Ask Knowledge</h3>

        <p>
          Ask questions about your uploaded enterprise documents.
          NEXUS AI will retrieve relevant knowledge and generate
          an answer.
        </p>

        <textarea
          className="ai-input"
          rows="4"
          placeholder="Ask something about your documents..."
          value={question}
          onChange={(e) => {
            setQuestion(e.target.value);
            setError("");
          }}
        />

        <button
          className="primary-btn"
          onClick={askKnowledge}
          disabled={loading}
        >
          {loading ? "Searching Knowledge..." : "Ask NEXUS AI"}
        </button>
      </div>

      {/* Answer */}
      {answer && (
        <div className="page-panel">
          <span className="section-label">
            RAG KNOWLEDGE RESPONSE
          </span>

          <div className="ai-response">
            <ReactMarkdown>{answer}</ReactMarkdown>
          </div>
        </div>
      )}

      {/* Error */}
      {error && (
        <div className="page-panel">
          <p>{error}</p>
        </div>
      )}
    </section>
  );
}

function Dashboard({ setActivePage }) {
  return (
    <>
      <div className="hero">
        <div>
          <div className="hero-badge">
            AUTONOMOUS ENTERPRISE INTELLIGENCE
          </div>

          <h2>
            Turn complex business problems into{" "}
            <span>intelligent decisions.</span>
          </h2>

          <p>
            NEXUS AI coordinates specialized AI agents to research,
            analyze, reason, and execute enterprise workflows.
          </p>

          <button
            className="primary-btn"
            onClick={() => setActivePage("AI Chat")}
          >
            Start AI Analysis
          </button>
        </div>

        <div className="agent-orbit">
          <div className="orbit orbit-one"></div>
          <div className="orbit orbit-two"></div>

          <div className="core">N</div>
        </div>
      </div>

      <div className="stats">
        <div className="stat-card">
          <span>AI Agents</span>
          <strong>7</strong>
          <small>Specialized agents</small>
        </div>

        <div className="stat-card">
          <span>Workflows</span>
          <strong>12</strong>
          <small>Automated workflows</small>
        </div>

        <div className="stat-card">
          <span>Knowledge</span>
          <strong>RAG</strong>
          <small>Enterprise knowledge</small>
        </div>

        <div className="stat-card">
          <span>Intelligence</span>
          <strong>AI</strong>
          <small>Decision support</small>
        </div>
      </div>

      <div className="content-grid">
        <div className="panel">
          <div className="panel-header">
            <div>
              <span className="section-label">ACTIVE SYSTEMS</span>
              <h3>AI Agents</h3>
            </div>

            <button
              className="text-btn"
              onClick={() => setActivePage("Agents")}
            >
              View all
            </button>
          </div>

          <div className="agent">
            <div className="agent-icon">
              <Search size={18} />
            </div>

            <div>
              <strong>Research Agent</strong>
              <span>Web research and intelligence</span>
            </div>

            {/* <span className="online">ONLINE</span> */}
          </div>

          <div className="agent">
            <div className="agent-icon">
             <BarChart3 size={18} />
            </div>

            <div>
              <strong>Data Analyst</strong>
              <span>Data analysis and insights</span>
            </div>

            {/* <span className="online">ONLINE</span> */}
          </div>

          <div className="agent">
            <div className="agent-icon">
             <BookOpen size={18} />
            </div>

            <div>
              <strong>Knowledge Agent</strong>
              <span>Enterprise RAG intelligence</span>
            </div>

            {/* <span className="online">ONLINE</span> */}
          </div>

          <div className="agent">
            <div className="agent-icon">
             <ShieldCheck size={18} />
             </div>

            <div>
              <strong>Judge Agent</strong>
              <span>Decision validation</span>
            </div>

            {/* <span className="online">ONLINE</span> */}
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <div>
              <span className="section-label">QUICK ACCESS</span>
              <h3>Actions</h3>
            </div>
          </div>

          <div className="quick-actions">
            <button onClick={() => setActivePage("AI Chat")}>
                <span><MessageSquare size={17} /></span>
                  Ask NEXUS AI
                 </button>

            <button onClick={() => setActivePage("Knowledge")}>
             <span><Search size={17} /></span>
               Search Knowledge
            </button>

            <button onClick={() => setActivePage("Agents")}>
              <span><BarChart3 size={17} /></span>
              Run AI Agent
            </button>

            <button onClick={() => setActivePage("Workflows")}>
             <span><Workflow size={17} /></span>
              Create Workflow
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
function Settings() {
  const [notifications, setNotifications] = useState(true);
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [theme, setTheme] = useState("dark");

  const userName =
    localStorage.getItem("userName") || "Admin User";

  const userEmail =
    localStorage.getItem("userEmail") || "Enterprise User";

  const initial =
    userName.charAt(0).toUpperCase();

  return (
    <section className="settings-page">

      {/* HEADER */}
      <div className="settings-header">

        <div>
          <span className="section-label">
            NEXUS AI CONTROL CENTER
          </span>

          <h2>Platform Settings</h2>

          <p>
            Configure your enterprise intelligence environment,
            AI behavior, account preferences and system controls.
          </p>
        </div>

        {/* <div className="settings-status-badge">
          <span className="settings-status-dot"></span>
          Backend Online
        </div> */}

      </div>


      {/* PROFILE + AI ENGINE */}
      <div className="settings-grid">

        {/* ACCOUNT */}
        <div className="settings-card">

          <div className="settings-card-header">
            <div className="settings-card-icon">
              <SettingsIcon size={20} />
            </div>

            <div>
              <span className="settings-label">
                PROFILE
              </span>

              <h3>Account</h3>
            </div>
          </div>

          <div className="profile-block">

            <div className="profile-avatar">
              {initial}
            </div>

            <div className="profile-info">
              <strong>{userName}</strong>
              <span>{userEmail}</span>

              <div className="role-badge">
                <span></span>
                Enterprise User
              </div>
            </div>

          </div>

          <div className="settings-divider"></div>

          <div className="settings-info-row">
            <span>Account Status</span>
            <strong className="success-text">
              Active
            </strong>
          </div>

          <div className="settings-info-row">
            <span>Authentication</span>
            <strong>JWT Secured</strong>
          </div>

        </div>


        {/* AI ENGINE */}
        <div className="settings-card">

          <div className="settings-card-header">
            <div className="settings-card-icon ai-icon">
              <Sparkles size={20} />
            </div>

            <div>
              <span className="settings-label">
                INTELLIGENCE ENGINE
              </span>

              <h3>AI Configuration</h3>
            </div>
          </div>

          <p className="settings-card-description">
            Configure the intelligence layer powering
            NEXUS AI's autonomous agents.
          </p>

          <div className="engine-status">
            <div className="engine-status-left">
              <span className="engine-dot"></span>

              <div>
                <strong>AI Engine Operational</strong>
                <span>Multi-Agent orchestration active</span>
              </div>
            </div>

            <span className="active-badge">
              ACTIVE
            </span>
          </div>

          <div className="settings-info-row">
            <span>Architecture</span>
            <strong>Multi-Agent</strong>
          </div>

          <div className="settings-info-row">
            <span>Knowledge Layer</span>
            <strong>RAG Enabled</strong>
          </div>

          <div className="settings-info-row">
            <span>AI Provider</span>
            <strong>Configured</strong>
          </div>

        </div>

      </div>


      {/* PREFERENCES */}
      <div className="settings-section-title">
        <span className="section-label">
          PLATFORM
        </span>

        <h3>Preferences</h3>

        <p>
          Control how NEXUS AI behaves during your session.
        </p>
      </div>


      <div className="settings-preferences">

        {/* Notifications */}
        <div className="preference-card">

          <div className="preference-icon">
           <Bell size={19} />
          </div>

          <div className="preference-content">
            <strong>Notifications</strong>

            <span>
              Receive important system and agent updates.
            </span>
          </div>

          <button
            className={`settings-toggle ${
              notifications ? "active" : ""
            }`}
            onClick={() =>
              setNotifications(!notifications)
            }
          >
            <span></span>
          </button>

        </div>


        {/* Auto Refresh */}
        <div className="preference-card">

          <div className="preference-icon">
          <RefreshCw size={19} />
          </div>

          <div className="preference-content">
            <strong>Auto Refresh</strong>

            <span>
              Automatically refresh analytics and system metrics.
            </span>
          </div>

          <button
            className={`settings-toggle ${
              autoRefresh ? "active" : ""
            }`}
            onClick={() =>
              setAutoRefresh(!autoRefresh)
            }
          >
            <span></span>
          </button>

        </div>


        {/* Theme */}
        <div className="preference-card">

          <div className="preference-icon">
           <Palette size={19} />
          </div>

          <div className="preference-content">
            <strong>Interface Theme</strong>

            <span>
              Choose the appearance of your NEXUS workspace.
            </span>
          </div>

          <div className="theme-selector">

            <button
              className={
                theme === "dark" ? "selected" : ""
              }
              onClick={() => setTheme("dark")}
            >
              Dark
            </button>

            <button
              className={
                theme === "system" ? "selected" : ""
              }
              onClick={() => setTheme("system")}
            >
              System
            </button>

          </div>

        </div>

      </div>


      {/* SYSTEM ARCHITECTURE */}
      {/* <div className="settings-section-title">

        <span className="section-label">
          INFRASTRUCTURE
        </span>

        <h3>System Architecture</h3>

        <p>
          Current NEXUS AI enterprise platform configuration.
        </p>

      </div> */}


      <div className="architecture-grid">

        {/* <div className="architecture-card">
          <div className="architecture-icon">
            ?
          </div>

          <div>
            <span>PLATFORM</span>
            <strong>NEXUS AI</strong>
            <small>Enterprise Intelligence</small>
          </div>

          <div className="architecture-check">
            ?
          </div>
        </div> */}


        {/* <div className="architecture-card">
          <div className="architecture-icon">
            ?
          </div>

          <div>
            <span>DATABASE</span>
            <strong>PostgreSQL</strong>
            <small>Enterprise data layer</small>
          </div>

          <div className="architecture-check">
            ?
          </div>
        </div>


        <div className="architecture-card">
          <div className="architecture-icon">
            ?
          </div>

          <div>
            <span>KNOWLEDGE</span>
            <strong>RAG Engine</strong>
            <small>Context-aware intelligence</small>
          </div>

          <div className="architecture-check">
            ?
          </div>
        </div>


        <div className="architecture-card">
          <div className="architecture-icon">
            ?
          </div>

          <div>
            <span>SECURITY</span>
            <strong>JWT</strong>
            <small>Authenticated access</small>
          </div>

          <div className="architecture-check">
            ?
          </div>
        </div> */}

      </div>


      {/* FINAL STATUS */}
      {/* <div className="configuration-banner">

        <div className="configuration-glow"></div>

        <div className="configuration-icon">
          ?
        </div>

        <div>
          <span className="section-label">
            ENTERPRISE INTELLIGENCE
          </span>

          <h3>NEXUS AI is ready</h3>

          <p>
            Your enterprise intelligence platform is
            configured and ready to research, analyze,
            reason and execute intelligent workflows.
          </p>
        </div>

        <div className="configuration-state">
          <span></span>
          Configuration Ready
        </div>

      </div> */}

    </section>
  );
}

function GenericPage({ title, description, icon: Icon }) {
  return (
    <section className="page-panel">
      <span className="section-label">
        {Icon && <Icon size={16} />}
        NEXUS AI
      </span>

      <h2>{title}</h2>
      <p>{description}</p>

      <div className="feature-grid">
        <div className="feature-card">
          <strong>Intelligent</strong>
          <span>
            Powered by the NEXUS AI agent architecture.
          </span>
        </div>

        <div className="feature-card">
          <strong>Enterprise Ready</strong>
          <span>
            Designed for autonomous enterprise workflows.
          </span>
        </div>
      </div>
    </section>
  );
}
function Agents() {
  const [selectedAgent, setSelectedAgent] = useState("research");
  const [task, setTask] = useState("");
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const agents = {
    research: {
      name: "Research Agent",
      icon: "R",
      description: "Web research and enterprise intelligence",
      endpoint: "/api/agents/research",
      placeholder:
        "Example: Research how AI automation can reduce enterprise customer support costs.",
    },

    analyst: {
      name: "Data Analyst Agent",
      icon: "D",
      description: "Business data analysis and insights",
      endpoint: "/api/agents/data-analyst",
      placeholder:
        "Example: Analyze customer support cost reduction strategies and identify key metrics.",
    },

    rag: {
      name: "RAG Knowledge Agent",
      icon: "K",
      description: "Enterprise document intelligence",
      endpoint: "/api/agents/rag",
      placeholder:
        "Example: What information in my uploaded enterprise documents is relevant to this problem?",
    },

    prediction: {
      name: "ML Prediction Agent",
      icon: "M",
      description: "Scenario prediction and business forecasting",
      endpoint: "/api/agents/ml-prediction",
      placeholder:
        "Example: Predict the likely business impact of implementing AI-powered customer support.",
    },

    decision: {
      name: "Decision Agent",
      icon: "D",
      description: "Enterprise decision-making and recommendations",
      endpoint: "/api/agents/decision",
      placeholder:
        "Use the full Autonomous Workflow for evidence-based decision generation.",
    },

    solution: {
      name: "Solution Agent",
      icon: "S",
      description: "Enterprise solution architecture and implementation",
      endpoint: "/api/agents/solution",
      placeholder:
        "Example: Design a practical AI-powered customer support solution.",
    },

    judge: {
      name: "Judge Agent",
      icon: "J",
      description: "Validates decisions, solutions and predictions",
      endpoint: "/api/agents/judge",
      placeholder:
        "Use the full Autonomous Workflow to evaluate the complete enterprise solution.",
    },
  };

  async function runAgent() {
    if (!task.trim()) {
      setError("Please enter a task for the agent.");
      return;
    }

    // Decision and Judge require multiple agent outputs.
    // Use the Autonomous Workflow for those agents.
    if (selectedAgent === "decision" || selectedAgent === "judge") {
      setError(
        `${agents[selectedAgent].name} works with multiple agent outputs. Use the Autonomous Workflow page for complete execution.`
      );
      return;
    }

    setLoading(true);
    setResult("");
    setError("");

    try {
      const token =
        localStorage.getItem("token") ||
        localStorage.getItem("jwt") ||
        localStorage.getItem("accessToken");

      const body =
        selectedAgent === "rag"
          ? {
              question: task.trim(),
            }
          : {
              task: task.trim(),
            };

      const response = await fetch(
        `${API_BASE}${agents[selectedAgent].endpoint}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ...(token
              ? {
                  Authorization: `Bearer ${token}`,
                }
              : {}),
          },
          body: JSON.stringify(body),
        }
      );

      let data = {};

      try {
        data = await response.json();
      } catch {
        data = {};
      }

      if (!response.ok) {
        throw new Error(
          data?.message ||
            data?.error ||
            `Agent request failed (${response.status})`
        );
      }

      setResult(
        data?.result ||
          data?.answer ||
          data?.response ||
          "No result generated."
      );
    } catch (err) {
      console.error("Agent error:", err);

      setError(
        err.message || "Unable to run agent."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-panel">
      <span className="section-label">
        AUTONOMOUS INTELLIGENCE
      </span>

      <h2>AI Agents</h2>

      <p>
        Execute specialized NEXUS AI agents for research,
        analytics, knowledge retrieval, prediction and
        enterprise solution design.
      </p>

      <div className="feature-grid">

        {/* Research */}
        <button
          className={`feature-card ${
            selectedAgent === "research"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("research");
            setResult("");
            setError("");
          }}
        >
          <strong>R Research Agent</strong>
          <span>
            Research enterprise problems and generate
            intelligence.
          </span>
        </button>

        {/* Data Analyst */}
        <button
          className={`feature-card ${
            selectedAgent === "analyst"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("analyst");
            setResult("");
            setError("");
          }}
        >
          <strong>D Data Analyst Agent</strong>
          <span>
            Analyze business information, trends and risks.
          </span>
        </button>

        {/* RAG */}
        <button
          className={`feature-card ${
            selectedAgent === "rag"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("rag");
            setResult("");
            setError("");
          }}
        >
          <strong>K RAG Knowledge Agent</strong>
          <span>
            Retrieve intelligence from enterprise documents.
          </span>
        </button>

        {/* ML Prediction */}
        <button
          className={`feature-card ${
            selectedAgent === "prediction"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("prediction");
            setResult("");
            setError("");
          }}
        >
          <strong>M ML Prediction Agent</strong>
          <span>
            Generate scenario-based enterprise predictions.
          </span>
        </button>

        {/* Decision */}
        <button
          className={`feature-card ${
            selectedAgent === "decision"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("decision");
            setResult("");
            setError("");
          }}
        >
          <strong>D Decision Agent</strong>
          <span>
            Combine evidence and generate enterprise decisions.
          </span>
        </button>

        {/* Solution */}
        <button
          className={`feature-card ${
            selectedAgent === "solution"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("solution");
            setResult("");
            setError("");
          }}
        >
          <strong>S Solution Agent</strong>
          <span>
            Design practical enterprise solutions and plans.
          </span>
        </button>

        {/* Judge */}
        <button
          className={`feature-card ${
            selectedAgent === "judge"
              ? "agent-selected"
              : ""
          }`}
          onClick={() => {
            setSelectedAgent("judge");
            setResult("");
            setError("");
          }}
        >
          <strong>J Judge Agent</strong>
          <span>
            Validate decisions, solutions and predictions.
          </span>
        </button>

      </div>

      <div className="feature-card agent-runner">

        <h3>{agents[selectedAgent].name}</h3>

        <p>{agents[selectedAgent].description}</p>

        <textarea
          className="ai-input"
          placeholder={agents[selectedAgent].placeholder}
          value={task}
          onChange={(e) => {
            setTask(e.target.value);
            setError("");
          }}
        />

        <button
          className="primary-btn"
          onClick={runAgent}
          disabled={loading}
        >
          {loading
            ? "Running Agent..."
            : "Run Agent"}
        </button>

        {error && (
          <div className="agent-error">
            {error}
          </div>
        )}

        {result && (
          <div className="ai-response">
            <ReactMarkdown>
              {result}
            </ReactMarkdown>
          </div>
        )}

      </div>
    </section>
  );
}

function AIChat() {
  const [message, setMessage] = useState("");
  const [response, setResponse] = useState("");
  const [loading, setLoading] = useState(false);

  async function sendMessage() {
    if (!message.trim()) return;

    setLoading(true);
    setResponse("");

    try {
      const token =
        localStorage.getItem("token") ||
        localStorage.getItem("jwt") ||
        localStorage.getItem("accessToken");

const res = await fetch(`${API_BASE}/api/ai/chat`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...(token
            ? {
                Authorization: `Bearer ${token}`,
              }
            : {}),
        },
        body: JSON.stringify({
         message: message.trim(),
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data?.message || "AI request failed");
      }

      setResponse(
        data?.result ||
          data?.response ||
          data?.answer ||
          JSON.stringify(data)
      );
    } catch (err) {
      setResponse(`Error: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-panel">
      <span className="section-label">NEXUS AI ENGINE</span>

      <h2>AI Chat</h2>

      <p>
        Ask NEXUS AI to research, analyze, reason and provide
        intelligent enterprise insights.
      </p>

      <textarea
        className="ai-input"
        placeholder="Ask NEXUS AI anything..."
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />

      <button
        className="primary-btn"
        onClick={sendMessage}
        disabled={loading}
      >
        {loading ? "Thinking..." : "Ask NEXUS AI"}
      </button>

      {response && (
        <div className="ai-response">
          <ReactMarkdown>{response}</ReactMarkdown>
        </div>
      )}
    </section>
  );
}


function Workflows() {
  const [problem, setProblem] = useState("");
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function executeWorkflow() {
    if (!problem.trim()) {
      setError("Please enter an enterprise problem.");
      return;
    }

    setLoading(true);
    setResult("");
    setError("");

    try {
      const token =
        localStorage.getItem("token") ||
        localStorage.getItem("jwt") ||
        localStorage.getItem("accessToken");

      const response = await fetch(
        `${API_BASE}/api/workflows/execute`,
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json",

            ...(token
              ? {
                  Authorization: `Bearer ${token}`,
                }
              : {}),
          },

          body: JSON.stringify({
            problem: problem.trim(),
          }),
        }
      );

      let data = {};

      try {
        data = await response.json();
      } catch {
        data = {};
      }

      if (!response.ok) {
        throw new Error(
          data?.message ||
            data?.error ||
            `Workflow execution failed (${response.status})`
        );
      }

      setResult(
        data?.result ||
          "No workflow result generated."
      );

    } catch (err) {
      console.error("Workflow error:", err);

      setError(
        err.message ||
          "Unable to execute autonomous workflow."
      );

    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-panel">

      <span className="section-label">
        AUTONOMOUS ENTERPRISE INTELLIGENCE
      </span>

      <h2>Multi-Agent Workflows</h2>

      <p>
        NEXUS AI autonomously coordinates specialized
        intelligence agents to transform an enterprise
        problem into a validated business solution.
      </p>

      {/* COMPLETE PIPELINE */}

      <div className="feature-grid">

        <div className="feature-card">
          <strong>01 · Research Agent</strong>

          <span>
            Investigates the enterprise problem and
            gathers relevant intelligence.
          </span>
        </div>

        <div className="feature-card">
          <strong>02 · Data Analyst Agent</strong>

          <span>
            Analyzes findings, patterns, metrics,
            risks and opportunities.
          </span>
        </div>

        <div className="feature-card">
          <strong>03 · RAG Knowledge Agent</strong>

          <span>
            Retrieves relevant information from
            enterprise knowledge.
          </span>
        </div>

        <div className="feature-card">
          <strong>04 · ML Prediction Agent</strong>

          <span>
            Generates scenario-based predictions,
            assumptions and business implications.
          </span>
        </div>

        <div className="feature-card">
          <strong>05 · Decision Agent</strong>

          <span>
            Synthesizes research, analysis and
            knowledge into an enterprise decision.
          </span>
        </div>

        <div className="feature-card">
          <strong>06 · Solution Agent</strong>

          <span>
            Designs the recommended solution,
            architecture and implementation plan.
          </span>
        </div>

        <div className="feature-card">
          <strong>07 · Judge Agent</strong>

          <span>
            Validates the prediction, decision and
            solution before final output.
          </span>
        </div>

      </div>

      {/* WORKFLOW RUNNER */}

      <div className="feature-card agent-runner">

        <span className="section-label">
          AUTONOMOUS EXECUTION
        </span>

        <h3>
          Enterprise Intelligence Workflow
        </h3>

        <p>
          Enter one business problem. NEXUS AI will
          automatically execute the complete seven-stage
          intelligence pipeline.
        </p>

        <textarea
          className="ai-input"
          rows="6"
          placeholder="Example: A company wants to reduce customer support costs while maintaining customer satisfaction."
          value={problem}
          onChange={(e) => {
            setProblem(e.target.value);
            setError("");
          }}
        />

        <button
          className="primary-btn"
          onClick={executeWorkflow}
          disabled={loading}
        >
          {loading
            ? "Executing 7-Agent Workflow..."
            : "Run Autonomous Workflow"}
        </button>

        {loading && (
          <div className="feature-card">
            <p>
              NEXUS AI is coordinating:
            </p>

            <p>
              Research → Data Analysis → RAG →
              ML Prediction → Decision → Solution → Judge
            </p>

            <small>
              This may take some time because multiple
              AI agents are executed sequentially.
            </small>
          </div>
        )}

        {error && (
          <div className="agent-error">
            {error}
          </div>
        )}

      </div>

      {/* FINAL RESULT */}

      {result && (
        <div className="feature-card">

          <span className="section-label">
            FINAL ENTERPRISE INTELLIGENCE
          </span>

          <h3>
            Autonomous Workflow Result
          </h3>

          <div className="ai-response">
            <ReactMarkdown>
              {result}
            </ReactMarkdown>
          </div>

        </div>
      )}

    </section>
  );
}

function Analytics() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadAnalytics() {
      try {
        const token =
          localStorage.getItem("token") ||
          localStorage.getItem("jwt") ||
          localStorage.getItem("accessToken");

        const response = await fetch(
          `${API_BASE}/api/analytics`,
          {
            headers: token
              ? {
                  Authorization: `Bearer ${token}`,
                }
              : {},
          }
        );

        const text = await response.text();

let result = {};

if (text) {
  try {
    result = JSON.parse(text);
  } catch {
    result = {};
  }
}

if (!response.ok) {
  throw new Error(
    result?.message ||
    `Analytics request failed (${response.status})`
  );
}

        setData(result);
      } catch (err) {
        console.error("Analytics error:", err);
        setError(err.message || "Unable to load analytics.");
      } finally {
        setLoading(false);
      }
    }

    loadAnalytics();
  }, []);

  if (loading) {
    return (
      <section className="page-panel">
        <span className="section-label">
          NEXUS AI ANALYTICS
        </span>

        <h2>Analytics</h2>

        <p>Loading enterprise analytics...</p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="page-panel">
        <span className="section-label">
          NEXUS AI ANALYTICS
        </span>

        <h2>Analytics</h2>

        <p>{error}</p>
      </section>
    );
  }

  return (
    <section className="page-panel">

      <span className="section-label">
        NEXUS AI ANALYTICS
      </span>

      <h2>Agent Analytics</h2>

      <p>
        Monitor agent executions, success rate, failures
        and execution performance.
      </p>

      <div className="stats">

        <div className="stat-card">
          <span>Total Executions</span>
          <strong>
            {data?.totalExecutions ?? 0}
          </strong>
          <small>Agent executions</small>
        </div>

        <div className="stat-card">
          <span>Successful</span>
          <strong>
            {data?.successfulExecutions ?? 0}
          </strong>
          <small>Completed successfully</small>
        </div>

        <div className="stat-card">
          <span>Failed</span>
          <strong>
            {data?.failedExecutions ?? 0}
          </strong>
          <small>Failed executions</small>
        </div>

        <div className="stat-card">
          <span>Avg Duration</span>
          <strong>
            {data?.averageDurationMs ?? 0} ms
          </strong>
          <small>Average execution time</small>
        </div>

      </div>

      <div className="panel">

        <div className="panel-header">
          <div>
            <span className="section-label">
              EXECUTION HISTORY
            </span>

            <h3>Agent Activity</h3>
          </div>
        </div>

        {data?.executions?.length === 0 ? (
          <p>
            No agent executions recorded yet.
          </p>
        ) : (
          data.executions.map((execution) => (
            <div
              className="agent"
              key={execution.id}
            >
              <div className="agent-icon">
                {execution.agentName?.charAt(0) || "A"}
              </div>

              <div>
                <strong>
                  {execution.agentName}
                </strong>

                <span>
                  {execution.durationMs} ms
                </span>
              </div>

              <span
                className={
                  execution.status === "SUCCESS"
                    ? "online"
                    : "offline-status"
                }
              >
                {execution.status}
              </span>
            </div>
          ))
        )}

      </div>

    </section>
  );
}



function App() {
  const [activePage, setActivePage] = useState("Dashboard");
  const [backendOnline, setBackendOnline] = useState(false);

  // Check whether user is already logged in
  const [isLoggedIn, setIsLoggedIn] = useState(
  !!localStorage.getItem("token")
);

const [authPage, setAuthPage] = useState("login");

 const navItems = [
  { name: "Dashboard", icon: LayoutDashboard },
  { name: "AI Chat", icon: MessageSquare },
  { name: "Agents", icon: Bot },
  { name: "Knowledge", icon: BookOpen },
  { name: "Workflows", icon: Workflow },
  { name: "Analytics", icon: BarChart3 },
  { name: "Settings", icon: SettingsIcon },
];

  useEffect(() => {
    async function checkBackend() {
      try {
        const response = await fetch(`${API_BASE}/api/health`);
        setBackendOnline(response.ok);
      } catch {
        setBackendOnline(false);
      }
    }

    checkBackend();

    const interval = setInterval(checkBackend, 10000);

    return () => clearInterval(interval);
  }, []);

  // Login successful
  function handleLogin(userData) {
    console.log("Logged in user:", userData);
    setIsLoggedIn(true);
    setActivePage("Dashboard");
  }

  // Logout
  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("jwt");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("userId");

    setIsLoggedIn(false);
    setActivePage("Dashboard");
  }

  // Show Login page if user is not authenticated
  // Show Login/Register page if user is not authenticated
if (!isLoggedIn) {
  if (authPage === "register") {
    return (
      <Register
        onRegister={() => setAuthPage("login")}
        onGoToLogin={() => setAuthPage("login")}
      />
    );
  }

  return (
    <Login
      onLogin={handleLogin}
      onGoToRegister={() => setAuthPage("register")}
    />
  );
}

  function renderPage() {
    switch (activePage) {
      case "Dashboard":
        return <Dashboard setActivePage={setActivePage} />;

      case "AI Chat":
        return <AIChat />;

      case "Knowledge":
        return <Knowledge />;

      case "Agents":
        return <Agents />;

      case "Workflows":
        return <Workflows />;

      case "Analytics":
  return <Analytics />;


      case "Settings":
  return <Settings />;

      default:
        return <Dashboard setActivePage={setActivePage} />;
    }
  }

  return (
    <div className="app">

      {/* SIDEBAR */}
      <aside className="sidebar">

        <div className="logo">
          <div className="logo-icon">
          <BrainCircuit size={24} strokeWidth={1.8} />
        </div>

          <div>
            <h2>NEXUS AI</h2>
            <span>Enterprise Intelligence</span>
          </div>
        </div>

        <nav>
          {navItems.map((item) => (
            <button
              key={item.name}
              className={`nav-item ${
                activePage === item.name ? "active" : ""
              }`}
              onClick={() => setActivePage(item.name)}
            >
             <span className="nav-icon">
              <item.icon size={18} strokeWidth={1.8} />
              </span>
              <span>{item.name}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">

          <div className="user-card">

            <div className="avatar">
              {(localStorage.getItem("userName") || "A")
                .charAt(0)
                .toUpperCase()}
            </div>

            <div>
              <strong>
                {localStorage.getItem("userName") || "Admin User"}
              </strong>

              <span>Enterprise</span>
            </div>

          </div>

          <button
  className="logout-btn"
  onClick={handleLogout}
  title="Sign out of NEXUS AI"
>
  <span className="logout-icon">
  <LogOut size={17} />
</span>
  <span>Logout</span>
</button>

        </div>

      </aside>

      {/* MAIN */}
      <main className="main">

        <div className="topbar">

          <div>
            <span className="eyebrow">
              AUTONOMOUS ENTERPRISE INTELLIGENCE
            </span>

            <h1>{activePage}</h1>
          </div>
{/* 
          <div
            className={`status ${
              backendOnline
                ? "online-status"
                : "offline-status"
            }`}
          >
            <span className="status-dot"></span>

            {backendOnline
              ? "Backend Online"
              : "Backend Offline"}
          </div> */}

        </div>

        {renderPage()}

      </main>

    </div>
  );
}

export default App;



