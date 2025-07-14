// src/App.tsx
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage.tsx"; // deine Hauptseite
import { getToken } from "./services/authService";

function App() {
  const isLoggedIn = !!getToken();

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={isLoggedIn ? <HomePage /> : <Navigate to="/login" />} />
    </Routes>
  );
}

export default App;