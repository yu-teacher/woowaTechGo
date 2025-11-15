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
- [ ] 바둑판 컨테이너
- [ ] 게임 정보 영역 (현재 차례, 따낸 돌 수)
- [ ] 컨트롤 버튼 영역
- [ ] 착수 기록 영역

#### 6.2. CSS
- [ ] 바둑판 스타일 (19x19 Grid)
- [ ] 돌 스타일 (흑/백)
- [ ] 버튼 스타일
- [ ] 반응형 레이아웃

#### 6.3. JavaScript
- [ ] 바둑판 렌더링
- [ ] 바둑판 클릭 이벤트 처리
- [ ] 게임 상태 업데이트
- [ ] 현재 차례 표시
- [ ] 따낸 돌 개수 표시
- [ ] 착수 기록 표시
- [ ] 무르기 버튼 기능
- [ ] 블루스팟 표시 버튼 + 시각화
- [ ] 계가 결과 표시 버튼
- [ ] 새 게임 시작 버튼
- [ ] 에러 메시지 표시 (toast)
- [ ] API 통신 (fetch)