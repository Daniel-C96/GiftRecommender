FROM eclipse-temurin:21-jdk as build
COPY . /app
WORKDIR /app
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests
RUN mv -f target/*.jar app.jar

# Execution
FROM eclipse-temurin:21-jre
ENV PORT=8080
COPY --from=build /app/app.jar .
RUN useradd runtime
USER runtime
ENTRYPOINT [ "java", "-Dserver.port=${PORT}", "-jar", "app.jar" ]
