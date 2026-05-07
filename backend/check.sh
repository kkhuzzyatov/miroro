#!/bin/bash
# check.sh - Запуск линтеров

set -e

TOTAL_STEPS=1

echo "1/${TOTAL_STEPS} Применение форматирования кода (Spotless)"
mvn spotless:apply

echo "Все литеры успешно завершены!"