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

# Prepara diretório do wallet e unzip
RUN mkdir -p /opt/app/wallet \
 && apt-get update && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*

# Script de entrada robusto
# - Procura wallet.zip em /etc/secrets (Render) e /secrets (local)
# - Descompacta
# - Se veio com pasta aninhada, move os arquivos (tnsnames.ora etc.) para /opt/app/wallet
# - Exporta TNS_ADMIN e PORT
# - Loga estrutura para debug
RUN printf '%s\n' \
'#!/usr/bin/env bash' \
'set -euo pipefail' \
'' \
'echo "[entrypoint] iniciando..."' \
'WALLET_SRC="";' \
'if [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi' \
'' \
'if [ -n "${WALLET_SRC}" ]; then' \
'  echo "[entrypoint] Encontrado wallet em ${WALLET_SRC}, descompactando para /opt/app/wallet..."' \
'  rm -rf /opt/app/wallet/*' \
'  unzip -oq "${WALLET_SRC}" -d /opt/app/wallet' \
'  # Se o ZIP tiver uma pasta raiz, “achata” para /opt/app/wallet' \
'  inner_dir=$(find /opt/app/wallet -mindepth 1 -maxdepth 1 -type d | head -n1 || true)' \
'  if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then' \
'    echo "[entrypoint] Detected nested dir (${inner_dir}), moving contents up..."' \
'    shopt -s dotglob nullglob' \
'    mv "${inner_dir}/"* /opt/app/wallet/ || true' \
'    rmdir "${inner_dir}" || true' \
'    shopt -u dotglob nullglob' \
'  fi' \
'else' \
'  echo "[entrypoint] wallet.zip não encontrado (ok se usar EZCONNECT)."' \
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
'  echo "[entrypoint] tnsnames.ora NÃO encontrado em /opt/app/wallet (verifique o ZIP e o TNS alias)."' \
'fi' \
'' \
'echo "[entrypoint] iniciando Java..."' \
'exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -jar /opt/app/app.jar' \
> /opt/app/entrypoint.sh \
 && chmod +x /opt/app/entrypoint.sh

# Porta padrão local (Render ignora EXPOSE e usa a PORT)
EXPOSE 8080

# Use ENTRYPOINT para garantir que o script rode sempre
ENTRYPOINT ["/opt/app/entrypoint.sh"]
