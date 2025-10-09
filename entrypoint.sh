#!/usr/bin/env bash
set -euo pipefail

echo "[entrypoint] starting..."

mkdir -p /opt/app/wallet

if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Found ORACLE_WALLET_ZIP_B64, decoding..."
  # grava e descompacta
  echo -n "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip
  unzip -oq /tmp/wallet.zip -d /opt/app/wallet
  echo "[entrypoint] Wallet unpacked to /opt/app/wallet"
else
  echo "[entrypoint][WARN] ORACLE_WALLET_ZIP_B64 not set; wallet may be missing."
fi

# checagens de sanidade
echo "[entrypoint] Listing wallet dir:"
ls -la /opt/app/wallet || true

if [ ! -f /opt/app/wallet/tnsnames.ora ]; then
  echo "[entrypoint][ERROR] tnsnames.ora was NOT found in /opt/app/wallet"
  echo "[entrypoint] Make sure ORACLE_WALLET_ZIP_B64 contains a valid wallet.zip with tnsnames.ora/sqlnet.ora/cwallet.sso"
  exit 20
fi

export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"
echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=$PORT"

echo "[entrypoint] starting app..."
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar