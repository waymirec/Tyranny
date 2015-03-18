#!/bin/bash
java -Djava.util.logging.config.file=/opt/tyranny/authserver/conf/logging.properties -javaagent:/opt/tyranny/authserver/lib/tyranny-instrumentation.jar -jar tyranny-authserver-boot.jar
