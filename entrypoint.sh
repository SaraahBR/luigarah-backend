#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT VERSION FINAL 6.0"
echo "########################################"

echo "[entrypoint] iniciando..."

# ===============================
# Configuração inicial
# ===============================
WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

echo "[DEBUG] WALLET_DIR=${WALLET_DIR}"

# ===============================
# 1) Wallet via Base64 (Render)
# ===============================
if [ "${ORACLE_WALLET_ZIP_B64:-}" != "" ]; then
  echo "[entrypoint] Wallet via BASE64 detectado"

  echo "[DEBUG] Tamanho BASE64: ${#ORACLE_WALLET_ZIP_B64}"

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  echo "[DEBUG] Arquivo /tmp/wallet.zip criado"
  ls -lh /tmp/wallet.zip

  echo "[DEBUG] Validando ZIP..."
  unzip -t /tmp/wallet.zip || {
    echo "[ERRO] ZIP inválido"
    exit 1
  }

  echo "[DEBUG] Limpando diretório wallet..."
  rm -rf "${WALLET_DIR:?}/"*

  echo "[DEBUG] Extraindo wallet..."
  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"

  rm -f /tmp/wallet.zip

else
  echo "[entrypoint] Wallet via arquivo"

  WALLET_SRC=""

  [ -f "/etc/secrets/wallet.zip" ] && WALLET_SRC="/etc/secrets/wallet.zip"
  [ -z "$WALLET_SRC" ] && [ -f "/secrets/wallet.zip" ] && WALLET_SRC="/secrets/wallet.zip"

  if [ -z "$WALLET_SRC" ]; then
    echo "[ERRO] Nenhum wallet encontrado"
    exit 1
  fi

  echo "[DEBUG] WALLET_SRC=${WALLET_SRC}"
  ls -lh "$WALLET_SRC"

  rm -rf "${WALLET_DIR:?}/"*
  unzip -oq "$WALLET_SRC" -d "$WALLET_DIR"
fi

# ===============================
# Ajuste de estrutura
# ===============================
echo "[DEBUG] Verificando estrutura interna..."

inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)

if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then
  echo "[DEBUG] Wallet dentro de subpasta → corrigindo estrutura"

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
# Debug completo do wallet
# ===============================
echo "========== DEBUG WALLET =========="

ls -la "$WALLET_DIR"

echo "[DEBUG] Arquivos esperados:"
for f in tnsnames.ora sqlnet.ora cwallet.sso ewallet.p12 truststore.jks; do
  if [ -f "$WALLET_DIR/$f" ]; then
    echo "✔ $f encontrado"
  else
    echo "✘ $f NÃO encontrado"
  fi
done

echo "[DEBUG] Primeiras linhas do tnsnames.ora:"
head -n 20 "$WALLET_DIR/tnsnames.ora" || true

echo "[DEBUG] Conteúdo do sqlnet.ora:"
cat "$WALLET_DIR/sqlnet.ora" || true

echo "================================="

# ===============================
# Verificação crítica
# ===============================
[ -f "$WALLET_DIR/tnsnames.ora" ] || { echo "[ERRO] tnsnames.ora ausente"; exit 1; }
[ -f "$WALLET_DIR/sqlnet.ora" ] || { echo "[ERRO] sqlnet.ora ausente"; exit 1; }
[ -f "$WALLET_DIR/truststore.jks" ] || { echo "[ERRO] truststore.jks ausente"; exit 1; }

echo "[entrypoint] Wallet válido ✔"

# ===============================
# DEBUG SSL avançado
# ===============================
if [ "${DEBUG_SSL:-false}" = "true" ]; then
  echo "========== DEBUG SSL =========="

  echo "[DEBUG] TRUSTSTORE PATH:"
  echo "${TNS_ADMIN}/truststore.jks"

  echo "[DEBUG] TRUSTSTORE EXISTS?"
  ls -lh "${TNS_ADMIN}/truststore.jks"

  echo "[DEBUG] TRUSTSTORE HASH:"
  sha256sum "${TNS_ADMIN}/truststore.jks" || true

  echo "[DEBUG] TRUSTSTORE PASSWORD LENGTH:"
  echo "${#TRUSTSTORE_PASSWORD:-0}"

  echo "================================"
fi

# ===============================
# Inicialização do Java (CORRIGIDO)
# ===============================
echo "[entrypoint] iniciando Java com SSL correto..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="${PORT}" \
  -Doracle.net.tns_admin="${TNS_ADMIN}" \
  -Djavax.net.ssl.trustStore="${TNS_ADMIN}/truststore.jks" \
  -Djavax.net.ssl.trustStorePassword="${TRUSTSTORE_PASSWORD}" \
  -Djavax.net.ssl.trustStoreType=JKS \
  ${DEBUG_SSL:+-Djavax.net.debug=ssl,handshake} \
  -jar /opt/app/app.jar
