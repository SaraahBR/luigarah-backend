#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT VERSION FINAL 9.0"
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
# Ajuste estrutura
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
# DEBUG WALLET
# ===============================
echo "========== WALLET =========="
ls -lah "$WALLET_DIR"

echo "========== VALIDACAO ARQUIVOS =========="
for f in tnsnames.ora sqlnet.ora cwallet.sso ewallet.p12 truststore.jks; do
  [ -f "$WALLET_DIR/$f" ] && echo "[OK] $f encontrado" || echo "[ERRO] $f NÃO encontrado"
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
# DEBUG TRUSTSTORE (CORRIGIDO)
# ===============================
echo "========== TRUSTSTORE =========="

TRUSTSTORE_PATH="${TNS_ADMIN}/truststore.jks"
RAW_PASSWORD="${TRUSTSTORE_PASSWORD:-changeit}"

TRUSTSTORE_PASSWORD_CLEAN=$(echo -n "$RAW_PASSWORD" | tr -d '\r\n')

echo "[DEBUG] PASSWORD LIMPA:"
echo "$TRUSTSTORE_PASSWORD_CLEAN"

echo "[DEBUG] TAMANHO SENHA: ${#TRUSTSTORE_PASSWORD_CLEAN}"

echo "[DEBUG] EXISTE?"
ls -lh "$TRUSTSTORE_PATH"

echo "[DEBUG] HASH:"
sha256sum "$TRUSTSTORE_PATH" || true

echo "[DEBUG] DETECTANDO TIPO DO KEYSTORE..."
file "$TRUSTSTORE_PATH" || true

echo "[DEBUG] TESTE KEYTOOL (PKCS12)..."
keytool -list \
  -storetype PKCS12 \
  -keystore "$TRUSTSTORE_PATH" \
  -storepass "$TRUSTSTORE_PASSWORD_CLEAN" || {
    echo "[ERRO] Falha ao ler truststore como PKCS12"
    exit 1
}

echo "[OK] Truststore PKCS12 válido"

# ===============================
# DEBUG REDE
# ===============================
echo "========== DNS =========="
getent hosts adb.sa-saopaulo-1.oraclecloud.com || true

echo "========== TESTE PORTA 1522 =========="
timeout 5 bash -c "</dev/tcp/adb.sa-saopaulo-1.oraclecloud.com/1522" \
  && echo "[OK] Porta acessível" \
  || echo "[WARN] Porta inacessível"

# ===============================
# JAVA OPTIONS
# ===============================
JAVA_OPTS=""

JAVA_OPTS="$JAVA_OPTS -Doracle.net.tns_admin=$TNS_ADMIN"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD_CLEAN"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStoreType=PKCS12"

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
