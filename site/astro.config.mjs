import { defineConfig } from 'astro/config';

export default defineConfig({
  site: 'https://under-utils.howied.me',
  output: 'static',
  devToolbar: {
    enabled: false
  },
  markdown: {
    shikiConfig: {
      theme: 'github-dark',
      wrap: true
    }
  },
  vite: {
    build: {
      target: 'es2022'
    }
  }
});
