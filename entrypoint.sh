#!/usr/bin/env bash
set -euo pipefail

echo "[entrypoint] starting..."

# ===============================
# 🧩 Oracle Wallet (2 formas)
# 1) Secret File: /etc/secrets/wallet.zip (Render converte em texto, pode quebrar)
# 2) ENV Base64 : ORACLE_WALLET_ZIP_B64
# ===============================
mkdir -p /opt/app/wallet

if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Found ORACLE_WALLET_ZIP_B64, decoding and unpacking..."
  echo -n "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip
  unzip -oq /tmp/wallet.zip -d /opt/app/wallet
  echo "[entrypoint] Wallet unpacked successfully from Base64."
elif [ -f "/etc/secrets/wallet.zip" ]; then
  echo "[entrypoint] Found /etc/secrets/wallet.zip (not recommended by Render), trying unzip..."
  unzip -oq /etc/secrets/wallet.zip -d /opt/app/wallet || echo "[WARN] unzip failed, likely text-encoded secret file."
else
  echo "[entrypoint][WARN] No wallet found (neither Base64 nor Secret File)."
fi

# ===============================
# Oracle config
# ===============================
export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"
echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

# ===============================
# Porta padrão
# ===============================
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=$PORT"

# ===============================
# Start app
# ===============================
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar
