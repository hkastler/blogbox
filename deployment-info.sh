#!/bin/sh
HOST=www.hkstlr.com
ADMIN="$1"
ADMIN_AUTH="$2"
WILDFLY_MANAGEMENT_URL=http://${ADMIN}:${ADMIN_AUTH}@${HOST}:9990
if [ "$PROJECTS" == "" ]; then
PROJECTS=blogbox-webapp,blogbox
fi
IFS=","
for PROJECT in $PROJECTS
do
PROJECT_HOME=$LOCAL_HOME/$PROJECT
WAR_NAME=${PROJECT}.war
echo "-> Deployment Info '$WAR_NAME' to '$WILDFLY_MANAGEMENT_URL'"
result=`curl -s --digest ${WILDFLY_MANAGEMENT_URL}/management --header "Content-Type: application/json" -d '{"operation":"read-attribute","name":"status","recursive":"true", "include-runtime":"true", "address":[{"deployment":"'"${WAR_NAME}"'"}], "json.pretty":0}'`
echo $result
done