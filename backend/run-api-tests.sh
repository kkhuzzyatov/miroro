#!/usr/bin/env bash

set -euo pipefail

HTTP_FILE="src/test/http/api.http"

echo "========================================"
echo "Запуск API тестов"
echo "Файл: ${HTTP_FILE}"
echo "========================================"

if ! command -v httpyac >/dev/null 2>&1; then
    echo "ОШИБКА: httpyac не установлен"
    echo
    echo "Установка:"
    echo "npm install -g httpyac"
    exit 1
fi

echo
echo "Проверка доступности backend..."

if ! curl -s http://localhost:8080 >/dev/null 2>&1; then
    echo "ПРЕДУПРЕЖДЕНИЕ: Backend может быть не запущен на localhost:8080"
    echo
fi

echo
echo "Выполнение тестов..."
echo

httpyac "${HTTP_FILE}" --all

echo
echo "========================================"
echo "Все API тесты успешно пройдены"
echo "========================================"