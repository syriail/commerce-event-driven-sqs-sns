
FROM eclipse-temurin:17-jdk AS build

ENV GRADLE_OPTS: '-Dorg.gradle.jvmargs="-Xmx1000 -XX:+HeapDumpOnOutOfMemoryError" -Dorg.gradle.daemon=false -Dorg.gradle.workers.max=1 -Dorg.gradle.parallel=false'

WORKDIR /app

COPY . .

RUN ./gradlew --full-stacktrace clean assemble

FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar
CMD ["java", "-XX:MaxRAMPercentage=50.0", "-jar", "app.jar"]