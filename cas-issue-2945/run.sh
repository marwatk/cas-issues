#!/bin/bash

mvn clean package && java -Dlog4j.configurationFile=etc/cas/config/log4j2.xml -Dcas.standalone.config.file=etc/cas/config/cas.properties -jar target/cas.war --logging.config=file:etc/cas/config/log4j2.xml
