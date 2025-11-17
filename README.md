# WooTeGo 🎮

Spring Boot 기반 웹 바둑 게임 with KataGo AI

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring)
![KataGo](https://img.shields.io/badge/KataGo-AI-orange?style=flat-square)

## 📝 프로젝트 소개

19x19 바둑판에서 정확한 바둑 규칙(따내기, 자충수, 패)을 구현하고, KataGo AI를 활용한 착수 추천 및 형세 판단 기능을 제공하는 웹 애플리케이션입니다.

## 🎯 제작 동기

평소 바둑을 즐겨보는 사람으로서, 바둑 게임을 직접 구현해보고 싶다는 생각을 자주 했습니다. 하지만 바둑은 단순해 보이는 규칙 속에 복잡한 예외 상황들(따내기, 자충수, 패)이 숨어있고, 이를 올바른 객체 구조로 표현하는 것이 쉽지 않을 것으로 예상했습니다.

**"어려운 문제일수록 좋은 학습 과제"** 라는 생각으로 이 주제를 선택했습니다.
- 복잡한 도메인 규칙을 어떻게 코드로 옮길 것인가?
- 상태 변화가 많은 게임을 어떻게 안전하게 관리할 것인가?
- 외부 AI 엔진을 어떻게 통합할 것인가?

이러한 기술적 도전들이 객체지향 설계와 문제 해결 능력을 증명하기에 적합하다고 판단했습니다.

## ✨ 주요 기능

- **정확한 바둑 규칙 구현**
    - 따내기 (활로 0인 그룹 제거)
    - 자충수 방지 (단, 상대 따내기 가능 시 허용)
    - 패(Ko) 규칙 (직전 국면 재현 금지)

- **KataGo AI 연동**
    - 착수 추천 (블루스팟)
    - 형세 판단
    - 계가

- **편리한 게임 진행**
    - 실시간 바둑판 시각화
    - 무르기 기능
    - 착수 기록 표시
    - 마지막 착수 하이라이트

## 🔥 기술적 도전 과제

### 백엔드 집중 전략
백엔드 지원자로서 **핵심 역량은 서버 사이드 로직**에 있다고 판단했습니다. 따라서:
- **프론트엔드**: AI 도구(Claude) 활용하여 빠르게 구현
- **백엔드**: 직접 설계하고 구현
    - 도메인 구조 설계
    - 핵심 알고리즘 선택 (BFS 기반 그룹 탐색)
    - 규칙 검증 로직 (시뮬레이션 기반 자충수 판단)
    - 109개 테스트 케이스 작성

시간을 효율적으로 배분하여 **가장 중요한 부분에 집중**할 수 있었습니다.

### KataGo 연동의 난관
KataGo는 세계 최강급 오픈소스 바둑 AI이지만, 사용 방법을 익히는 것이 가장 큰 도전이었습니다.

**3일간의 학습 과정**:
1. GTP(Go Text Protocol) 프로토콜 문서 학습
2. 터미널에서 KataGo 직접 실행 및 명령어 테스트
3. Java ProcessBuilder로 프로세스 통신 구현
4. 응답 파싱 및 에러 처리

**특히 어려웠던 점**:
- stdin/stdout 버퍼링 이슈로 응답이 제대로 안 오는 문제
- `kata-raw-nn` 명령어의 복잡한 JSON 응답 파싱

결과적으로 **외부 시스템 통합 경험**을 키울 수 있었습니다.

### 바둑 규칙의 객체지향 구현

#### 1. 패(Ko) 규칙
"직전 국면을 즉시 재현할 수 없다"는 규칙을 구현하기 위해:
- `List<Board> boardHistory`로 전체 국면 기록
- `Board.equals()`로 국면 비교
- 착수 전 시뮬레이션으로 검증

#### 2. 자충수 검증
"활로 0이 되는 수는 둘 수 없다"는 규칙의 예외 처리:
- 단순히 활로만 체크하면 안 됨
- 상대를 따내서 활로가 생기는 경우 허용
- `Board.copy()`로 가상 착수 → 따내기 → 활로 재계산

#### 3. 따내기 로직
연결된 돌 그룹을 찾고 활로를 계산:
- BFS 알고리즘으로 같은 색 돌 탐색
- Set으로 활로 중복 제거
- 인접한 모든 상대 그룹 순회

**설계 원칙**: 각 클래스가 하나의 책임만 가지도록 분리
- `StoneGroup`: 그룹 분석만
- `MoveValidator`: 검증만
- `CaptureHandler`: 따내기만
- `Game`: 전체 흐름 조율

## 🛠 기술 스택

### Backend
- Java 21
- Spring Boot 3.x
- Lombok

### Frontend
- Vanilla JavaScript
- SVG (바둑판 렌더링)
- CSS3

### AI
- KataGo (GTP 프로토콜 통신)

## 🚀 실행 방법

### 1. KataGo 설치 및 설정

1. [KataGo 다운로드](https://github.com/lightvector/KataGo/releases) 및 설치
2. `application.yml` 경로 수정:
```yaml
katago:
  path: {KataGo 실행파일 경로}
  config: {gtp_config.cfg 경로}
  model: {모델 파일 경로}
```

### 2. 애플리케이션 실행

```bash
# 빌드 및 실행
./gradlew bootRun

# 또는
./gradlew build
java -jar build/libs/woowago-0.0.1-SNAPSHOT.jar
```

### 3. 브라우저 접속

```
http://localhost:8080
```

## 📂 프로젝트 구조

```
src/main/java/com/woowa/woowago/
├── domain/          # 핵심 도메인 모델
├── service/         # 비즈니스 로직
├── controller/      # REST API
├── client/          # 외부 API 통신
├── dto/             # 데이터 전송 객체
├── util/            # 유틸리티 클래스
└── config/          # 설정 클래스

src/main/resources/static/
├── index.html       # UI
├── app.js           # 게임 로직
└── style.css        # 스타일링

src/test/java/       # 테스트 (109개)
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test
```

**테스트 구성 (총 109개)**:
- **도메인 로직 테스트**: 바둑 규칙의 정확성 검증 (Position, Board, StoneGroup, MoveValidator, CaptureHandler, Game 등)
- **서비스 계층 테스트**: 비즈니스 로직 검증 (GameService, KataGoService)
- **API 통합 테스트**: 전체 요청-응답 흐름 검증 (GameController, KataGoController)
- **예외 처리 테스트**: 모든 에러 케이스 검증

**주요 테스트 사례**:
- 패 규칙 시나리오 (가장 복잡한 바둑 규칙)
- KataGo 실제 프로 기보 273수 계가 검증
- 자충수 및 따내기 엣지 케이스

## 📡 API 명세

### 게임 관리
- `POST /api/game/start` - 새 게임 시작
- `POST /api/game/move` - 착수
- `DELETE /api/game/move` - 무르기
- `GET /api/game` - 게임 상태 조회
- `GET /api/game/moves` - 착수 기록 조회

### KataGo AI
- `GET /api/katago/bluespots` - 착수 추천
- `GET /api/katago/score` - 계가

## 💡 회고

...