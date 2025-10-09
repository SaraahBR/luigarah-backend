#!/usr/bin/env bash
set -euo pipefail

echo "[entrypoint] iniciando..."

# Destino do wallet
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

# 1) Preferir secret em Base64 (variável ORACLE_WALLET_ZIP_B64)
if [ "${ORACLE_WALLET_ZIP_B64:-}" != "" ]; then
  echo "[entrypoint] Detectei ORACLE_WALLET_ZIP_B64; decodificando ZIP..."
  # Decodifica para um arquivo temporário
  echo -n "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip
  rm -rf "${WALLET_DIR:?}/"*
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"
  rm -f /tmp/wallet.zip
else
  # 2) Fallback: Secret File (binário) — /etc/secrets/wallet.zip ou /secrets/wallet.zip
  WALLET_SRC=""
  if [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi
  if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi

  if [ -n "${WALLET_SRC}" ]; then
    echo "[entrypoint] Encontrado wallet em ${WALLET_SRC}, descompactando para ${WALLET_DIR}..."
    rm -rf "${WALLET_DIR:?}/"*
    unzip -oq "${WALLET_SRC}" -d "$WALLET_DIR"
  else
    echo "[entrypoint] Nenhum wallet encontrado (nem ORACLE_WALLET_ZIP_B64, nem wallet.zip)."
  fi
fi

# Caso o ZIP tenha vindo com pasta raiz, mover conteúdo para a raiz do WALLET_DIR
inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)
if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then
  echo "[entrypoint] Detectado diretório interno (${inner_dir}), movendo conteúdo para ${WALLET_DIR}..."
  shopt -s dotglob nullglob
  mv "${inner_dir}/"* "$WALLET_DIR"/ || true
  rmdir "${inner_dir}" || true
  shopt -u dotglob nullglob
fi

# Exporta TNS_ADMIN
export TNS_ADMIN="${TNS_ADMIN:-$WALLET_DIR}"
echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"

# Porta (Render injeta PORT)
export PORT="${PORT:-8080}"
echo "[entrypoint] PORT=${PORT}"

# Debug útil
echo "[entrypoint] Conteúdo de ${WALLET_DIR}:"
ls -la "$WALLET_DIR" || true
if [ -f "${WALLET_DIR}/tnsnames.ora" ]; then
  echo "[entrypoint] tnsnames.ora:"
  sed -e "s/.*/    &/g" "${WALLET_DIR}/tnsnames.ora" || true
else
  echo "[entrypoint] tnsnames.ora NÃO encontrado em ${WALLET_DIR} (verifique o wallet)."
fi

echo "[entrypoint] iniciando Java..."
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar
