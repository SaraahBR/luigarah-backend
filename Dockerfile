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

# Fuso horário
ENV TZ=America/Sao_Paulo

# Pasta de trabalho
WORKDIR /opt/app

# Copia o jar final
COPY --from=build /app/target/*.jar app.jar

# Instala unzip e cria diretório do wallet
RUN mkdir -p /opt/app/wallet \
 && apt-get update && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*

# Script de entrada:
# - Decodifica wallet base64 (Render)
# - Fallback para secrets locais
# - Ajusta estrutura do wallet
# - Configura variáveis de ambiente
# - FORÇA uso do truststore (resolve PKIX)
RUN printf '%s\n' \
'#!/usr/bin/env bash' \
'set -euo pipefail' \
'' \
'echo "[entrypoint] iniciando..."' \
'WALLET_SRC=""' \
'' \
'# 1) Wallet via variável base64' \
'if [ "${ORACLE_WALLET_ZIP_B64-}" != "" ]; then' \
'  echo "[entrypoint] Detectei ORACLE_WALLET_ZIP_B64; decodificando ZIP..."' \
'  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /opt/app/wallet.zip' \
'  WALLET_SRC="/opt/app/wallet.zip"' \
'fi' \
'' \
'# 2) Fallbacks (dev/local)' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi' \
'' \
'if [ -n "${WALLET_SRC}" ]; then' \
'  echo "[entrypoint] Usando wallet: ${WALLET_SRC} → /opt/app/wallet..."' \
'  rm -rf /opt/app/wallet/*' \
'  unzip -oq "${WALLET_SRC}" -d /opt/app/wallet' \
'' \
'  # Ajuste caso venha com pasta interna' \
'  inner_dir=$(find /opt/app/wallet -mindepth 1 -maxdepth 1 -type d | head -n1 || true)' \
'  if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then' \
'    echo "[entrypoint] Pasta interna detectada (${inner_dir}); movendo conteúdo..."' \
'    shopt -s dotglob nullglob' \
'    mv "${inner_dir}/"* /opt/app/wallet/ || true' \
'    rmdir "${inner_dir}" || true' \
'    shopt -u dotglob nullglob' \
'  fi' \
'else' \
'  echo "[entrypoint] Nenhum wallet encontrado (ERRO se usar TNS)."' \
'fi' \
'' \
'# Variáveis de ambiente' \
'export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"' \
'export PORT="${PORT:-8080}"' \
'echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"' \
'echo "[entrypoint] PORT=${PORT}"' \
'' \
'# Debug do wallet' \
'echo "[entrypoint] Conteúdo de /opt/app/wallet:"' \
'ls -la /opt/app/wallet || true' \
'' \
'if [ -f "/opt/app/wallet/tnsnames.ora" ]; then' \
'  echo "[entrypoint] tnsnames.ora OK"' \
'else' \
'  echo "[entrypoint] ERRO: tnsnames.ora não encontrado"' \
'fi' \
'' \
'echo "[entrypoint] iniciando Java..."' \
'' \
'# CORREÇÃO CRÍTICA: comando java sem quebra inválida' \
'exec java -XX:+ExitOnOutOfMemoryError -Dserver.port="${PORT}" -Doracle.net.tns_admin=${TNS_ADMIN} -Djavax.net.ssl.trustStore=${TNS_ADMIN}/truststore.jks -Djavax.net.ssl.trustStorePassword=changeit -jar /opt/app/app.jar' \
> /opt/app/entrypoint.sh \
 && chmod +x /opt/app/entrypoint.sh

# Porta padrão
EXPOSE 8080

# EntryPoint
ENTRYPOINT ["/opt/app/entrypoint.sh"]
