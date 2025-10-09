#!/usr/bin/env bash
set -euo pipefail

# ===============================
# Oracle Wallet (Render Secret)
# ===============================
# O Render monta secret files em /etc/secrets/<nome-do-arquivo>
# Então, se houver wallet.zip lá, ele é extraído para /opt/app/wallet
if [ -f "/etc/secrets/wallet.zip" ]; then
  echo "[entrypoint] Found /etc/secrets/wallet.zip, unpacking to /opt/app/wallet..."
  rm -rf /opt/app/wallet/*
  unzip -oq /etc/secrets/wallet.zip -d /opt/app/wallet
else
  echo "[entrypoint] Wallet file not found at /etc/secrets/wallet.zip"
fi

# ===============================
# Configurações do Oracle
# ===============================
export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"
echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

# ===============================
# Porta da aplicação (Render define automaticamente)
# ===============================
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=$PORT"

# ===============================
# Inicializa aplicação
# ===============================
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar
