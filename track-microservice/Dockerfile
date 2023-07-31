FROM openjdk:17-alpine

LABEL authors="Denis Solovey"

WORKDIR /music-dashboard

ENV WAIT_VERSION 2.12.0
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/$WAIT_VERSION/wait /wait
RUN chmod +x /wait

COPY target/*.jar track-service.jar

COPY entrypoint.sh /entrypoint
RUN chmod +x /entrypoint

ENTRYPOINT ["/entrypoint"]