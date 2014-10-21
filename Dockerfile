################################################################################
# Build a dockerfile for buddycloud-media-server
# Based on ubuntu
################################################################################

FROM dockerfile/java:openjdk-7-jdk

MAINTAINER Lloyd Watkin <lloyd@evilprofessor.co.uk>

RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y --no-install-recommends maven

RUN git clone https://github.com/buddycloud/buddycloud-media-server.git
RUN cd buddycloud-media-server && git checkout develop
RUN cd buddycloud-media-server && mvn package
ADD src/main/resources/log4j.properties /data/buddycloud-server-java/
ADD contrib/docker/start.sh /data/
RUN chmod +x start.sh
CMD ./start.sh