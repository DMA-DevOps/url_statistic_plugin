#!/bin/bash -e

if [ ! -f "${1}" ]; then
	echo "Usage: pass hadoop log file as argument"
	exit 1
fi

mvn clean install > /dev/null

cat ${1} |grep fetching| awk '{print $7}'| \
	java -cp target/url_statistic_plugin-0.0.1-SNAPSHOT.jar StatisticGenerator \
	|head -n 1000
