# Multi-stage build for React Vite Frontend
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci || npm install

COPY . .
RUN npm run build

# Nginx production runtime stage
FROM nginx:1.25-alpine

COPY --from=builder /app/dist /usr/share/nginx/html

# Copy our template to a custom location (NOT /etc/nginx/templates/ to prevent nginx's buggy default envsubst from running)
COPY nginx.conf.template /etc/nginx/conf.d/default.conf.template

# Create custom entrypoint that safely replaces __BACKEND_URL__ without touching nginx variables ($uri, $host, etc.)
RUN printf '#!/bin/sh\n\
set -e\n\
TARGET="${BACKEND_HOST:-backend:8080}"\n\
case "$TARGET" in\n\
  *:*) ;;\n\
  *) TARGET="${TARGET}:8080" ;;\n\
esac\n\
case "$TARGET" in\n\
  http://*|https://*) ;;\n\
  *) TARGET="http://${TARGET}" ;;\n\
esac\n\
echo "Configuring Nginx proxy to: $TARGET"\n\
sed "s|__BACKEND_URL__|$TARGET|g" /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf\n\
exec nginx -g "daemon off;"\n' > /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
