import { useState } from "react";
import "./App.css";

const API_BASE = "https://nexus-ai-n3oc.onrender.com";

function Login({ onLogin, onGoToRegister }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleLogin(e) {
    e.preventDefault();

    if (!email.trim() || !password.trim()) {
      setError("Please enter email and password.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await fetch(`${API_BASE}/api/users/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email.trim(),
          password,
        }),
      });

      let data = {};

      try {
        data = await response.json();
      } catch {
        data = {};
      }

      if (!response.ok) {
        throw new Error(
          typeof data === "string"
            ? data
            : data?.message || "Invalid email or password."
        );
      }

      if (!data?.token) {
        throw new Error("Login successful, but no JWT token was received.");
      }

      localStorage.setItem("token", data.token);

      if (data.name) {
        localStorage.setItem("userName", data.name);
      }

      if (data.email) {
        localStorage.setItem("userEmail", data.email);
      }

      if (data.id) {
        localStorage.setItem("userId", String(data.id));
      }

      onLogin({
        token: data.token,
        name: data.name,
        email: data.email,
        id: data.id,
      });
    } catch (err) {
      console.error("Login error:", err);
      setError(err.message || "Unable to login.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">

        <div className="login-logo">
          <div className="logo-icon">N</div>
          <div>
            <h1>NEXUS AI</h1>
            <span>Enterprise Intelligence</span>
          </div>
        </div>

        <div className="login-heading">
          <span className="section-label">
            AUTONOMOUS ENTERPRISE INTELLIGENCE
          </span>

          <h2>Welcome Back</h2>

          <p>
            Sign in to access your NEXUS AI enterprise intelligence platform.
          </p>
        </div>

        <form onSubmit={handleLogin}>

          <div className="form-group">
            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                setError("");
              }}
            />
          </div>

          <div className="form-group">
            <label>Password</label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError("");
              }}
            />
          </div>

          {error && (
            <div className="login-error">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="primary-btn login-btn"
            disabled={loading}
          >
            {loading ? "Signing in..." : "Sign In"}
          </button>

        </form>

        <div className="login-footer">
            <div className="auth-switch">
  <span>Don't have an account?</span>

  <button
    type="button"
    onClick={onGoToRegister}
    className="auth-link"
  >
    Create Account
  </button>
</div>
          <span>NEXUS AI</span>
          <small>Secure Enterprise Intelligence Platform</small>
        </div>

      </div>
    </div>
  );
}

export default Login;
