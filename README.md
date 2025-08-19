# ubermensch-maker-backend

## Build and Run

`start.sh` 스크립트를 실행하면 PostgreSQL과 Spring Boot 서버가 도커로 함께 실행됩니다.

```bash
$ cd ubermensch-maker-backend
$ chmod +x scripts/start.sh
$ ./scripts/start.sh
```

실행 후 docker ps 결과:

```bash
CONTAINER ID   IMAGE                          COMMAND                  CREATED          STATUS        PORTS                    NAMES
8502afc88038   ubermensch-maker-backend-api   "java -jar /app.jar"     35 minutes ago   Up 1 second   0.0.0.0:8080->8080/tcp   api
2ca585d716bb   postgres:latest                "docker-entrypoint.s…"   35 minutes ago   Up 1 second   0.0.0.0:5432->5432/tcp   db
```

PostgreSQL 확장 프로그램:
PostgreSQL에 UUID v7 확장이 포함되어 있어 데이터베이스 스키마나 확장 프로그램 변경 시 볼륨을 삭제해야 합니다.

```bash
docker-compose down -v
docker-compose up --build
```

## Code Style

이 프로젝트는 Java 코드 스타일 통일을 위해 [google-java-format](https://github.com/google/google-java-format)을 사용합니다.

### 방법 1: Gradle 명령어 (권장)

Homebrew로 설치한 google-java-format 사용:

```bash
# google-java-format 설치 (최초 1회)
brew install google-java-format

# 모든 Java 파일 포맷팅
./gradlew format
```

### 방법 2: VS Code 자동 포맷팅 (저장 시)

파일 저장할 때마다 자동으로 포맷팅하려면:

1. Homebrew로 google-java-format 설치 (macOS):
   ```bash
   brew install google-java-format
   ```

2. 실행 파일 경로 확인:
   ```bash
   which google-java-format
   ```

3. VS Code 확장 프로그램 `google-java-format` (작성자: ilkka) 설치

4. `.vscode/settings.json`에 설정 추가:
   ```json
   {
     "[java]": {
       "editor.defaultFormatter": "ilkka.google-java-format"
     },
     "google-java-format.executable-path": "/opt/homebrew/bin/google-java-format",
     "editor.formatOnSave": true
   }
   ```
