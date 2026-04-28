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

# Instala unzip
RUN mkdir -p /opt/app/wallet \
 && apt-get update && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*

# ===== ENTRYPOINT CORRIGIDO =====
RUN printf '%s\n' \
'#!/usr/bin/env bash' \
'set -euo pipefail' \
'' \
'echo "[entrypoint] iniciando..."' \
'' \
'WALLET_SRC=""' \
'' \
'# Wallet via base64 (Render)' \
'if [ "${ORACLE_WALLET_ZIP_B64-}" != "" ]; then' \
'  echo "[entrypoint] Decodificando wallet base64..."' \
'  printf "%s" "$ORACLE_WALLET_ZIP_B64" | base64 -d > /opt/app/wallet.zip' \
'  WALLET_SRC="/opt/app/wallet.zip"' \
'fi' \
'' \
'# Fallback local' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/etc/secrets/wallet.zip" ]; then WALLET_SRC="/etc/secrets/wallet.zip"; fi' \
'if [ -z "${WALLET_SRC}" ] && [ -f "/secrets/wallet.zip" ]; then WALLET_SRC="/secrets/wallet.zip"; fi' \
'' \
'if [ -n "${WALLET_SRC}" ]; then' \
'  echo "[entrypoint] Extraindo wallet..."' \
'  rm -rf /opt/app/wallet/*' \
'  unzip -oq "${WALLET_SRC}" -d /opt/app/wallet' \
'' \
'  inner_dir=$(find /opt/app/wallet -mindepth 1 -maxdepth 1 -type d | head -n1 || true)' \
'  if [ -n "${inner_dir}" ] && [ -f "${inner_dir}/tnsnames.ora" ]; then' \
'    echo "[entrypoint] Ajustando estrutura do wallet..."' \
'    shopt -s dotglob nullglob' \
'    mv "${inner_dir}/"* /opt/app/wallet/ || true' \
'    rmdir "${inner_dir}" || true' \
'    shopt -u dotglob nullglob' \
'  fi' \
'else' \
'  echo "[entrypoint] ERRO: wallet não encontrado"' \
'fi' \
'' \
'# Variáveis' \
'export TNS_ADMIN="${TNS_ADMIN:-/opt/app/wallet}"' \
'export PORT="${PORT:-8080}"' \
'' \
'echo "[entrypoint] TNS_ADMIN=${TNS_ADMIN}"' \
'echo "[entrypoint] PORT=${PORT}"' \
'' \
'# Debug' \
'ls -la /opt/app/wallet || true' \
'' \
'# Validação do truststore (usa senha da ENV)' \
'if [ -f "${TNS_ADMIN}/truststore.jks" ]; then' \
'  echo "[entrypoint] Validando truststore..."' \
'  keytool -list -keystore "${TNS_ADMIN}/truststore.jks" -storepass "${TRUSTSTORE_PASSWORD}" > /dev/null 2>&1 && echo "[entrypoint] truststore OK" || echo "[entrypoint] truststore inválido"' \
'else' \
'  echo "[entrypoint] ERRO: truststore não encontrado"' \
'fi' \
'' \
'echo "[entrypoint] iniciando Java..."' \
'' \
'exec java \
-XX:+ExitOnOutOfMemoryError \
-Dserver.port="${PORT}" \
-Doracle.net.tns_admin="${TNS_ADMIN}" \
-Djavax.net.ssl.trustStore="${TNS_ADMIN}/truststore.jks" \
-Djavax.net.ssl.trustStorePassword="${TRUSTSTORE_PASSWORD}" \
-jar /opt/app/app.jar' \
> /opt/app/entrypoint.sh \
&& chmod +x /opt/app/entrypoint.sh

# Porta
EXPOSE 8080

# EntryPoint
ENTRYPOINT ["/opt/app/entrypoint.sh"]
