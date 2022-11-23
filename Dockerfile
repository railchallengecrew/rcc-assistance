FROM gradle:jdk17 AS BUILD_ARTIFACT
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src
RUN gradle build || return 0
COPY . .
RUN gradle clean build

FROM amazoncorretto:17
ENV ARTIFACT=RCCAssistance-1.0-all.jar
WORKDIR /app
COPY --from=BUILD_ARTIFACT /app/build/libs/$ARTIFACT .
ENTRYPOINT exec java -jar ${ARTIFACT}