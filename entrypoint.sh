#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT"
echo "########################################"

echo "[entrypoint] iniciando..."

# ===============================
# CONFIG
# ===============================
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

echo "[DEBUG] WALLET_DIR=${WALLET_DIR}"

# ===============================
# WALLET BASE64
# ===============================
if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Wallet via BASE64 detectado"

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  echo "[DEBUG] Validando ZIP..."
  unzip -t /tmp/wallet.zip || {
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
  echo "[DEBUG] Ajustando estrutura do wallet"

  shopt -s dotglob nullglob
  mv "$inner_dir/"* "$WALLET_DIR"/
  rmdir "$inner_dir" || true
  shopt -u dotglob nullglob
fi

# ===============================
# VARIÁVEIS
# ===============================
export TNS_ADMIN="$WALLET_DIR"
export PORT="${PORT:-8080}"

echo "[DEBUG] TNS_ADMIN=${TNS_ADMIN}"
echo "[DEBUG] PORT=${PORT}"

# ===============================
# VALIDAÇÃO
# ===============================
echo "========== WALLET =========="
ls -lah "$WALLET_DIR"

for f in tnsnames.ora sqlnet.ora cwallet.sso ewallet.p12 truststore.jks; do
  [ -f "$WALLET_DIR/$f" ] || {
    echo "[ERRO FATAL] $f ausente"
    exit 1
  }
done

echo "[entrypoint] Wallet válido ✔"

# ===============================
# DEBUG REDE
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
JAVA_OPTS=""

# Wallet
JAVA_OPTS="$JAVA_OPTS -Doracle.net.tns_admin=$TNS_ADMIN"
JAVA_OPTS="$JAVA_OPTS -Doracle.net.wallet_location=(SOURCE=(METHOD=FILE)(METHOD_DATA=(DIRECTORY=$TNS_ADMIN)))"
JAVA_OPTS="$JAVA_OPTS -Doracle.net.ssl_server_dn_match=true"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=$TNS_ADMIN/truststore.jks"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=changeit"

echo "========== JAVA_OPTS =========="
echo "$JAVA_OPTS"

# ===============================
# IMPORT TRUSTSTORE NO JAVA
# ===============================
echo "[DEBUG] Importando certificados no cacerts..."

keytool -importkeystore \
  -srckeystore "$TNS_ADMIN/truststore.jks" \
  -srcstorepass changeit \
  -destkeystore "$JAVA_HOME/lib/security/cacerts" \
  -deststorepass changeit \
  -noprompt || true

# ===============================
# START
# ===============================
echo "[entrypoint] iniciando Java..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="$PORT" \
  $JAVA_OPTS \
  ${DEBUG_SSL:+-Djavax.net.debug=ssl,handshake} \
  -jar /opt/app/app.jar
