#!/usr/bin/env bash
set -euo pipefail

echo "[entrypoint] starting..."

# 1) Descompacta o wallet se existir como Secret File do Render
#    DICA: no Render, o nome do Secret File define o caminho:
#    - Se o secret chama 'wallet.zip'  -> /etc/secrets/wallet.zip
#    - Se o secret chama 'ORACLE_WALLET_ZIP' -> /etc/secrets/ORACLE_WALLET_ZIP
WALLET_ZIP_PATH="${WALLET_ZIP_PATH:-/etc/secrets/wallet.zip}"

if [ -f "$WALLET_ZIP_PATH" ]; then
  echo "[entrypoint] found wallet zip at: $WALLET_ZIP_PATH"
  rm -rf /opt/app/wallet/*
  unzip -oq "$WALLET_ZIP_PATH" -d /opt/app/wallet
else
  echo "[entrypoint] no wallet zip at $WALLET_ZIP_PATH (ok if you usa B64 ou já bakeou a pasta)"
fi

# 2) Garante TNS_ADMIN (onde o driver vai procurar tnsnames.ora / sqlnet.ora)
export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"
echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

# 3) Diagnóstico: lista conteúdo da pasta do wallet
if [ -d "$TNS_ADMIN" ]; then
  echo "[entrypoint] listing $TNS_ADMIN:"
  ls -la "$TNS_ADMIN" || true
else
  echo "[entrypoint][WARN] wallet dir not found: $TNS_ADMIN"
fi

# 4) Fail-fast se não tiver tnsnames.ora
if [ ! -f "$TNS_ADMIN/tnsnames.ora" ]; then
  echo "[entrypoint][ERROR] tnsnames.ora not found in $TNS_ADMIN"
  echo "                  ensure your wallet .zip contém: tnsnames.ora, sqlnet.ora, cwallet.sso, ewallet.p12 etc."
  exit 1
fi

# 5) Porta
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=$PORT"

# 6) Sobe a app
echo "[entrypoint] launching app..."
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar
