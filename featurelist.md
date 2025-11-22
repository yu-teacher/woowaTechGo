## 기능 목록

### 1. 도메인

#### 1.1. Position (좌표 값 객체)
- [x] x, y 필드 (1~19)
- [x] 생성 시 유효성 검증
- [x] getAdjacentPositions() - 상하좌우 인접 좌표 반환
- [x] equals, hashCode

#### 1.2. Stone (Enum)
- [x] BLACK, WHITE, EMPTY
- [x] opposite() - 반대 색상 반환

#### 1.3. Board (바둑판 상태)
- [x] Stone[][] stones (19x19)
- [x] getStone(Position) - 특정 좌표의 돌 조회
- [x] placeStone(Position, Stone) - 돌 놓기
- [x] removeStones(Set<Position>) - 여러 돌 제거
- [x] copy() - 바둑판 복사
- [x] equals() - 바둑판 상태 비교 (패 규칙용)

#### 1.4. Move (착수 기록 값 객체)
- [x] moveNumber - 착수 번호
- [x] position - 착수 위치
- [x] color - 놓은 돌 색
- [x] 생성자, getter

#### 1.5. GameState (게임 상태)
- [x] Board board
- [x] Stone currentTurn
- [x] int blackCaptures
- [x] int whiteCaptures
- [x] getter/setter

#### 1.6. MoveHistory (착수 기록 관리)
- [x] List<Move> moves
- [x] add(Move) - 착수 추가
- [x] removeLast() - 마지막 착수 제거
- [x] getLast() - 마지막 착수 조회
- [x] getAll() - 전체 착수 기록 조회
- [x] isEmpty() - 기록 존재 여부

#### 1.7. StoneGroup (그룹 분석 유틸)
- [x] findConnectedGroup(Board, Position) - BFS로 연결된 같은 색 그룹 찾기
- [x] countLiberties(Board, Set<Position>) - 그룹의 활로 개수 계산

#### 1.8. MoveValidator (착수 검증)
- [x] validate(Board, Position, Stone, Board previousBoard) - 착수 가능 여부 검증
    - [x] 좌표 유효성 검증
    - [x] 빈 자리 확인
    - [x] 자충수 검증 (시뮬레이션 후 활로 확인)
    - [x] 패 규칙 검증 (직전 국면과 비교)

#### 1.9. CaptureHandler (따내기 처리)
- [x] execute(Board, Position, Stone) - 따내기 실행
    - [x] 착수한 위치의 인접 상대 그룹들 확인
    - [x] 활로 0인 그룹 찾기
    - [x] 해당 그룹의 돌들 제거
    - [x] 제거된 Position들 반환

#### 1.10. Game (게임 조립 + 흐름 제어)
- [x] GameState state
- [x] MoveHistory history
- [x] List<Board> boardHistory
- [x] move(Position) - 착수 처리
- [x] undo() - 마지막 수 무르기
- [x] getState() - 게임 상태 조회
- [x] getMoveHistory() - 착수 기록 조회

---

### 2. 서비스

#### 2.1. GameService
- [x] Game game (싱글톤 인스턴스)
- [x] startNewGame() - 새 게임 시작
- [x] move(int x, int y) - 착수 요청 처리
- [x] undo() - 무르기 처리
- [x] getGameState() - 게임 상태 DTO 반환
- [x] getMoveHistory() - 착수 기록 반환

#### 2.2. KataGoService
- [x] getBlueSpots() - 착수 추천 요청
- [x] getScore() - 계가 요청

---

### 3. 외부 API

#### 3.1. KataGoClient
- [x] KataGo 프로세스 시작/종료
- [x] GTP 프로토콜 통신 (sendCommand)
- [x] 바둑판 설정 (setBoardSize, clearBoard)
- [x] 착수 입력 (play)
- [x] 착수 추천 (genmove) - 블루스팟용
- [x] 점수 계산 (finalScore - kata-raw-nn)
- [x] YAML 설정 파일로 경로 관리
- [x] GTP 좌표 변환 유틸 (GtpCoordinateConverter)

---

### 4. DTO

#### 4.1. 응답 DTO
- [x] GameStateResponse - 게임 상태 응답
- [x] MoveResponse - 착수 결과 응답
- [x] BlueSpotsResponse - 착수 추천 응답
- [x] ScoreResponse - 계가 결과 응답
- [x] ErrorResponse - 에러 응답
- [x] MoveDTO - 움직임

#### 4.2. 요청 DTO
- [x] MoveRequest - 착수 요청 (x, y)

---

### 5. 컨트롤러

#### 5.1. GameController
- [x] POST `/api/game/start` - 새 게임 시작
- [x] POST `/api/game/move` - 착수 (RequestBody: x, y)
- [x] DELETE `/api/game/move` - 무르기
- [x] GET `/api/game` - 게임 상태 조회
- [x] GET `/api/game/moves` - 착수 기록 조회

#### 5.2. KataGoController
- [x] GET `/api/katago/bluespots` - 착수 추천
- [x] GET `/api/katago/score` - 계가 결과

#### 5.3. ExceptionHandler
- [x] 유효하지 않은 좌표 예외 처리
- [x] 이미 돌이 있는 위치 예외 처리
- [x] 자충수 예외 처리
- [x] 패 규칙 위반 예외 처리
- [x] 무를 수 없음 예외 처리
- [x] 게임 없음 예외 처리

---

### 6. 프론트엔드

#### 6.1. HTML
- [x] 바둑판 컨테이너
- [x] 게임 정보 영역 (현재 차례, 따낸 돌 수)
- [x] 컨트롤 버튼 영역
- [x] 착수 기록 영역

#### 6.2. CSS
- [x] 바둑판 스타일 (19x19 Grid)
- [x] 돌 스타일 (흑/백)
- [x] 버튼 스타일
- [x] 반응형 레이아웃

#### 6.3. JavaScript
- [x] 바둑판 렌더링
- [x] 바둑판 클릭 이벤트 처리
- [x] 게임 상태 업데이트
- [x] 현재 차례 표시
- [x] 따낸 돌 개수 표시
- [x] 착수 기록 표시
- [x] 무르기 버튼 기능
- [x] 블루스팟 표시 버튼 + 시각화
- [x] 계가 결과 표시 버튼
- [x] 새 게임 시작 버튼
- [x] 에러 메시지 표시 (toast)
- [x] API 통신 (fetch)
- [x] 교차점 호버 미리보기 - 빈 교차점에 마우스 올리면 반투명 돌 표시
- [x] 마지막 착수 표시 - 작은 색깔 원 (흑돌: 빨강, 백돌: 금색)
- [x] 형세 판단 기능 - 토스트로 "[백 6.5집 우세]"
- [x] 계가 모달 - "[백 6.5집 승리]" + 새 게임 버튼

# 2차 기능 구현 - 웹소켓 멀티플레이어

## 1. 도메인

### 1.1. GameRoom
- [x] roomId, player1, player2, spectators, game 필드
- [x] addUser(username) - 선착순 자동 배정 (player1 → player2 → spectator)
- [x] removeUser(username) - 사용자 제거
- [x] getRole(username) - 역할 반환 (player1/player2/spectator)
- [x] resetGame() - 게임 초기화 (새 게임 시작)

---

## 2. 서비스

### 2.1. GameRoomService
- [x] Map<String, GameRoom> rooms - 방 목록 관리
- [x] createOrGetRoom(roomId) - 방 생성 또는 조회
- [x] joinRoom(roomId, username) - 방 입장 (자동 역할 배정)
- [x] leaveRoom(roomId, username) - 방 퇴장 (빈 방 자동 삭제)

### 2.2. KataGoService
- [x] getBlueSpots(Game game) - 멀티플레이어용 착수 추천
- [x] getScore(Game game) - 멀티플레이어용 계가

---

## 3. 설정

### 3.1. WebSocketConfig
- [x] STOMP 엔드포인트 등록 (`/ws-game`)
- [x] 메시지 브로커 설정 (`/topic`, `/app`)
- [x] CORS 설정 (`http://localhost:5173`)

### 3.2. WebConfig
- [x] REST API CORS 설정 (`/api/**` → `http://localhost:5173`)

---

## 4. DTO

### 4.1. 요청 DTO (dto/websocket)
- [x] GameJoinRequest - 게임 입장 (gameId, username)
- [x] GameMoveRequest - 착수 (gameId, username, x, y)
- [x] GameActionRequest - 일반 액션 (gameId, username)

### 4.2. 응답 DTO (dto/websocket)
- [x] RoleResponse - 역할 응답 (role)
- [x] GameMessage - 게임 메시지 (type, data, username)

---

## 5. 컨트롤러

### 5.1. GameWebSocketController
- [x] `@MessageMapping("/game/join")` - 입장 및 역할 배정
- [x] `@MessageMapping("/game/start")` - 새 게임 시작 (참가자만)
- [x] `@MessageMapping("/game/move")` - 착수 (권한 체크)
- [x] `@MessageMapping("/game/undo")` - 무르기 (참가자만)
- [x] `@MessageMapping("/game/score")` - 계가 (KataGo 연동, 모두에게 브로드캐스트)
- [x] `@MessageMapping("/game/leave")` - 퇴장 처리

# 1차 리팩토링 - 도메인 중심 설계

## 목표
Controller의 도메인 로직을 Domain/Service 레이어로 이동하여 책임 분리

---

## 1. 새로운 도메인 객체 생성

### 1.1. Participant (Value Object)
- [x] username 필드
- [x] ParticipantRole enum (PLAYER1, PLAYER2, SPECTATOR)
- [x] isPlayer() - 참가자인지 확인
- [x] isSpectator() - 관전자인지 확인
- [x] equals/hashCode 구현

### 1.2. Participants (Collection Domain)
- [x] player1: Participant 필드
- [x] player2: Participant 필드
- [x] spectators: Set<Participant> 필드
- [x] add(username) - 역할 자동 배정
- [x] remove(username) - 사용자 제거
- [x] getRole(username) - 역할 조회
- [x] isReady() - 2명 모였는지 확인
- [x] validatePlayerPermission(username) - 참가자 권한 검증
- [x] getPlayer1Username(), getPlayer2Username() - username 조회

### 1.3. GameSettings (외적 룰)
- [x] blackPlayer: String 필드
- [x] whitePlayer: String 필드
- [x] assigned: boolean 필드
- [x] assignColors(player1, player2) - 흑/백 랜덤 배정
- [x] validateAssigned() - 배정 여부 확인
- [x] isMyTurn(username, currentTurn) - 내 차례 확인
- [x] getBlackPlayer(), getWhitePlayer() - getter

---

## 2. GameRoom 리팩토링

### 2.1. 필드 변경
- [x] ~~player1, player2, spectators~~ → Participants로 통합
- [x] GameSettings 필드 추가
- [x] ~~gameStarted~~ → started로 네이밍 통일

### 2.2. 도메인 로직 추가
- [x] validateExists() - 방 존재 확인
- [x] validateCanStart() - 게임 시작 가능 여부 검증
- [x] start(username) - 게임 시작 (검증 + 색상 배정 + 초기화)
- [x] move(username, position) - 착수 (권한 + 차례 검증)
- [x] undo(username) - 무르기 (권한 검증)
- [x] isMyTurn(username) - 내 차례인지 확인

### 2.3. 기존 메서드 수정
- [x] addUser() → participants.add()로 위임
- [x] removeUser() → participants.remove()로 위임
- [x] getRole() → participants.getRole()로 위임
- [x] isReady() → participants.isReady()로 위임

---

## 3. DTO 리팩토링

### 3.1. GameStateResponse
- [x] from(Game game) → GameStateResponse 정적 팩토리 메서드
- [x] convertBoardToArray(Game game) - Board 변환 로직

### 3.2. JoinResponse
- [x] 정적 팩토리 메서드 from(room, gameState, role)

### 3.3. StartResponse
- [x] 정적 팩토리 메서드 from(room, gameState, blackPlayer, whitePlayer)
- [x] GameSettings에서 흑/백 정보 가져오도록 수정


---

## 4. GameRoomService 리팩토링

### 4.1. 비즈니스 로직 추가
- [x] join(gameId, username) → JoinResponse 반환
- [x] start(gameId, username) → StartResponse 반환
- [x] move(gameId, username, x, y) → GameStateResponse 반환
- [x] undo(gameId, username) → GameStateResponse 반환
- [x] score(gameId) → ScoreResponse 반환

### 4.2. 검증 로직
- [x] getRoomOrThrow(gameId) - 방 조회 + 예외 처리

---

## 5. GameWebSocketController 단순화

### 5.1. 각 메서드 리팩토링
- [x] joinGame() - Service 호출만
- [x] startNewGame() - Service 호출만
- [x] makeMove() - Service 호출만
- [x] undo() - Service 호출만
- [x] calculateScore() - Service 호출만
- [x] leaveGame() - Service 호출만

### 5.2. 제거할 메서드
- [x] ~~buildGameStateResponse()~~ → GameStateResponse.from()으로 교체
- [x] ~~convertBoardToArray()~~ → GameStateResponse로 이동

### 5.3. 유지할 메서드
- [x] broadcastToRoom() - 메시지 전송
- [x] sendError() - 에러 전송

# 3차 기능 추가 - 요청/응답 시스템 & 연결 관리

## 목표
1. 게임 액션(시작/무르기/계가)에 상대방 동의 시스템 추가
2. WebSocket 연결 끊김 감지 및 즉시 퇴장 처리

---

## 1. 새로운 도메인 객체 생성

### 1.1. RequestType (Enum)
- [x] START - 게임 시작 요청
- [x] UNDO - 무르기 요청
- [x] SCORE - 계가 요청

### 1.2. PendingRequest (Value Object)
- [x] type: RequestType 필드
- [x] requester: String 필드 (요청한 사람)
- [x] requestedAt: Instant 필드 (요청 시간)
- [x] isTimeout() - 30초 경과 여부 확인
- [x] getTargetPlayer(room) - 응답해야 할 사람 찾기
- [x] equals/hashCode 구현

---

## 2. GameRoom 리팩토링

### 2.1. 필드 추가
- [x] pendingRequest: PendingRequest 필드

### 2.2. 요청/응답 로직
- [x] createRequest(type, requester) - 요청 생성
- [x] hasPendingRequest() - 대기 중인 요청 있는지
- [x] acceptRequest(responder) - 요청 수락
- [x] rejectRequest(responder) - 요청 거절
- [x] clearRequest() - 요청 초기화
- [x] validateCanRequest(username, type) - 요청 가능 여부 검증
- [x] validateCanRespond(username) - 응답 가능 여부 검증

### 2.3. 연결 관리 로직 (단순화)
- [x] handleDisconnect(username) - 방에서 사용자 제거

---

## 3. 새로운 DTO 생성

### 3.1. RequestMessage
- [x] type: String 필드 (REQUEST_START, REQUEST_UNDO, REQUEST_SCORE)
- [x] requester: String 필드
- [x] message: String 필드 ("OOO님이 게임 시작을 요청했습니다")

### 3.2. ResponseMessage
- [x] type: String 필드 (RESPOND_START, RESPOND_UNDO, RESPOND_SCORE)
- [x] responder: String 필드
- [x] accepted: boolean 필드

### 3.3. DisconnectMessage
- [x] username: String 필드
- [x] message: String 필드 ("OOO님의 연결이 끊어졌습니다")

### 3.4 TimeoutMessage
- [x] type: String 필드
- [x] requester: String 필드
- [x] message: String 필드 ("요청 시간이 초과되었습니다")

---

## 4. GameRoomService 리팩토링

### 4.1. 요청 메서드 추가
- [x] requestStart(gameId, username) → RequestMessage 반환
- [x] requestUndo(gameId, username) → RequestMessage 반환
- [x] requestScore(gameId, username) → RequestMessage 반환

### 4.2. 응답 메서드 추가
- [x] respondStart(gameId, username, accepted) → StartResponse or ErrorResponse
- [x] respondUndo(gameId, username, accepted) → GameStateResponse or ErrorResponse
- [x] respondScore(gameId, username, accepted) → ScoreResponse or ErrorResponse

### 4.3. 연결 관리 메서드 (단순화)
- [x] handleDisconnect(gameId, username) → DisconnectMessage

### 4.4. 기존 메서드 수정
- [x] start(gameId, username) 유지 (내부에서 바로 시작용)
- [x] undo(gameId, username) 유지 (내부에서 바로 무르기용)
- [x] score(gameId) 유지 (내부에서 바로 계가용)
- [x] 위 메서드들은 응답 accept 시 내부적으로 호출됨

---

## 5. GameWebSocketController 확장

### 5.1. 요청 엔드포인트 추가
- [x] @MessageMapping("/game/request/start")
- [x] @MessageMapping("/game/request/undo")
- [x] @MessageMapping("/game/request/score")

### 5.2. 응답 엔드포인트 추가
- [x] @MessageMapping("/game/respond/start")
- [x] @MessageMapping("/game/respond/undo")
- [x] @MessageMapping("/game/respond/score")

### 5.3. 기존 엔드포인트 유지 (하위 호환)
- [x] @MessageMapping("/game/start")
- [x] @MessageMapping("/game/undo")
- [x] @MessageMapping("/game/score")

---

## 6. WebSocket 이벤트 리스너 생성

### 6.1. WebSocketEventListener.java (새로 생성)
- [x] handleWebSocketDisconnectListener() - 연결 끊김 감지 → 방에서 제거

### 6.2. WebSocketConfig 수정
- [x] ChannelInterceptor 추가 - username/gameId 세션 저장
- [x] preSend() 구현 - SUBSCRIBE 시 gameId 추출하여 저장

---

## 7. 타임아웃 스케줄러

### 7.1. GameRoomScheduler.java (새로 생성)
- [ ] @Scheduled(fixedRate = 10000) - 10초마다 체크
- [ ] checkPendingRequests() - 요청 타임아웃 체크 (30초)
- [ ] autoRejectTimeoutRequests() - 자동 거절 처리
- [ ] broadcastTimeoutMessages() - 타임아웃 메시지 브로드캐스트

---

## 8. 메시지 타입 확장

### 8.1. 새로운 메시지 타입
```
REQUEST_START       - 게임 시작 요청
RESPOND_START       - 게임 시작 응답
REQUEST_UNDO        - 무르기 요청
RESPOND_UNDO        - 무르기 응답
REQUEST_SCORE       - 계가 요청
RESPOND_SCORE       - 계가 응답
DISCONNECT          - 연결 끊김 알림
TIMEOUT_REQUEST     - 요청 타임아웃
```

---
