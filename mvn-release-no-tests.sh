#!/bin/sh
mvn -e clean release:prepare -Darguments="-DskipTests" && mvn -e release:perform -Darguments="-DskipTests"