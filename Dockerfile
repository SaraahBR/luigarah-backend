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

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /opt/app/wallet \
 && apt-get update && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*

# USA O ARQUIVO REAL
COPY entrypoint.sh /opt/app/entrypoint.sh
RUN chmod +x /opt/app/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/opt/app/entrypoint.sh"]
