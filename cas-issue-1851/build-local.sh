#!/bin/bash
rm -rf target/*
mvn clean install package
rm -rf ../sso-cas/webapps/cas
rm -rf ../sso-cas/webapps/ROOT
rm -rf ../sso-cas/webapps/cas.war
cp ./target/cas.war ../sso-cas/webapps/
