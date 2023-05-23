#!/bin/bash

REPO_PATH=~/jwp-shopping-order
BRANCH=test
PORT=8080

if [ ! -d "${REPO_PATH}" ]; then
    echo "[INFO] 배포를 실행할 레포지토리가 존재하지 않아 클론을 진행합니다..."
    git clone https://github.com/woo-chang/jwp-shopping-order.git
fi

cd "${REPO_PATH}"
git fetch
echo "[INFO] ${BRANCH} 브랜치로 체크아웃하고 브랜치 최신 상태를 반영합니다..."
git checkout ${BRANCH}
git pull origin ${BRANCH}

EXISTING_PID=$(lsof -t -i :${PORT})
if [ -n "${EXISTING_PID}" ]; then
    echo "[INFO] ${PORT}포트에 실행 중인 프로세스가 존재합니다. 해당 프로세스를 종료합니다..."
    kill -15 ${EXISTING_PID}
    sleep 5
fi

echo "[INFO] 이전 빌드 결과물을 삭제하고 빌드, 패키징을 수행하여 JAR 파일을 생성합니다..."
./gradlew clean bootJar

echo "[INFO] 애플리케이션을 실행합니다..."
nohup java -jar build/libs/jwp-shopping-order.jar &
