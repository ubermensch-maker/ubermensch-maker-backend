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

