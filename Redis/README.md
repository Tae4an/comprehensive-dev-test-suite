# Redis Docker 설치 및 설정 가이드

## 목차
1. [사전 준비사항](#1-사전-준비사항)
2. [디렉토리 구조 설정](#2-디렉토리-구조-설정)
3. [Redis 설정](#3-redis-설정)
4. [Docker Compose 설정](#4-docker-compose-설정)
5. [Redis 실행 및 테스트](#5-redis-실행-및-테스트)
6. [유용한 Redis 명령어](#6-유용한-redis-명령어)
7. [문제 해결](#7-문제-해결)

## 1. 사전 준비사항
- Docker 설치
- Docker Compose 설치

## 2. 디렉토리 구조 설정
```bash
# Redis 프로젝트 디렉토리 생성
mkdir -p ~/redis
cd ~/redis

# Redis 설정 파일 디렉토리 생성
mkdir -p redis-config
```

최종 디렉토리 구조:
```
redis/
├── docker-compose.yml
└── redis-config/
    └── redis.conf
```

## 3. Redis 설정
`redis-config/redis.conf` 파일 생성:

```bash
# 네트워크 설정
bind 0.0.0.0
port 6379
protected-mode yes

# 보안 설정
requirepass your_password

# 메모리 설정
maxmemory 2gb
maxmemory-policy allkeys-lru

# 지속성 설정
appendonly yes
appendfilename "appendonly.aof"

# 로깅 설정
loglevel notice
logfile ""

# 일반 설정
daemonize no
supervised no
pidfile /var/run/redis_6379.pid
```

## 4. Docker Compose 설정
`docker-compose.yml` 파일 생성:

```yaml
version: '3'
services:
  redis:
    image: redis:latest
    container_name: redis-server
    ports:
      - "6379:6379"
    volumes:
      - ./redis-config/redis.conf:/usr/local/etc/redis/redis.conf
      - redis-data:/data
    command: redis-server /usr/local/etc/redis/redis.conf
    restart: unless-stopped
    networks:
      - redis-network

volumes:
  redis-data:
    driver: local

networks:
  redis-network:
    driver: bridge
```

## 5. Redis 실행 및 테스트

### Redis 실행
```bash
# Docker Compose로 Redis 실행
docker-compose up -d

# 컨테이너 상태 확인
docker ps
```

### 연결 테스트
```bash
# Redis CLI 접속
docker exec -it redis-server redis-cli

# 인증
auth your_password

# 연결 테스트
ping
# 예상 응답: PONG

# 간단한 데이터 저장/조회 테스트
set test "Hello Redis"
get test
# 예상 응답: "Hello Redis"
```

## 6. 유용한 Redis 명령어

### Docker 관련 명령어
```bash
# 컨테이너 시작
docker-compose start

# 컨테이너 중지
docker-compose stop

# 컨테이너 재시작
docker-compose restart

# 로그 확인
docker-compose logs

# 컨테이너 제거
docker-compose down
```

### Redis CLI 명령어
```bash
# Redis CLI 접속
docker exec -it redis-server redis-cli

# 기본 명령어들
auth your_password   # 인증
info                # Redis 서버 정보 조회
monitor            # Redis 명령어 모니터링
config get *       # 모든 설정 조회
```

## 7. 문제 해결

### 연결 오류 발생 시
```bash
# Redis 컨테이너 로그 확인
docker logs redis-server

# Redis 설정 파일 확인
cat redis-config/redis.conf

# Redis 컨테이너 재시작
docker-compose restart
```

### 데이터 초기화가 필요한 경우
```bash
# 컨테이너와 볼륨 제거
docker-compose down -v

# 새로 시작
docker-compose up -d
```

## 주의사항
1. 프로덕션 환경에서는 반드시 강력한 비밀번호를 사용하세요.
2. Redis 데이터는 `redis-data` 볼륨에 저장됩니다.
3. 메모리 설정은 서버 환경에 맞게 조정하세요.

## 참고자료
- [Redis 공식 문서](https://redis.io/documentation)
- [Docker Redis 공식 이미지](https://hub.docker.com/_/redis)
- [Redis 보안 가이드](https://redis.io/topics/security)
