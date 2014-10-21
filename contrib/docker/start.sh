#!/bin/bash

OPTS="-Xms1024m -Xmx1024m -XX:-OmitStackTraceInFastThrow"

export PATH=/config/media-server/:$PATH

if [ "$DATABASE" != "" ]; then
    OPTS=" $OPTS -DDATABASE=\"$DATABASE\""
fi

cd buddycloud-media-server
java $OPTS -jar target/buddycloud-media-server-jar-with-dependencies.jar