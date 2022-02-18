FROM ubuntu:latest

RUN apt-get update -yq \
  && apt-get -yq install openjdk-11-jdk wget \
  && wget https://github.com/astrovan14/gmecord/releases/download/v1.5/io.banditoz.gmecord-all.jar

EXPOSE 4567

COPY Config.json .

CMD ["java", "-jar", "io.banditoz.gmecord-all.jar"]
