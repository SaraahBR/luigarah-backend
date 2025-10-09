#!/usr/bin/env bash
set -euo pipefail

# Se o Render tiver um Secret File em /secrets/wallet.zip, descompacta
if [ -f "/secrets/wallet.zip" ]; then
  echo "[entrypoint] Found /secrets/wallet.zip, unpacking to /opt/app/wallet..."
  rm -rf /opt/app/wallet/*
  unzip -oq /secrets/wallet.zip -d /opt/app/wallet
fi

# Garante TNS_ADMIN
export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"
echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

# Porta exposta pelo Render
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=$PORT"

# Sobe a aplicação
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar
