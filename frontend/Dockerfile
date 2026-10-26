# Multi-stage build for React Vite Frontend
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci || npm install

COPY . .
RUN npm run build

# Nginx production runtime stage
FROM nginx:1.25-alpine

# Install envsubst (comes with gettext package)
RUN apk add --no-cache gettext

COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx template (uses ${BACKEND_HOST} placeholder)
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

EXPOSE 80

# nginx official image auto-processes /etc/nginx/templates/*.template via envsubst
CMD ["nginx", "-g", "daemon off;"]
