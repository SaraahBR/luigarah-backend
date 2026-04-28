#!/usr/bin/env bash
set -euo pipefail

echo "[entrypoint] iniciando..."

# Destino do wallet
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

# 1) Preferir secret em Base64 (variável ORACLE_WALLET_ZIP_B64)
if [ "${ORACLE_WALLET_ZIP_B64:-}" != "" ]; then
  echo "[entrypoint] Detectei ORACLE_WALLET_ZIP_B64; decodificando ZIP..."
  echo -n "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip
  rm -rf "${WALLET_DIR:?}/"*
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"
  rm -f /tmp/wallet.zip
else
  # 2) Fallback: Secret File
  WALLET_SRC=""
  if [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi
  if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi

  if [ -n "${WALLET_SRC}" ]; then
    echo "[entrypoint] Encontrado wallet em ${WALLET_SRC}, descompactando..."
    rm -rf "${WALLET_DIR:?}/"*
    unzip -oq "${WALLET_SRC}" -d "$WALLET_DIR"
  else
    echo "[entrypoint] ERRO: Nenhum wallet encontrado"
  fi
fi

# Ajuste caso venha com pasta interna
inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)
if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then
  echo "[entrypoint] Pasta interna detectada (${inner_dir}), ajustando..."
  shopt -s dotglob nullglob
  mv "${inner_dir}/"* "$WALLET_DIR"/ || true
  rmdir "${inner_dir}" || true
  shopt -u dotglob nullglob
fi

# Exporta TNS_ADMIN
export TNS_ADMIN="${TNS_ADMIN:-$WALLET_DIR}"
echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"

# Porta
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=${PORT}"

# Debug do wallet
echo "[entrypoint] Conteúdo do wallet:"
ls -la "$WALLET_DIR" || true

# Validação do truststore (CRÍTICO)
if [ -f "${WALLET_DIR}/truststore.jks" ]; then
  echo "[entrypoint] Validando truststore..."
  keytool -list -keystore "${WALLET_DIR}/truststore.jks" -storepass changeit > /dev/null 2>&1 \
    && echo "[entrypoint] truststore OK" \
    || echo "[entrypoint] ERRO: truststore inválido ou corrompido"
else
  echo "[entrypoint] ERRO: truststore.jks não encontrado"
fi

echo "[entrypoint] iniciando Java..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="${PORT}" \
  -Doracle.net.tns_admin="${TNS_ADMIN}" \
  -Djavax.net.ssl.trustStore="${TNS_ADMIN}/truststore.jks" \
  -Djavax.net.ssl.trustStorePassword="${TRUSTSTORE_PASSWORD}" \
  -jar /opt/app/app.jar
