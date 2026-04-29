#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[ENTRYPOINT FINAL CORRETO]"
echo "########################################"

echo "[entrypoint] iniciando..."

# ===============================
# CONFIG
# ===============================
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

# ===============================
# WALLET BASE64
# ===============================
if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Wallet via BASE64 detectado"

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  unzip -t /tmp/wallet.zip >/dev/null || {
    echo "[ERRO] ZIP inválido"
    exit 1
  }

  rm -rf "${WALLET_DIR:?}/"*
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"
  rm -f /tmp/wallet.zip
else
  echo "[ERRO] ORACLE_WALLET_ZIP_B64 não definido"
  exit 1
fi

# ===============================
# AJUSTE SUBPASTA
# ===============================
inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)

if [ -n "$inner_dir" ] && [ -f "$inner_dir/tnsnames.ora" ]; then
  mv "$inner_dir/"* "$WALLET_DIR"/
  rmdir "$inner_dir" || true
fi

# ===============================
# VARIÁVEIS
# ===============================
export TNS_ADMIN="$WALLET_DIR"
export PORT="${PORT:-8080}"

echo "[DEBUG] TNS_ADMIN=$TNS_ADMIN"
echo "[DEBUG] PORT=$PORT"

# ===============================
# VALIDAÇÃO
# ===============================
echo "========== WALLET =========="
ls -lah "$WALLET_DIR"

for f in tnsnames.ora sqlnet.ora cwallet.sso truststore.jks; do
  [ -f "$WALLET_DIR/$f" ] || {
    echo "[ERRO FATAL] $f ausente"
    exit 1
  }
done

echo "========== SQLNET =========="
cat "$TNS_ADMIN/sqlnet.ora"

# ===============================
# REDE
# ===============================
echo "========== DNS =========="
getent hosts adb.sa-saopaulo-1.oraclecloud.com || true

echo "========== PORTA =========="
timeout 5 bash -c "</dev/tcp/adb.sa-saopaulo-1.oraclecloud.com/1522" \
  && echo "[OK] Porta 1522 acessível" \
  || echo "[WARN] Porta 1522 inacessível"

# ===============================
# JAVA OPTIONS
# ===============================
JAVA_OPTS="\
-Doracle.net.tns_admin=$TNS_ADMIN \
-Doracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=$TNS_ADMIN))) \
-Doracle.net.ssl_server_dn_match=true \
-Djavax.net.ssl.trustStore=$TNS_ADMIN/truststore.jks \
-Djavax.net.ssl.trustStorePassword=changeit \
"

echo "========== JAVA_OPTS =========="
echo "$JAVA_OPTS"

# ===============================
# DEBUG SSL
# ===============================
if [ "${DEBUG_SSL:-false}" = "true" ]; then
  JAVA_OPTS="$JAVA_OPTS -Djavax.net.debug=ssl,handshake"
  echo "[DEBUG] SSL DEBUG ATIVADO"
fi

# ===============================
# START
# ===============================
echo "[entrypoint] iniciando Java..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="$PORT" \
  $JAVA_OPTS \
  -jar /opt/app/app.jar
