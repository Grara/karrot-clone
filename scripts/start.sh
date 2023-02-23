#!/bin/bash

REPOSITORY=/home/ec2-user/app/karrot/temp

echo "> 현재 구동중인 PID확인"

CURRENT_PID=$(pgrep -fl karrot | awk '{print $1}')

echo "현재 구동중인 PID : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
	echo "> 현재 구동중인 애플리케이션 없음"
else
	echo "> kill -15 $CURRENT_PID"
	kill -15 $CURRENT_PID
	sleep 5
fi

echo "> 새 어플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

rm $REPOSITORY/nohup.out

nohup java -jar -Dspring.config.location=/home/ec2-user/app/karrot/temp/application-temp.yml \
-Djasypt.password=ekdrmszmffhs \
-Dspring.profiles.active=temp $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
