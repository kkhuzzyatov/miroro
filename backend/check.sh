#!/bin/bash
# check.sh - Запуск проверок проекта

set -e

TOTAL_STEPS=2

echo "1/${TOTAL_STEPS} Применение форматирования кода (Spotless)"
mvn spotless:apply

echo
echo "2/${TOTAL_STEPS} Запуск API тестов"

./run-api-tests.sh

echo
echo "Все проверки успешно завершены!"