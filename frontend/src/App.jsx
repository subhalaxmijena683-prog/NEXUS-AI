
import Login from "./Login";
import Register from "./Register";
import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import "./App.css";
//import "./index.css";

const API_BASE = "http://localhost:8081";

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
          <strong>8</strong>
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
            <div className="agent-icon">R</div>

            <div>
              <strong>Research Agent</strong>
              <span>Web research and intelligence</span>
            </div>

            <span className="online">ONLINE</span>
          </div>

          <div className="agent">
            <div className="agent-icon">D</div>

            <div>
              <strong>Data Analyst</strong>
              <span>Data analysis and insights</span>
            </div>

            <span className="online">ONLINE</span>
          </div>

          <div className="agent">
            <div className="agent-icon">K</div>

            <div>
              <strong>Knowledge Agent</strong>
              <span>Enterprise RAG intelligence</span>
            </div>

            <span className="online">ONLINE</span>
          </div>

          <div className="agent">
            <div className="agent-icon">J</div>

            <div>
              <strong>Judge Agent</strong>
              <span>Decision validation</span>
            </div>

            <span className="online">ONLINE</span>
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
              <span>✦</span>
              Ask NEXUS AI
            </button>

            <button onClick={() => setActivePage("Knowledge")}>
              <span>◈</span>
              Search Knowledge
            </button>

            <button onClick={() => setActivePage("Agents")}>
              <span>◆</span>
              Run AI Agent
            </button>

            <button onClick={() => setActivePage("Workflows")}>
              <span>⚡</span>
              Create Workflow
            </button>
          </div>
        </div>
      </div>
    </>
  );
}

