# Use Maven 3.9.5 with a compatible JDK to build the app
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Use OpenJDK 21 for runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/obi-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "obi-0.0.1-SNAPSHOT.jar"]
