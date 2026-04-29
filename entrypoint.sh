#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT VERSION FINAL 7.1"
echo "########################################"

echo "[entrypoint] iniciando..."

# ===============================
# Configuração inicial
# ===============================
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

echo "[DEBUG] WALLET_DIR=${WALLET_DIR}"

# ===============================
# 1) Wallet via Base64
# ===============================
if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Wallet via BASE64 detectado"

  echo "[DEBUG] Tamanho BASE64:"
  echo "${#ORACLE_WALLET_ZIP_B64}"

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  echo "[DEBUG] Arquivo wallet.zip:"
  ls -lh /tmp/wallet.zip

  echo "[DEBUG] Validando ZIP..."
  unzip -t /tmp/wallet.zip || {
    echo "[ERRO] ZIP inválido"
    exit 1
  }

  echo "[DEBUG] Limpando WALLET_DIR..."
  rm -rf "${WALLET_DIR:?}/"*

  echo "[DEBUG] Extraindo wallet..."
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"

  rm -f /tmp/wallet.zip

else
  echo "[ERRO] ORACLE_WALLET_ZIP_B64 não definido"
  exit 1
fi

# ===============================
# Ajuste de estrutura interna
# ===============================
echo "[DEBUG] Verificando estrutura interna..."

inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)

if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then
  echo "[DEBUG] Wallet em subpasta → ajustando"

  shopt -s dotglob nullglob
  mv "${inner_dir}/"* "$WALLET_DIR"/
  rmdir "${inner_dir}" || true
  shopt -u dotglob nullglob
fi

# ===============================
# Variáveis
# ===============================
export TNS_ADMIN="${TNS_ADMIN:-$WALLET_DIR}"
export PORT="${PORT:-8080}"

echo "[DEBUG] TNS_ADMIN=${TNS_ADMIN}"
echo "[DEBUG] PORT=${PORT}"

# ===============================
# DEBUG WALLET COMPLETO
# ===============================
echo "========== WALLET =========="
ls -lah "$WALLET_DIR"

echo "========== VALIDACAO ARQUIVOS =========="
for f in tnsnames.ora sqlnet.ora cwallet.sso ewallet.p12 truststore.jks; do
  if [ -f "$WALLET_DIR/$f" ]; then
    echo "[OK] $f encontrado"
  else
    echo "[ERRO] $f NÃO encontrado"
  fi
done

echo "========== TNSNAMES =========="
cat "$WALLET_DIR/tnsnames.ora" || true

echo "========== SQLNET =========="
cat "$WALLET_DIR/sqlnet.ora" || true

# ===============================
# VALIDAÇÃO CRÍTICA
# ===============================
[ -f "$WALLET_DIR/tnsnames.ora" ] || { echo "[ERRO FATAL] tnsnames.ora ausente"; exit 1; }
[ -f "$WALLET_DIR/sqlnet.ora" ] || { echo "[ERRO FATAL] sqlnet.ora ausente"; exit 1; }
[ -f "$WALLET_DIR/truststore.jks" ] || { echo "[ERRO FATAL] truststore.jks ausente"; exit 1; }

echo "[entrypoint] Wallet válido ✔"

# ===============================
# DEBUG TRUSTSTORE REAL
# ===============================
echo "========== TRUSTSTORE =========="

TRUSTSTORE_PATH="${TNS_ADMIN}/truststore.jks"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-changeit}"

echo "[DEBUG] PATH: $TRUSTSTORE_PATH"
echo "[DEBUG] PASSWORD (SIM, EXPOSTO): $TRUSTSTORE_PASSWORD"

echo "[DEBUG] EXISTE?"
ls -lh "$TRUSTSTORE_PATH"

echo "[DEBUG] HASH:"
sha256sum "$TRUSTSTORE_PATH" || true

echo "[DEBUG] TENTANDO LER TRUSTSTORE (keytool)..."
keytool -list -keystore "$TRUSTSTORE_PATH" -storepass "$TRUSTSTORE_PASSWORD" || {
  echo "[ERRO] Senha do truststore inválida ou arquivo corrompido"
  exit 1
}

# ===============================
# DEBUG REDE
# ===============================
echo "========== DNS =========="
getent hosts adb.sa-saopaulo-1.oraclecloud.com || true

# ===============================
# JAVA OPTIONS
# ===============================
JAVA_OPTS=""

JAVA_OPTS="$JAVA_OPTS -Doracle.net.tns_admin=$TNS_ADMIN"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStoreType=JKS"

echo "========== JAVA_OPTS =========="
echo "$JAVA_OPTS"

# ===============================
# START JAVA
# ===============================
echo "[entrypoint] iniciando Java..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="$PORT" \
  $JAVA_OPTS \
  ${DEBUG_SSL:+-Djavax.net.debug=ssl,handshake} \
  -jar /opt/app/app.jar
