#!/bin/bash

WDIR="/home/tomcat/sso-cas"
SERVICE="cas"


sed -i 's/test-cas-ng.properties/cas.properties/g' src/main/webapp/WEB-INF/spring-configuration/propertyFileConfigurer.xml

rm -rf target/*
mvn clean install package
echo "[INFO] suppression du context $SERVICE"

ssh tomcat@cas1-ng "rm -rf $WDIR/webapps/$SERVICE"
ssh tomcat@cas1-ng "rm -rf $WDIR/webapps/ROOT"
ssh tomcat@cas1-ng "rm -rf $WDIR/webapps/${SERVICE}.war"

ssh tomcat@cas2-ng "rm -rf $WDIR/webapps/$SERVICE"
ssh tomcat@cas2-ng "rm -rf $WDIR/webapps/ROOT"
ssh tomcat@cas2-ng "rm -rf $WDIR/webapps/${SERVICE}.war"
echo "[INFO] copie du war sur cas1-ng et cas2-ng ${SERVICE}"

scp ./target/${SERVICE}.war tomcat@cas1-ng:$WDIR/webapps/
scp ./target/${SERVICE}.war tomcat@cas2-ng:$WDIR/webapps/

