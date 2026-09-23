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

# Render injeta PORT; localmente usa 8080
# A conexao com o banco (Supabase) vem das variaveis DB_URL, DB_USERNAME e DB_PASSWORD;
# nao ha arquivos extras (certificados/chaves) para copiar para a imagem.
ENV PORT=8080

# Porta padrão local (Render ignora EXPOSE e usa a PORT)
EXPOSE 8080

# sh -c para expandir ${PORT}; exec faz o Java receber os sinais de parada do container
ENTRYPOINT ["sh", "-c", "exec java -XX:+ExitOnOutOfMemoryError -Dserver.port=${PORT} -jar /opt/app/app.jar"]
