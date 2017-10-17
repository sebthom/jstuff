#!/bin/bash
REM to skip tests execute "mvn-release.sh -DskipTests"
mvn -e clean release:prepare -Darguments="$@" && mvn -e release:perform -Darguments="$@"