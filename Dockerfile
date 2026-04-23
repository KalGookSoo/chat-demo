FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
RUN chmod +x gradlew
RUN ./gradlew dependencies

COPY src src
RUN ./gradlew build -x test
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Create data directory for H2 database
RUN mkdir -p /app/data

EXPOSE 3001
ENTRYPOINT ["java","-cp","app:app/lib/*","kr.me.seesaw.ChatDemoApplication"]