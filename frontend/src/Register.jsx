import { useState } from "react";
import "./App.css";

const API_BASE = "https://nexus-ai-n3oc.onrender.com";

function Register({ onRegister, onGoToLogin }) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleRegister(e) {
    e.preventDefault();

    setError("");
    setSuccess("");

    if (!name.trim() || !email.trim() || !password || !confirmPassword) {
      setError("Please fill in all fields.");
      return;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${API_BASE}/api/users/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: name.trim(),
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
            : data?.message || "Registration failed."
        );
      }

      setSuccess("Account created successfully!");

      setTimeout(() => {
        onRegister();
      }, 800);

    } catch (err) {
      console.error("Registration error:", err);
      setError(err.message || "Unable to register.");
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

          <h2>Create Account</h2>

          <p>
            Create your account to access the NEXUS AI enterprise platform.
          </p>
        </div>

        <form onSubmit={handleRegister}>

          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              placeholder="Enter your name"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                setError("");
              }}
            />
          </div>

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
              placeholder="Create a password"
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                setError("");
              }}
            />
          </div>

          <div className="form-group">
            <label>Confirm Password</label>
            <input
              type="password"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e) => {
                setConfirmPassword(e.target.value);
                setError("");
              }}
            />
          </div>

          {error && (
            <div className="login-error">
              {error}
            </div>
          )}

          {success && (
            <div className="login-success">
              {success}
            </div>
          )}

          <button
            type="submit"
            className="primary-btn login-btn"
            disabled={loading}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </button>

        </form>

        <div className="auth-switch">
          <span>Already have an account?</span>

          <button
            type="button"
            onClick={onGoToLogin}
            className="auth-link"
          >
            Sign In
          </button>
        </div>

        <div className="login-footer">
          <span>NEXUS AI</span>
          <small>Secure Enterprise Intelligence Platform</small>
        </div>

      </div>
    </div>
  );
}

export default Register;
