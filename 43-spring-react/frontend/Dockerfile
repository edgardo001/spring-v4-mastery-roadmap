# Modulo 43 - Frontend Dockerfile (multi-stage).
# Compila el proyecto React con Vite y sirve el dist/ con nginx.
# Requiere que el desarrollador haya generado antes el proyecto Vite
# (ver frontend/README.md).

FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
