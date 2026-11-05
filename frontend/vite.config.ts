import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import { loadEnv } from 'vite';

export default defineConfig(({ mode }) => ({
  plugins: [react()],
  server: { port: 5173, strictPort: true, proxy: { '/api': loadEnv(mode, '.', '').API_PROXY_TARGET || 'http://127.0.0.1:8080' } },
  test: { environment: 'jsdom', setupFiles: ['./src/test-setup.ts'], restoreMocks: true },
}));
