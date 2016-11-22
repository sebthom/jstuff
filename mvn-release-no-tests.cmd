@echo off

call mvn %MVN_OPTS% -e release:prepare -Darguments="%MVN_OPTS% -DskipTests" && call mvn %MVN_OPTS% -e release:perform -Darguments="%MVN_OPTS% -DskipTests"