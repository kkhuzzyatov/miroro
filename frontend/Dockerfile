# Этап 1: сборка приложения
FROM node:20-alpine AS builder

WORKDIR /app

# Копируем зависимости
COPY package.json package-lock.json ./
RUN npm ci

# Копируем исходники
COPY . .

# Сборка
RUN npm run build


# Этап 2: production-сервер
FROM nginx:alpine

# Удаляем дефолтный конфиг
RUN rm -rf /etc/nginx/conf.d/default.conf

# Кастомный конфиг для SPA
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Копируем билд
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]