FROM openjdk:7-jre-alpine

RUN mkdir /output
VOLUME /output

EXPOSE 25

COPY target/fakesmtp-*-jar-with-dependencies.jar /fakesmtp.jar

# setting the bind address and port prevents accidential misconfigurations
ENTRYPOINT ["java", "-jar", "/fakesmtp.jar", "-b", "0.0.0.0", "-p", "25"]
CMD ["-o", "/output"]
