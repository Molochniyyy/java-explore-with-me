FROM bellsoft/liberica-openjdk-alpine-musl:11
ENV TZ=Europe/Podgorica
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]