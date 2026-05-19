FROM eclipse-temurin:17
WORKDIR /app

# Since Render is already inside backend/Todo, just copy everything here
COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -B

EXPOSE 8080
# Use the exact jar name we verified earlier
CMD ["java", "-jar", "target/Todo-0.0.1-SNAPSHOT.jar"]