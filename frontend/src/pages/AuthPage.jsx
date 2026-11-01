import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

function passwordScore(password) {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score += 1;
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score += 1;
  if (/\d/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;
  if (password.length < 8) score = Math.min(score, 1);
  return Math.max(score, 1);
}

function AuthPage({ initialView = "login" }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, register } = useAuth();
  const view = location.pathname === "/register" ? "register" : initialView;
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false);

  const score = useMemo(() => passwordScore(form.password), [form.password]);

  useEffect(() => {
    document.title = `BizFlow · ${view === "login" ? "Sign in" : "Create account"}`;
  }, [view]);

  const changeView = (nextView) => {
    setErrors({});
    setMessage("");
    navigate(nextView === "register" ? "/register" : "/login", { replace: true });
  };

  const clearNavigationMessage = () => {
    if (location.state?.message) {
      navigate(location.pathname, { replace: true, state: null });
    }
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
    setErrors((current) => ({ ...current, [name]: "" }));
    setMessage("");
    clearNavigationMessage();
  };

  const validate = () => {
    const nextErrors = {};
    if (view === "register" && form.name.trim().length < 2) {
      nextErrors.name = "Enter your full name.";
    }
    if (!emailPattern.test(form.email.trim())) {
      nextErrors.email = "Enter a valid email, like you@company.com.";
    }
    if (!form.password) {
      nextErrors.password = "Enter your password.";
    } else if (view === "register" && score < 2) {
      nextErrors.password = "Too easy to guess. Add length, numbers or symbols.";
    }
    if (view === "register" && form.password !== form.confirmPassword) {
      nextErrors.confirmPassword = "Passwords do not match.";
    }
    if (view === "register" && !termsAccepted) {
      nextErrors.terms = "Accept the terms to create your account.";
    }
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    clearNavigationMessage();
    if (!validate()) return;

    setLoading(true);
    setMessage("");
    try {
      if (view === "login") {
        await login(form.email.trim(), form.password);
        navigate("/dashboard", { replace: true });
      } else {
        await register({
          name: form.name.trim(),
          email: form.email.trim(),
          password: form.password,
        });
        setForm({ name: "", email: form.email, password: "", confirmPassword: "" });
        setTermsAccepted(false);
        setMessage("Account created successfully. Please sign in.");
        navigate("/login", {
          replace: true,
          state: { message: "Account created successfully. Please sign in." },
        });
      }
    } catch (error) {
      if (error.response?.status === 401) {
        setMessage("Invalid email or password.");
      } else if (error.response?.status === 409) {
        setMessage("An account with this email already exists.");
      } else {
        setMessage(
          error.response?.data?.message ||
            error.response?.data?.error ||
            (view === "login"
              ? "Unable to sign in. Please try again."
              : "Unable to create your account. Please try again.")
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const successMessage = location.state?.message || message;
  const fieldClass = (name) => `source-auth-field${errors[name] ? " has-error" : ""}`;

  return (
    <div className="source-auth-page">
      <div className="source-auth-stage" aria-hidden="true">
        <span className="source-auth-blob source-auth-blob-one" />
        <span className="source-auth-blob source-auth-blob-two" />
        <span className="source-auth-blob source-auth-blob-three" />
      </div>

      <main className="source-auth-layout">
        <section className="source-auth-pitch">
          <div className="source-auth-brand">
            <img src="/bizflow-logo.png" alt="BizFlow" />
            <span>BizFlow</span>
          </div>
          <h1>Every task, <em>moving.</em></h1>
          <p>Invoices, stock, approvals and your team in one steady flow, so nothing waits on someone else&apos;s inbox.</p>
          <div className="source-auth-chips" aria-hidden="true">
            <span><i />Invoice #2041 approved</span>
            <span><i />Stock low: reorder sent</span>
            <span><i />Team synced 2 min ago</span>
          </div>
        </section>

        <section className="source-auth-card">
          <div className="source-auth-tabs" role="tablist" aria-label="Authentication">
            <span className={`source-auth-pill ${view === "register" ? "is-register" : ""}`} />
            <button type="button" role="tab" aria-selected={view === "login"} onClick={() => changeView("login")}>Sign in</button>
            <button type="button" role="tab" aria-selected={view === "register"} onClick={() => changeView("register")}>Create account</button>
          </div>

          <form onSubmit={handleSubmit} noValidate>
            <h2>{view === "login" ? "Welcome back" : "Start your workspace"}</h2>
            <p className="source-auth-sub">
              {view === "login" ? "Sign in to pick up where your team left off." : "Free for 14 days. No card needed."}
            </p>

            {view === "register" && (
              <div className={fieldClass("name")}>
                <input id="auth-name" name="name" type="text" value={form.name} onChange={handleChange} placeholder=" " autoComplete="name" />
                <label htmlFor="auth-name">Full name</label>
                {errors.name && <p>{errors.name}</p>}
              </div>
            )}

            <div className={fieldClass("email")}>
              <input id="auth-email" name="email" type="email" value={form.email} onChange={handleChange} placeholder=" " autoComplete="email" />
              <label htmlFor="auth-email">Work email</label>
              {errors.email && <p>{errors.email}</p>}
            </div>

            <div className={fieldClass("password")}>
              <input id="auth-password" name="password" type={showPassword ? "text" : "password"} value={form.password} onChange={handleChange} placeholder=" " autoComplete={view === "login" ? "current-password" : "new-password"} />
              <label htmlFor="auth-password">Password</label>
              <button type="button" className="source-auth-eye" onClick={() => setShowPassword((current) => !current)} aria-label={showPassword ? "Hide password" : "Show password"}>{showPassword ? "Hide" : "Show"}</button>
              {errors.password && <p>{errors.password}</p>}
            </div>

            {view === "register" && (
              <>
                <div className={fieldClass("confirmPassword")}>
                  <input id="auth-confirm-password" name="confirmPassword" type={showConfirmPassword ? "text" : "password"} value={form.confirmPassword} onChange={handleChange} placeholder=" " autoComplete="new-password" />
                  <label htmlFor="auth-confirm-password">Confirm password</label>
                  <button type="button" className="source-auth-eye" onClick={() => setShowConfirmPassword((current) => !current)} aria-label={showConfirmPassword ? "Hide password" : "Show password"}>{showConfirmPassword ? "Hide" : "Show"}</button>
                  {errors.confirmPassword && <p>{errors.confirmPassword}</p>}
                </div>
                <div className="source-auth-meter" data-score={score}>
                  {[1, 2, 3, 4].map((item) => <span key={item} />)}
                </div>
                <div className="source-auth-meter-text">
                  {score ? ["", "Weak", "Fair", "Good", "Strong"][score] + (score < 3 ? ": add length, numbers or symbols." : ".") : "Use 8+ characters with a mix of letters, numbers and symbols."}
                </div>
                <label className="source-auth-check">
                  <input type="checkbox" checked={termsAccepted} onChange={(event) => setTermsAccepted(event.target.checked)} />
                  <span>I agree to the Terms and Privacy Policy</span>
                </label>
                {errors.terms && <p className="source-auth-form-error">{errors.terms}</p>}
              </>
            )}

            {view === "login" && (
              <div className="source-auth-row">
                <label className="source-auth-check"><input type="checkbox" defaultChecked /> <span>Keep me signed in</span></label>
                <button type="button" className="source-auth-link">Forgot password?</button>
              </div>
            )}

            {(successMessage || message) && <div className={message && !location.state?.message ? "source-auth-form-error" : "source-auth-success"}>{successMessage || message}</div>}
            <button className="source-auth-cta" type="submit" disabled={loading}>{loading ? (view === "login" ? "Signing in..." : "Creating account...") : view === "login" ? "Sign in" : "Create account"}</button>

            {view === "login" && <><div className="source-auth-divider">or continue with</div><div className="source-auth-social"><button type="button" disabled>Google</button><button type="button" disabled>Microsoft</button></div></>}
            <p className="source-auth-alt">{view === "login" ? "New to BizFlow?" : "Already have an account?"} <button type="button" className="source-auth-link" onClick={() => changeView(view === "login" ? "register" : "login")}>{view === "login" ? "Create an account" : "Sign in"}</button></p>
          </form>
        </section>
      </main>
    </div>
  );
}

export default AuthPage;
