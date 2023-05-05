#!/bin/bash

REPOSITORY=/home/ec2-user/app/karrot/temp/build/libs
TEMP_REPOSITORY=/home/ec2-user/app/karrot

echo "> 현재 구동중인 PID확인"

CURRENT_PID=$(pgrep -fl karrot | awk '{print $1}')

echo "현재 구동중인 PID : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
	echo "> 현재 구동중인 애플리케이션 없음"
else
	echo "> kill -15 $CURRENT_PID"
	kill -15 $CURRENT_PID
	sleep 8
fi

echo "> 8080 포트 사용가능한지 확인 시작"

for RETRY_COUNT in {1..10}
do
  if [ -z "$(sudo lsof -i :8080)" ]; then
      echo "> 8080 포트가 비어있습니다. 다음 단계로 넘어갑니다."
      break
  else
      echo "> 8080 포트는 현재 사용불가능합니다.. 3초 후 다시 확인합니다."
      sleep 3
  fi
done

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
