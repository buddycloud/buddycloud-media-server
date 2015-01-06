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
RUN cd buddycloud-media-server && git checkout master
RUN cd buddycloud-media-server && mvn package
ADD contrib/docker/start.sh /data/
RUN chmod +x start.sh
CMD ./start.sh
