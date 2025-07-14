// vite.config.ts

import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// Diese Funktion wird automatisch von Vite beim Start aufgerufen.
// Vite übergibt den "mode" (z. B. 'development' oder 'production')
export default ({ mode }) => {
  // Lädt passende .env-Datei, z. B. .env.development oder .env.production
  // und schreibt alle VITE_ Variablen in process.env
  const env = loadEnv(mode, process.cwd(), '');

  return defineConfig({
    // Nur Variablen mit Präfix "VITE_" werden im Frontend verfügbar gemacht
    envPrefix: 'VITE_',

    // Diese define-Zuweisungen erlauben Zugriff auf Variablen im Frontend via import.meta.env.XYZ
    define: {
      'import.meta.env.VITE_ENV_VARIABLE': JSON.stringify(env.VITE_ENV_VARIABLE),
    },

    plugins: [react()],

    server: {
      port: parseInt(env.VITE_PORT) || 3000,
      strictPort: true,

      headers: {
        // Diese zwei Header sind optional, wenn du z. B. SharedArrayBuffer brauchst
        "Cross-Origin-Embedder-Policy": "credentialless",
        "Cross-Origin-Opener-Policy": "same-origin"
      },

      // ➜ DEV: Leitet API-Calls (z. B. fetch('/api/xyz')) an dein Backend weiter
      proxy: {
        "/api": {
          target: 'http://localhost:80',        //env.VITE_API_TARGET_URL, //  http://localhost:6969
          changeOrigin: true,
          secure: false,
          ws: true,
          rewrite: (path) => path.replace(/^\/api/, ''),

          // Debug-Ausgaben
          configure: (proxy, _options) => {
            proxy.on('error', (err, _req, _res) => {
              console.error('Proxy Error:', err);
            });
            proxy.on('proxyReq', (proxyReq, req, _res) => {
              console.log('🔁 Request →', req.method, req.url);
            });
            proxy.on('proxyRes', (proxyRes, req, _res) => {
              console.log('✅ Response ←', proxyRes.statusCode, req.url);
            });
          },
        },
      },
    },

    // Für 'vite preview' im Production-Test (meist nicht nötig)
    preview: {
      port: parseInt(env.VITE_PORT) || 3000,
    },
  });
}