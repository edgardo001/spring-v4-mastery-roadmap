# Frontend React 19 — Modulo 43

Este modulo NO trae el `package.json` ni `node_modules` generados. Se documenta
como crear el frontend con Vite y consumir la API del backend.

## Generar el proyecto con Vite

```bash
# Desde 43-spring-react/frontend/
npm create vite@latest . -- --template react-ts
npm install
```

## Consumir la API con Basic Auth (React 19)

Archivo `src/App.tsx`:

```tsx
import { useEffect, useState } from "react";

// El backend expone /api/messages en localhost:8080.
type Message = { id: number; text: string };

// Basic Auth: usuario:contraseña en base64.
const AUTH = "Basic " + btoa("demo:demo123");

export default function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [text, setText] = useState("");

  // GET publico (no requiere auth).
  const load = async () => {
    const res = await fetch("/api/messages");
    setMessages(await res.json());
  };

  // POST requiere Basic Auth.
  const send = async () => {
    await fetch("/api/messages", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": AUTH,
      },
      body: JSON.stringify({ text }),
    });
    setText("");
    load();
  };

  useEffect(() => { load(); }, []);

  return (
    <div style={{ padding: 20 }}>
      <h1>Modulo 43 — Spring + React</h1>
      <ul>{messages.map(m => <li key={m.id}>{m.text}</li>)}</ul>
      <input value={text} onChange={e => setText(e.target.value)} />
      <button onClick={send}>Enviar</button>
    </div>
  );
}
```

## Proxy de desarrollo (evita CORS)

`vite.config.ts`:

```ts
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": "http://localhost:8080",
    },
  },
});
```

Con el proxy, el frontend en `localhost:5173` envia peticiones a `/api/messages`
y Vite las redirige a `localhost:8080`. El navegador nunca ve una peticion
cross-origin y CORS no aplica.

## Build de produccion

```bash
npm run build       # genera dist/
```

En produccion, `docker-compose.yml` sirve el `dist/` con nginx (ver Dockerfile
en `frontend/Dockerfile` si decides producirlo).

## Antes vs Ahora

| Antes (JSP + Tomcat) | Ahora (React 19 + REST) |
|----------------------|-------------------------|
| HTML generado en el servidor por JSP | HTML/JS servido estatico, datos por fetch |
| Session del servidor con cookies JSESSIONID | JWT / Basic Auth stateless en headers |
| Recarga completa de pagina en cada accion | SPA: solo se actualiza el DOM afectado |
| Un solo despliegue (WAR con backend+front) | Dos despliegues: JAR backend + estatico nginx |
