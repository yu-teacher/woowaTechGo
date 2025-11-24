# WoowaGo

온라인 바둑 게임 서버 (게임 로직 + KataGo AI 연동)

## 기술 스택

- Java 21
- Spring Boot 3.5.7
- Spring WebSocket (STOMP)
- KataGo (오픈소스 바둑 AI)
- Lombok

## 주요 기능

### 게임 기능
- 19x19 바둑판 게임 로직
- 착수 검증 (자충수, 패 규칙)
- 따내기 처리
- 무르기 기능
- 멀티플레이어 지원 (참가자 2명 + 관전자)

### AI 기능 (KataGo 연동)
- 착수 추천 (bluespot)
- 형세 판단 (analysis)
- 계가 (score)

### 실시간 통신
- WebSocket 기반 게임 방 시스템
- 요청/응답 시스템 (게임 시작, 무르기, 계가)
- 자동 타임아웃 처리 (30초)

## 아키텍처

### 도메인 구조
```
domain/
├── game/           # 바둑 게임 로직
│   ├── Game       # 게임 흐름 관리
│   ├── Board      # 바둑판 상태
│   ├── Position   # 좌표
│   └── Stone      # 돌 (BLACK/WHITE/EMPTY)
├── move/          # 착수 관리
│   ├── Move       # 착수 기록
│   ├── MoveHistory
│   └── MoveValidator  # 착수 검증
├── capture/       # 따내기 로직
│   ├── CaptureHandler
│   └── StoneGroup    # 돌 그룹 분석
└── room/          # 게임 방 관리
    ├── GameRoom
    ├── Participants
    └── GameSettings  # 흑백 배정
```

### API 엔드포인트

**REST API** (싱글플레이어용)
- `POST /api/game/start` - 새 게임 시작
- `POST /api/game/move` - 착수
- `DELETE /api/game/move` - 무르기
- `GET /api/game` - 게임 상태 조회
- `GET /api/game/moves` - 착수 기록 조회

**KataGo API**
- `GET /api/katago/bluespots` - 착수 추천
- `GET /api/katago/score` - 계가
- `GET /api/katago/bluespots/{gameId}` - 게임별 착수 추천
- `GET /api/katago/score/{gameId}` - 게임별 계가

**WebSocket** (멀티플레이어용)
- `/ws-game` - WebSocket 연결
- `/app/game/join` - 방 입장
- `/app/game/move` - 착수
- `/app/game/start` - 게임 시작
- `/app/game/undo` - 무르기
- `/app/game/analysis` - 형세 판단
- `/app/game/score` - 계가
- `/app/game/request/*` - 요청 생성
- `/app/game/respond/*` - 요청 응답
- `/topic/game.{gameId}` - 게임 구독

## KataGo 설정

### 필수 파일
- KataGo 실행 파일
- GTP 설정 파일 (gtp_config.cfg)
- 모델 파일 (.bin)

### application.yml 설정
```yaml
katago:
  path: /path/to/katago
  config: /path/to/gtp_config.cfg
  model: /path/to/model.bin
```

## 실행 방법

### 로컬 실행
```bash
./gradlew bootRun
```

### Docker 실행 (CUDA 지원)
```bash
./gradlew bootJar
docker build -t woowago .
docker run --gpus all -p 9001:9001 woowago
```

서버는 `9001` 포트에서 실행됩니다.

## 게임 규칙

- 19x19 바둑판
- 흑 선수
- 자충수 금지
- 패 규칙 적용
- 따내기 자동 처리

## 프로젝트 구조

```
src/main/java/com/woowa/woowago/
├── client/              # KataGo GTP 클라이언트
├── config/              # WebSocket, CORS 설정
├── controller/          # REST, WebSocket 컨트롤러
├── domain/              # 도메인 모델
├── dto/                 # 요청/응답 DTO
├── scheduler/           # 타임아웃 체크 스케줄러
├── service/             # 비즈니스 로직
└── util/                # GTP 좌표 변환
```

## 싱글플레이어 UI

`/static` 디렉토리에 바닐라 JS로 구현된 웹 UI 포함
- SVG 기반 바둑판
- 실시간 게임 상태 표시
- KataGo 착수 추천/형세 판단/계가 지원