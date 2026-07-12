# Modulo 44 - Frontend Dockerfile (multi-stage).
# Compila el proyecto Angular v22 y sirve el dist/ con nginx.
# Requiere que el desarrollador haya generado antes el proyecto Angular
# (ver frontend/README.md — `ng new`).

FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Angular v22 emite el build en dist/<project-name>/browser por defecto.
# Ajustar el path si tu proyecto usa otro nombre.
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine
# Copia los estaticos generados por Angular al directorio que sirve nginx.
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
# nginx.conf con proxy_pass /api al backend (evita CORS en prod).
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
