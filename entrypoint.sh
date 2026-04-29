#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[ENTRYPOINT FINAL LIMPO - ORACLE WALLET]"
echo "########################################"

echo "[entrypoint] iniciando..."

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
  echo "[DEBUG] Ajustando estrutura do wallet"
  mv "$inner_dir/"* "$WALLET_DIR"/
  rmdir "$inner_dir" || true
fi

# ===============================
# FORÇA SQLNET CORRETO
# ===============================
echo "[DEBUG] Corrigindo sqlnet.ora"

cat > "$WALLET_DIR/sqlnet.ora" <<EOF
WALLET_LOCATION = (SOURCE = (METHOD = FILE) (METHOD_DATA = (DIRECTORY=$WALLET_DIR)))
SSL_SERVER_DN_MATCH = yes
EOF

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

for f in tnsnames.ora sqlnet.ora cwallet.sso ewallet.p12; do
  [ -f "$WALLET_DIR/$f" ] || {
    echo "[ERRO FATAL] $f ausente"
    exit 1
  }
done

# ===============================
# VALIDAÇÃO REAL DO WALLET
# ===============================
echo "========== VALIDANDO WALLET =========="

if command -v strings >/dev/null 2>&1; then
  strings "$WALLET_DIR/cwallet.sso" | head -n 5 || true
else
  echo "[WARN] comando 'strings' não disponível no container"
fi

echo "========== SQLNET =========="
cat "$WALLET_DIR/sqlnet.ora"

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
# TESTE SSL REAL (CRÍTICO)
# ===============================
echo "========== TESTE SSL =========="

if command -v openssl >/dev/null 2>&1; then
  openssl s_client \
    -connect adb.sa-saopaulo-1.oraclecloud.com:1522 \
    -servername adb.sa-saopaulo-1.oraclecloud.com \
    </dev/null 2>/dev/null | openssl x509 -noout -issuer -subject || true
else
  echo "[WARN] openssl não disponível"
fi

# ===============================
# JAVA OPTIONS (CORRETO)
# ===============================
JAVA_OPTS="\
-Doracle.net.tns_admin=$TNS_ADMIN \
-Doracle.net.ssl_server_dn_match=true \
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
