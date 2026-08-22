import { defineConfig } from "vitest/config";

export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  test: {
    globals: true,
    include: ["src/**/*.test.ts"]
  }
});
