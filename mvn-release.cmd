@echo off
REM to skip tests execute "mvn-release.cmd -DskipTests"
call mvn %MVN_OPTS% -e release:prepare -Darguments="%MVN_OPTS% %*" && call mvn %MVN_OPTS% -e release:perform -Darguments="%MVN_OPTS% %*"