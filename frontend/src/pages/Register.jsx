import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });

    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    try {
      await api.post("/auth/register", {
        name: form.name.trim(),
        email: form.email.trim(),
        password: form.password,
      });

      navigate("/login", {
        replace: true,
        state: {
          message: "Account created successfully. Please sign in.",
        },
      });
    } catch (err) {
      if (err.response?.status === 400) {
        const backendMessage =
          err.response?.data?.message ||
          err.response?.data?.error;

        setError(
          backendMessage ||
            "Please check your information and try again."
        );
      } else if (err.response?.status === 409) {
        setError("An account with this email already exists.");
      } else {
        setError(
          err.response?.data?.message ||
            "Unable to create your account. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-shell">

        <div className="auth-brand-panel">
          <div className="brand-mark">B</div>

          <div>
            <div className="brand-name">BizFlow</div>
            <div className="brand-tagline">
              Project & Task Management
            </div>
          </div>

          <div className="auth-visual">
            <div className="visual-card visual-card-main">
              <div className="visual-card-header">
                <span>Workspace</span>
                <span className="status-dot" />
              </div>

              <div className="visual-progress">
                <div />
              </div>

              <div className="visual-row">
                <span />
                <span />
                <span />
              </div>
            </div>

            <div className="visual-card visual-card-small">
              <strong>24</strong>
              <span>Tasks completed</span>
            </div>
          </div>

          <p className="auth-panel-footer">
            Organize work. Track progress. Deliver better.
          </p>
        </div>

        <div className="auth-form-panel">

          <div className="auth-form-container">

            <div className="mobile-brand">
              <div className="brand-mark">B</div>
              <span>BizFlow</span>
            </div>

            <div className="auth-heading">
              <span className="eyebrow">GET STARTED</span>

              <h1>Create your account</h1>

              <p>
                Set up your workspace and start managing
                projects with BizFlow.
              </p>
            </div>

            <form onSubmit={handleSubmit}>

              <div className="form-group">
                <label htmlFor="name">Full name</label>

                <input
                  id="name"
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Enter your full name"
                  autoComplete="name"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="email">Email address</label>

                <input
                  id="email"
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="you@example.com"
                  autoComplete="email"
                  required
                />
              </div>

              <div className="form-row">

                <div className="form-group">
                  <label htmlFor="password">Password</label>

                  <input
                    id="password"
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                    placeholder="Create password"
                    autoComplete="new-password"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="confirmPassword">
                    Confirm password
                  </label>

                  <input
                    id="confirmPassword"
                    type="password"
                    name="confirmPassword"
                    value={form.confirmPassword}
                    onChange={handleChange}
                    placeholder="Repeat password"
                    autoComplete="new-password"
                    required
                  />
                </div>

              </div>

              {error && (
                <div className="error-message">
                  {error}
                </div>
              )}

              <button
                type="submit"
                className="primary-button"
                disabled={loading}
              >
                {loading ? "Creating account..." : "Create account"}
              </button>

            </form>

            <div className="auth-switch">
              Already have an account?
              <Link to="/login">Sign in</Link>
            </div>

          </div>

        </div>

      </div>
    </div>
  );
}

export default Register;
