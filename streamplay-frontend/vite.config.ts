// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  base: '/frontend/', // wichtig für NGINX-Pfadpräfix
  plugins: [react()],
  build: {
    outDir: 'frontend', // optional, Standard ist 'dist'
  },
  //server: {
   // port: 5000,
  //  proxy: {
    //  '/api': {
    //    target: 'http://localhost:6969',
    //    changeOrigin: false,
    //    secure: false,
    //  },
   // },
  //},
});