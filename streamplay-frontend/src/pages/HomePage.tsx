// src/pages/HomePage.tsx

import { logout } from "../services/authService";

function HomePage() {
  const handleLogout = () => {
    logout();
    window.location.href = "/login";
  };

  return (
    <div className="container mt-5">
      <h1 className="text-success">🎉 Willkommen bei StreamPlay!</h1>
      <p>Du bist eingeloggt – viel Spaß!</p>

      <button className="btn btn-outline-danger mt-3" onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
}

export default HomePage;