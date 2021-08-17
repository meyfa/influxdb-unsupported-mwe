# maven
FROM maven:3-openjdk-16 as mvn
WORKDIR /app
COPY . ./
RUN mvn package

# jlink
FROM openjdk:16-alpine as jlink
WORKDIR /app

# java.sql is required for java.sql.Date used by InfluxDB
#   (and it automatically includes java.logging as well, also used by InfluxDB)

# jdk.unsupported is required by Gson (dependency of InfluxDB) for HTTP body deserialization
RUN jlink --compress=2 --add-modules java.sql --output jlink
#RUN jlink --compress=2 --add-modules java.sql,jdk.unsupported --output jlink

# production
FROM alpine:latest
WORKDIR /app
COPY --from=mvn /app/target/influxdb-unsupported-mwe-jar-with-dependencies.jar /app/influxdb-unsupported-mwe.jar
COPY --from=jlink /app/jlink /app/jlink
# add a script to wait for InfluxDB to come online before we run the app
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.9.0/wait /app/wait
RUN chmod +x /app/wait
CMD /app/wait && /app/jlink/bin/java -jar influxdb-unsupported-mwe.jar
