#!/bin/bash

# Stop on error
set -e;
# set -x;
skipTests="-DskipTests=true"

#Always make debug available, but don't wait unless -d option specified
debugFlags="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8083"

while getopts ":dst" opt
do
	case $opt in
		d) 
			debugMode=true
			echo "Option set to start API in debug mode."
			debugFlags="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8083 -Djava.compiler=NONE" 
		;;
		p)
			apiPort=$OPTARG
			echo "Option set run API on port ${apiPort}"
		;;
		s)
			skipBuild=true
			echo "Option set to skip build"
		;;
		t)
			skipTests=""
			echo "Running tests as part of build"
		;;
		help|\?)
			echo -e "Usage: [-d]  [-s]"
			echo -e "\t d - debug. Starts the API in debug mode, which an IDE can attach to on port 8001"
			#echo -e "\t p <port> - Starts the API on a specific port (default 8080)"
			echo -e "\t s - skip. Skips the build"
			echo -e "\t t - include unit tests"
			exit 0
		;;
	esac
done

#tomcatPID=`ps aux | grep '[c]atalina' | awk '{print $2}'`
#if [ -n "${tomcatPID}" ]
#then
#	sudo kill -9 $tomcatPID
#fi
#rm /tc/logs/catalina.out || true

if [ -z "${skipBuild}" ]
then
	mvn clean install ${skipTests}
fi

#export JPDA_ADDRESS=8000
#export JPDA_TRANSPORT=dt_socket

#/tc/bin/catalina.sh jpda start
#tail -f /tc/logs/catalina.out

#Port appears set at 8080, possibly by ./src/main/deb/config.properties 
#More likely resources/application.yml
#Can't get this self hosted option to listen for debug connections


memOptions="-Xms2g -Xmx5g"
set -x;
java ${memOptions} ${debugFlags} -jar target/ihtsdo-mlds.war \
--spring.config.location=local.config.properties 
