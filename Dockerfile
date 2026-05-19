FROM eclipse-temurin:17

WORKDIR /app

# Copy the specific backend folder contents to /app
COPY backend/Todo/ .

RUN chmod +x mvnw

# Use -B for cleaner logs as discussed earlier
RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080

# Use the specific jar name found in your previous logs
CMD ["java", "-jar", "target/Todo-0.0.1-SNAPSHOT.jar"]