function GenericPage({ title, description, icon }) {
  return (
    <section className="page-panel">
      <span className="section-label">{icon} NEXUS AI</span>

      <h2>{title}</h2>

      <p>{description}</p>

      <div className="feature-grid">
        <div className="feature-card">
          <strong>Intelligent</strong>
          <span>Powered by the NEXUS AI agent architecture.</span>
        </div>

        <div className="feature-card">
          <strong>Enterprise Ready</strong>
          <span>Designed for autonomous enterprise workflows.</span>
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
      description: "Web research and intelligence",
      endpoint: "/api/agents/research",
      placeholder:
        "Example: Research the benefits of AI agents in enterprise applications.",
    },

    analyst: {
      name: "Data Analyst Agent",
      icon: "D",
      description: "Data analysis and business insights",
      endpoint: "/api/agents/data-analyst",
      placeholder:
        "Example: Analyze monthly sales data and identify important trends.",
    },

    rag: {
      name: "RAG Knowledge Agent",
      icon: "K",
      description: "Enterprise knowledge and document intelligence",
      endpoint: "/api/agents/rag",
      placeholder:
        "Example: What are the technical skills mentioned in my resume?",
    },
  };

  async function runAgent() {
    if (!task.trim()) {
      setError("Please enter a task for the agent.");
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
          data?.message || "Agent request failed."
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
      setError(err.message || "Unable to run agent.");
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
        analytics and enterprise knowledge intelligence.
      </p>

      <div className="feature-grid">

        {/* Research Agent */}
        <button
          className={`feature-card ${
            selectedAgent === "research" ? "agent-selected" : ""
          }`}
          onClick={() => {
            setSelectedAgent("research");
            setResult("");
            setError("");
          }}
        >
          <strong>🔎 Research Agent</strong>
          <span>
            Research topics and generate intelligent insights.
          </span>
        </button>

        {/* Data Analyst */}
        <button
          className={`feature-card ${
            selectedAgent === "analyst" ? "agent-selected" : ""
          }`}
          onClick={() => {
            setSelectedAgent("analyst");
            setResult("");
            setError("");
          }}
        >
          <strong>📊 Data Analyst Agent</strong>
          <span>
            Analyze data, identify trends and generate insights.
          </span>
        </button>

        {/* RAG */}
        <button
          className={`feature-card ${
            selectedAgent === "rag" ? "agent-selected" : ""
          }`}
          onClick={() => {
            setSelectedAgent("rag");
            setResult("");
            setError("");
          }}
        >
          <strong>🧠 RAG Knowledge Agent</strong>
          <span>
            Ask questions using your uploaded enterprise documents.
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
          onChange={(e) => setTask(e.target.value)}
        />

        <button
          className="primary-btn"
          onClick={runAgent}
          disabled={loading}
        >
          {loading ? "Running Agent..." : "Run Agent"}
        </button>

        {error && (
          <div className="agent-error">
            {error}
          </div>
        )}

        {result && (
          <div className="ai-response">
            <ReactMarkdown>{result}</ReactMarkdown>
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

      const res = await fetch(`${API_BASE}/api/agents/chat`, {
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
          question: message.trim(),
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
          data?.message || "Workflow execution failed."
        );
      }

      setResult(
        data?.result ||
          "No workflow result generated."
      );
    } catch (err) {
      console.error("Workflow error:", err);

      setError(
        err.message || "Unable to execute workflow."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-panel">

      <span className="section-label">
       ⚙ AUTONOMOUS ENTERPRISE INTELLIGENCE
      </span>

      <h2>Multi-Agent Workflows</h2>

      <p>
        NEXUS AI coordinates specialized agents to transform
        an enterprise problem into research, analysis,
        knowledge-based reasoning and a final decision.
      </p>

      {/* Workflow Pipeline */}
      <div className="feature-grid">

        <div className="feature-card">
          <strong>🔎 Research Agent</strong>
          <span>
            Investigates the problem and gathers relevant
            intelligence.
          </span>
        </div>

        <div className="feature-card">
          <strong>📊 Data Analyst Agent</strong>
          <span>
            Analyzes research findings, trends, risks and
            opportunities.
          </span>
        </div>

        <div className="feature-card">
          <strong>🧠  RAG Knowledge Agent</strong>
          <span>
            Retrieves relevant information from enterprise
            documents.
          </span>
        </div>

        <div className="feature-card">
          <strong>🤖Decision Agent</strong>
          <span>
            Combines all agent outputs and generates a
            practical enterprise decision.
          </span>
        </div>

      </div>

      {/* Workflow Runner */}
      <div className="feature-card agent-runner">

        <h3>Enterprise Intelligence Workflow</h3>

        <p>
          Enter a business problem and NEXUS AI will
          automatically coordinate the complete multi-agent
          workflow.
        </p>

        <textarea
          className="ai-input"
          rows="5"
          placeholder="Example: Analyze how AI agents can improve enterprise productivity and decision making."
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
            ? "Executing Multi-Agent Workflow..."
            : "Run Autonomous Workflow"}
        </button>

        {error && (
          <div className="agent-error">
            {error}
          </div>
        )}

      </div>

      {/* Workflow Result */}
      {result && (
        <div className="feature-card">

          <span className="section-label">
            WORKFLOW EXECUTION RESULT
          </span>

          <h3>Autonomous Enterprise Intelligence</h3>

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

function App() {
  const [activePage, setActivePage] = useState("Dashboard");
  const [backendOnline, setBackendOnline] = useState(false);

  // Check whether user is already logged in
  const [isLoggedIn, setIsLoggedIn] = useState(
  !!localStorage.getItem("token")
);

const [authPage, setAuthPage] = useState("login");

 const navItems = [
  { name: "Dashboard", icon: "⌂" },
  { name: "AI Chat", icon: "✦" },
  { name: "Agents", icon: "◈" },
  { name: "Knowledge", icon: "▣" },
  { name: "Workflows", icon: "⚙" },
  { name: "Analytics", icon: "▥" },
  { name: "Settings", icon: "⚙" },
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
        return (
          <GenericPage
            title="Analytics"
            icon="▥"
            description="Monitor enterprise intelligence, agent activity, workflow execution and business insights."
          />
        );

      case "Settings":
        return (
          <GenericPage
            title="Settings"
            icon="⚙"
            description="Configure NEXUS AI, enterprise preferences, AI models and platform settings."
          />
        );

      default:
        return <Dashboard setActivePage={setActivePage} />;
    }
  }

  return (
    <div className="app">

      {/* SIDEBAR */}
      <aside className="sidebar">

        <div className="logo">
          <div className="logo-icon">N</div>

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
              <span className="nav-icon">{item.icon}</span>
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
  <span className="logout-icon">⇥</span>
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
          </div>

        </div>

        {renderPage()}

      </main>

    </div>
  );
}

export default App;



