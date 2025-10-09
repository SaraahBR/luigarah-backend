# ===== Build =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -U -DskipTests dependency:go-offline
COPY . .
RUN mvn -q -e -DskipTests package

# ===== Runtime =====
FROM eclipse-temurin:21-jre
ENV TZ=America/Sao_Paulo
WORKDIR /opt/app

# Copia o jar final
COPY --from=build /app/target/*.jar app.jar

# Diretório onde o wallet será montado/descompactado
# No Render usaremos um "Secret File" (wallet.zip) em /secrets/wallet.zip
RUN mkdir -p /opt/app/wallet
# Descompacta se o arquivo existir (útil localmente também)
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Script de entrada: se houver /secrets/wallet.zip, descompacta em /opt/app/wallet
# e exporta TNS_ADMIN apontando para lá.
RUN printf '#!/usr/bin/env bash\n\
set -euo pipefail\n\
if [ -f "/etc/secrets/wallet.zip" ]; then\n\
  rm -rf /opt/app/wallet/*\n\
  unzip -oq /etc/secrets/wallet.zip -d /opt/app/wallet\n\
fi\n\
export TNS_ADMIN=${TNS_ADMIN:-/opt/app/wallet}\n\
echo "TNS_ADMIN=$TNS_ADMIN"\n\
exec java -XX:+ExitOnOutOfMemoryError -Dserver.port=${PORT:-8080} -jar /opt/app/app.jar\n' > /opt/app/entrypoint.sh \
 && chmod +x /opt/app/entrypoint.sh

EXPOSE 8080
CMD ["/opt/app/entrypoint.sh"]
