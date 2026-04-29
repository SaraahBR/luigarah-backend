#!/usr/bin/env bash
set -euo pipefail

echo "########################################"
echo "[ENTRYPOINT ORACLE WALLET - LIMPO]"
echo "########################################"

WALLET_DIR="/opt/app/wallet"
mkdir -p "$WALLET_DIR"

# ===============================
# WALLET BASE64
# ===============================
if [ -n "${ORACLE_WALLET_ZIP_B64:-}" ]; then
  echo "[entrypoint] Extraindo wallet..."

  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /tmp/wallet.zip

  unzip -oq /tmp/wallet.zip -d "$WALLET_DIR"
  rm -f /tmp/wallet.zip
else
  echo "[ERRO] ORACLE_WALLET_ZIP_B64 não definido"
  exit 1
fi

# ===============================
# AJUSTE SUBPASTA (caso venha zipado dentro)
# ===============================
inner_dir=$(find "$WALLET_DIR" -mindepth 1 -maxdepth 1 -type d | head -n1 || true)

if [ -n "$inner_dir" ] && [ -f "$inner_dir/tnsnames.ora" ]; then
  echo "[entrypoint] Ajustando estrutura do wallet"
  mv "$inner_dir/"* "$WALLET_DIR"/
  rmdir "$inner_dir" || true
fi

# ===============================
# VARIÁVEIS ESSENCIAIS
# ===============================
export TNS_ADMIN="$WALLET_DIR"
export PORT="${PORT:-8080}"

echo "[entrypoint] TNS_ADMIN=$TNS_ADMIN"

# ===============================
# VALIDAÇÃO MÍNIMA
# ===============================
for f in tnsnames.ora cwallet.sso; do
  [ -f "$WALLET_DIR/$f" ] || {
    echo "[ERRO FATAL] $f ausente no wallet"
    exit 1
  }
done

# ===============================
# JAVA OPTIONS (APENAS O NECESSÁRIO)
# ===============================
JAVA_OPTS="\
-Doracle.net.tns_admin=$TNS_ADMIN \
-Doracle.net.ssl_server_dn_match=true \
"

# DEBUG opcional
if [ "${DEBUG_SSL:-false}" = "true" ]; then
  JAVA_OPTS="$JAVA_OPTS -Djavax.net.debug=ssl,handshake"
fi

# ===============================
# START
# ===============================
echo "[entrypoint] iniciando aplicação..."

exec java \
  -XX:+ExitOnOutOfMemoryError \
  -Dserver.port="$PORT" \
  $JAVA_OPTS \
  -jar /opt/app/app.jar
