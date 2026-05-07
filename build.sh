#!/bin/bash
set -e

build_maven_project() {
  local dir="$1"
  echo "==="
  echo "Сборка проекта в $dir..."

  if [ -d "$dir" ]; then
    pushd "$dir" > /dev/null

    mvn clean package -DskipTests

    echo "Сборка проекта $dir успешна"

    popd > /dev/null
  else
    echo "Директория $dir не найдена!"
    exit 1
  fi
}

build_maven_project "backend"

echo "==="
echo "Создание Docker образов"

 docker buildx build \
   --platform linux/amd64,linux/arm64 \
   -t khuzzyatov/miroro_backend:latest \
   --push ./backend
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t khuzzyatov/miroro_frontend:latest \
  --push ./frontend

echo "==="
echo "Отправка Docker образов"

docker push khuzzyatov/miroro_backend
docker push khuzzyatov/miroro_frontend

echo "==="
echo "Запуск Docker Compose"

docker-compose up -d