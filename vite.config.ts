import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// Relative base so the built site works from any path (GitHub Pages, a subfolder, etc.)
export default defineConfig({
  base: './',
  plugins: [react()],
  test: {
    environment: 'node',
    include: ['src/**/*.test.ts'],
  },
})
