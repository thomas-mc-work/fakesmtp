FROM openjdk:7-jre-alpine

RUN mkdir /output
VOLUME /output

EXPOSE 25

COPY target/fakesmtp-2.1-SNAPSHOT-jar-with-dependencies.jar /

# setting the bind address and port prevents accidential misconfigurations
ENTRYPOINT ["java", "-jar", "/fakesmtp-2.1-SNAPSHOT-jar-with-dependencies.jar", "-b", "0.0.0.0", "-p", "25"]
CMD ["-o", "/output"]
