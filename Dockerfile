FROM eclipse-temurin:21-jdk-alpine

# Impostazione directory di lavoro
WORKDIR /app

# Argomenti per caching e ottimizzazione
ARG WAR_FILE=target/*.war

# Copia del WAR dell'applicazione
COPY ${WAR_FILE} app.war

# Esposizione della porta (tipica per Spring Boot)
EXPOSE 8080

# Comando per avviare l'applicazione
ENTRYPOINT ["java", "-jar", "/app/app.war"]