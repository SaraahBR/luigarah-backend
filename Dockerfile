# ===== Build =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa dependências em cache
COPY pom.xml .
RUN mvn -q -e -U -DskipTests dependency:go-offline

# Copia o código e empacota
COPY . .
RUN mvn -q -e -DskipTests package

# ===== Runtime =====
FROM eclipse-temurin:21-jre

# Fuso horário (opcional)
ENV TZ=America/Sao_Paulo

# Pasta de trabalho
WORKDIR /opt/app

# Copia o jar final
COPY --from=build /app/target/*.jar app.jar

# Ferramentas necessárias
RUN mkdir -p /opt/app/wallet \
 && apt-get update && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*

# Script de entrada:
# - Se ORACLE_WALLET_ZIP_B64 estiver setada, decodifica e descompacta o wallet
# - (fallback) tenta /etc/secrets/wallet.zip e /secrets/wallet.zip, se existirem
# - Achata pasta aninhada do wallet, exporta TNS_ADMIN e sobe o app
RUN printf '%s\n' \
'#!/usr/bin/env bash' \
'set -euo pipefail' \
'' \
'echo "[entrypoint] iniciando..."' \
'WALLET_SRC=""' \
'' \
'# 1) Preferir variável base64 (segura no Render)' \
'if [ "${ORACLE_WALLET_ZIP_B64-}" != "" ]; then' \
'  echo "[entrypoint] Detectei ORACLE_WALLET_ZIP_B64; decodificando ZIP..."' \
'  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /opt/app/wallet.zip' \
'  WALLET_SRC="/opt/app/wallet.zip"' \
'fi' \
'' \
'# 2) Fallbacks (úteis em dev/local)' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi' \
'' \
'if [ -n "${WALLET_SRC}" ]; then' \
'  echo "[entrypoint] Usando wallet: ${WALLET_SRC} → /opt/app/wallet..."' \
'  rm -rf /opt/app/wallet/*' \
'  unzip -oq "${WALLET_SRC}" -d /opt/app/wallet' \
'  # Se o ZIP tiver pasta raiz, achata para /opt/app/wallet' \
'  inner_dir=$(find /opt/app/wallet -mindepth 1 -maxdepth 1 -type d | head -n1 || true)' \
'  if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then' \
'    echo "[entrypoint] Pasta interna detectada (${inner_dir}); movendo conteúdo..."' \
'    shopt -s dotglob nullglob' \
'    mv "${inner_dir}/"* /opt/app/wallet/ || true' \
'    rmdir "${inner_dir}" || true' \
'    shopt -u dotglob nullglob' \
'  fi' \
'else' \
'  echo "[entrypoint] Nenhum wallet informado (ok se usar EZCONNECT, mas para TNS é necessário o wallet)."' \
'fi' \
'' \
'export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"' \
'export PORT="${PORT:-8080}"' \
'echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"' \
'echo "[entrypoint] PORT=${PORT}"' \
'' \
'echo "[entrypoint] Conteúdo de /opt/app/wallet:"' \
'ls -la /opt/app/wallet || true' \
'if [ -f "/opt/app/wallet/tnsnames.ora" ]; then' \
'  echo "[entrypoint] tnsnames.ora:"' \
'  sed -e "s/.*/    &/g" /opt/app/wallet/tnsnames.ora || true' \
'else' \
'  echo "[entrypoint] tnsnames.ora NÃO encontrado (confira ORACLE_WALLET_ZIP_B64 e o ZIP)."' \
'fi' \
'' \
'echo "[entrypoint] iniciando Java..."' \
'exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar' \
> /opt/app/entrypoint.sh \
 && chmod +x /opt/app/entrypoint.sh

# Porta padrão local (Render ignora EXPOSE e usa a PORT)
EXPOSE 8080

# Garantir que o script rode sempre
ENTRYPOINT ["/opt/app/entrypoint.sh"]
