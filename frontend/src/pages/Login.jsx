import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const successMessage = location.state?.message;

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
    setLoading(true);

    try {
      await login(form.email.trim(), form.password);

      navigate("/dashboard", { replace: true });
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else {
        setError(
          err.response?.data?.message ||
            err.response?.data?.error ||
            "Unable to sign in. Please try again."
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
                <span>Project overview</span>
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

              <strong>12</strong>

              <span>Active projects</span>

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

              <span className="eyebrow">
                WELCOME BACK
              </span>

              <h1>Sign in to BizFlow</h1>

              <p>
                Access your projects, tasks, and workspace.
              </p>

            </div>

            {successMessage && (
              <div className="success-message">
                {successMessage}
              </div>
            )}

            <form onSubmit={handleSubmit}>

              <div className="form-group">

                <label htmlFor="email">
                  Email address
                </label>

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

              <div className="form-group">

                <div className="label-row">

                  <label htmlFor="password">
                    Password
                  </label>

                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() =>
                      setShowPassword(!showPassword)
                    }
                  >
                    {showPassword ? "Hide" : "Show"}
                  </button>

                </div>

                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />

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
                {loading ? "Signing in..." : "Sign in"}
              </button>

            </form>

            <div className="auth-switch">
              Don't have an account?
              <Link to="/register">Create an account</Link>
            </div>

          </div>

        </div>

      </div>

    </div>
  );
}

export default Login;
