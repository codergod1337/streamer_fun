// src/pages/LoginPage.tsx

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";

function LoginPage() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg("");

    try {
      await login(userName, password);
      navigate("/"); // Redirect zur Startseite
    } catch (err) {
      setErrorMsg("Login fehlgeschlagen! bitte überprüfe deine Daten.");
    }
  };

  return (
    <div className="container d-flex align-items-center justify-content-center vh-100">
      <div className="card p-4 shadow-lg" style={{ minWidth: "350px", maxWidth: "400px", width: "100%" }}>
        <h2 className="text-center mb-4 text-primary">🎮 StreamPlay Login</h2>

        {errorMsg && (
          <div className="alert alert-danger text-center py-2">{errorMsg}</div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="username" className="form-label">
              Benutzername
            </label>
            <input
              id="username"
              className="form-control"
              type="text"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              required
              autoFocus
              placeholder="User Name"
            />
          </div>

          <div className="mb-3">
            <label htmlFor="password" className="form-label">
              Passwort
            </label>
            <input
              id="password"
              className="form-control"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="pw"
            />
          </div>

          <button type="submit" className="btn btn-primary w-100">
            🔐 Login
          </button>
        </form>

        <div className="text-center mt-3 text-muted" style={{ fontSize: "0.9rem" }}>
          Kein Konto? Frag den Admin! 😎
        </div>
      </div>
    </div>
  );
}

export default LoginPage;