#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[DEBUG] ENTRYPOINT VERSION FINAL 10.0 CLEAN ORACLE WALLET"
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

  echo "[DEBUG] Tamanho BASE64: ${#ORACLE_WALLET_ZIP_B64}"

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  echo "[DEBUG] wallet.zip:"
  ls -lh /tmp/wallet.zip

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
# DEBUG WALLET
# ===============================
echo "========== WALLET =========="
ls -lah "$WALLET_DIR"

echo "========== VALIDACAO =========="
for f in tnsnames.ora sqlnet.ora cwallet.sso; do
  [ -f "$WALLET_DIR/$f" ] && echo "[OK] $f encontrado" || {
    echo "[ERRO] $f NÃO encontrado"
    exit 1
  }
done

echo "========== TNSNAMES =========="
cat "$WALLET_DIR/tnsnames.ora" || true

echo "========== SQLNET =========="
cat "$WALLET_DIR/sqlnet.ora" || true

echo "[entrypoint] Wallet válido ✔"

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
# JAVA OPTIONS (CORRETO)
# ===============================
JAVA_OPTS=""

JAVA_OPTS="$JAVA_OPTS -Doracle.net.tns_admin=$TNS_ADMIN"

echo "========== JAVA_OPTS =========="
echo "$JAVA_OPTS"

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
