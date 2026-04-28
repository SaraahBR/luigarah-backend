#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT VERSION FINAL 4.0"
echo "########################################"

echo "[entrypoint] iniciando..."

# ===============================
# Configuração inicial
# ===============================
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

# ===============================
# 1) Wallet via Base64 (Render)
# ===============================
if [ "${ORACLE_WALLET_ZIP_B64:-}" != "" ]; then
  echo "[entrypoint] Detectei ORACLE_WALLET_ZIP_B64; decodificando..."

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  echo "[DEBUG] Validando ZIP..."
  unzip -t /tmp/wallet.zip > /dev/null 2>&1 || {
    echo "[entrypoint] ERRO: wallet.zip corrompido"
    exit 1
  }

  rm -rf "${WALLET_DIR:?}/"*
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"
  rm -f /tmp/wallet.zip
else
  WALLET_SRC=""

  if [ -f "/etc/secrets/wallet.zip" ]; then
    WALLET_SRC="/etc/secrets/wallet.zip"
  fi

  if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then
    WALLET_SRC="/secrets/wallet.zip"
  fi

  if [ -n "${WALLET_SRC}" ]; then
    echo "[entrypoint] Extraindo wallet de ${WALLET_SRC}..."

    rm -rf "${WALLET_DIR:?}/"*
    unzip -oq "${WALLET_SRC}" -d "$WALLET_DIR"
  else
    echo "[entrypoint] ERRO: Nenhum wallet encontrado"
    exit 1
  fi
fi

# ===============================
# Ajuste de estrutura
# ===============================
inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)

if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then
  echo "[entrypoint] Ajustando estrutura do wallet..."

  shopt -s dotglob nullglob
  mv "${inner_dir}/"* "$WALLET_DIR"/ || true
  rmdir "${inner_dir}" || true
  shopt -u dotglob nullglob
fi

# ===============================
# Variáveis
# ===============================
export TNS_ADMIN="${TNS_ADMIN:-$WALLET_DIR}"
export PORT="${PORT:-8080}"

TRUSTSTORE_PASSWORD_SAFE=$(printf "%s" "${TRUSTSTORE_PASSWORD:-}")

echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"
echo "[entrypoint] PORT=${PORT}"

# ===============================
# Debug básico
# ===============================
echo "[entrypoint] Conteúdo do wallet:"
ls -la "$WALLET_DIR" || true

# ===============================
# Validação do truststore
# ===============================
if [ ! -f "${WALLET_DIR}/truststore.jks" ]; then
  echo "[entrypoint] ERRO: truststore.jks não encontrado"
  exit 1
fi

if [ -z "${TRUSTSTORE_PASSWORD_SAFE}" ]; then
  echo "[entrypoint] ERRO: TRUSTSTORE_PASSWORD não definida"
  exit 1
fi

echo "[entrypoint] Validando truststore..."

if keytool -list \
  -keystore "${WALLET_DIR}/truststore.jks" \
  -storetype JKS \
  -storepass "${TRUSTSTORE_PASSWORD_SAFE}" \
  > /dev/null 2>&1; then

  echo "[entrypoint] truststore OK ✔"

else
  echo "[entrypoint] ERRO: truststore inválido ou senha incorreta"

  echo "[DEBUG] Senha recebida (RAW): ${TRUSTSTORE_PASSWORD}"
  echo "[DEBUG] Senha sanitizada: ${TRUSTSTORE_PASSWORD_SAFE}"
  echo "[DEBUG] Tamanho da senha: ${#TRUSTSTORE_PASSWORD_SAFE}"

  exit 1
fi

# ===============================
# DEBUG COMPLETO
# ===============================
if [ "${DEBUG_SSL:-false}" = "true" ]; then
  echo "================ DEBUG SSL ================="

  echo "[DEBUG] TNS_ADMIN=${TNS_ADMIN}"
  echo "[DEBUG] TRUSTSTORE_PASSWORD=${TRUSTSTORE_PASSWORD_SAFE}"

  echo "[DEBUG] Arquivos no wallet:"
  ls -la "${TNS_ADMIN}"

  echo "[DEBUG] Conteúdo do tnsnames.ora:"
  cat "${TNS_ADMIN}/tnsnames.ora" || true

  echo "[DEBUG] Dump truststore:"
  keytool -list \
    -keystore "${TNS_ADMIN}/truststore.jks" \
    -storetype JKS \
    -storepass "${TRUSTSTORE_PASSWORD_SAFE}" || true

  echo "==========================================="
fi

# ===============================
# Inicialização do Java
# ===============================
echo "[entrypoint] iniciando Java..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="${PORT}" \
  -Doracle.net.tns_admin="${TNS_ADMIN}" \
  -Djavax.net.ssl.trustStore="${TNS_ADMIN}/truststore.jks" \
  -Djavax.net.ssl.trustStorePassword="${TRUSTSTORE_PASSWORD_SAFE}" \
  -Djavax.net.ssl.trustStoreType=JKS \
  ${DEBUG_SSL:+-Djavax.net.debug=ssl,handshake} \
  -jar /opt/app/app.jar
