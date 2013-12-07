#!/bin/sh
mvn -e clean release:prepare && mvn -e release:perform