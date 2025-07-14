// src/services/authService.ts

//const API_URL = "http://localhost/api/jwt";
import axios from "axios";
const API_URL = "/api/jwt";
const TOKEN_KEY = "jwt_token";

export async function login(userName: string, password: string): Promise<void> {
  const payload = { userName, password };

  axios
    .post(API_URL, payload, {
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json;charset=UTF-8",
      },
    })
    .then(({ data }) => {
      localStorage.setItem(TOKEN_KEY, data);
    });


}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function logout(): void {
  localStorage.removeItem(TOKEN_KEY);
}