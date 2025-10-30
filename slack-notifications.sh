#!/bin/bash

# Script para enviar notificaciones a Slack
# Variables de configuración
SLACK_WEBHOOK_URL="$SLACK_WEBHOOK_URL"SLACK_CHANNEL="#jenkins-notifications"
SLACK_USERNAME="JenkinsBot"

# Función para enviar mensaje a Slack
send_slack_message() {
    local message="$1"
    local color="$2"
    
    # Crear payload JSON
    local payload="{
        \"channel\": \"${SLACK_CHANNEL}\",
        \"username\": \"${SLACK_USERNAME}\",
        \"text\": \"${message}\",
        \"icon_emoji\": \":jenkins:\"
    }"
    
    # Enviar mensaje a Slack
    curl -X POST \
        --data-urlencode "payload=${payload}" \
        "${SLACK_WEBHOOK_URL}" \
        -H "Content-Type: application/json"
}

# Verificar si se pasó un mensaje como parámetro
if [ $# -eq 0 ]; then
    echo "Uso: $0 <mensaje> [color]"
    exit 1
fi

# Obtener mensaje y color (opcional)
MESSAGE="$1"
COLOR="${2:-good}"

# Enviar notificación
send_slack_message "${MESSAGE}" "${COLOR}"

echo "Notificación enviada a Slack: ${MESSAGE}"