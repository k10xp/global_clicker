# Global Clicker — frontend

Vue 3 + Vite + Tailwind CSS 4.

## Run locally

```bash
npm install
npm run dev
```

The frontend connects to `ws://localhost:8080/ws` by default. To use another
backend, copy `.env.example` to `.env` and change `VITE_WEBSOCKET_URL`.

The backend sends click counts and device state to the UI. Clicking a country
in the UI sends a `CLICK` message back to the backend.

## Other scripts

```bash
npm run build    # production build → dist/
npm run preview  # preview the production build
```
