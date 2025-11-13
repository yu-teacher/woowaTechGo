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
- [ ] validate(Board, Position, Stone, Board previousBoard) - 착수 가능 여부 검증
    - [ ] 좌표 유효성 검증
    - [ ] 빈 자리 확인
    - [ ] 자충수 검증 (시뮬레이션 후 활로 확인)
    - [ ] 패 규칙 검증 (직전 국면과 비교)

#### 1.9. CaptureHandler (따내기 처리)
- [x] execute(Board, Position, Stone) - 따내기 실행
    - [x] 착수한 위치의 인접 상대 그룹들 확인
    - [x] 활로 0인 그룹 찾기
    - [x] 해당 그룹의 돌들 제거
    - [x] 제거된 Position들 반환

#### 1.10. Game (게임 조립 + 흐름 제어)
- [ ] GameState state
- [ ] MoveHistory history
- [ ] List<Board> boardHistory
- [ ] move(Position) - 착수 처리
- [ ] undo() - 마지막 수 무르기
- [ ] getState() - 게임 상태 조회
- [ ] getMoveHistory() - 착수 기록 조회

---

### 2. 서비스

#### 2.1. GameService
- [ ] Game game (싱글톤 인스턴스)
- [ ] startNewGame() - 새 게임 시작
- [ ] move(int x, int y) - 착수 요청 처리
- [ ] undo() - 무르기 처리
- [ ] getGameState() - 게임 상태 DTO 반환
- [ ] getMoveHistory() - 착수 기록 반환

#### 2.2. KataGoService
- [ ] getBlueSpots() - 착수 추천 요청
- [ ] getScore() - 계가 요청

---

### 3. 외부 API

#### 3.1. KataGoClient
- [ ] KataGo API 연결 설정
- [ ] convertToKataGoFormat(List<Move>) - 착수 기록을 KataGo 형식으로 변환
- [ ] requestBlueSpots(String gameData) - 착수 추천 요청
- [ ] requestScore(String gameData) - 계가 요청 (한국 룰, 백 덤 6.5)
- [ ] 응답 파싱 및 DTO 변환

---

### 4. DTO

#### 4.1. 응답 DTO
- [ ] GameStateResponse - 게임 상태 응답
- [ ] MoveResponse - 착수 결과 응답
- [ ] BlueSpotsResponse - 착수 추천 응답
- [ ] ScoreResponse - 계가 결과 응답
- [ ] ErrorResponse - 에러 응답

#### 4.2. 요청 DTO
- [ ] MoveRequest - 착수 요청 (x, y)

---

### 5. 컨트롤러

#### 5.1. GameController
- [ ] POST `/api/game/start` - 새 게임 시작
- [ ] POST `/api/game/move` - 착수 (RequestBody: x, y)
- [ ] DELETE `/api/game/move` - 무르기
- [ ] GET `/api/game` - 게임 상태 조회
- [ ] GET `/api/game/moves` - 착수 기록 조회

#### 5.2. KataGoController
- [ ] GET `/api/katago/bluespots` - 착수 추천
- [ ] GET `/api/katago/score` - 계가 결과

#### 5.3. ExceptionHandler
- [ ] 유효하지 않은 좌표 예외 처리
- [ ] 이미 돌이 있는 위치 예외 처리
- [ ] 자충수 예외 처리
- [ ] 패 규칙 위반 예외 처리
- [ ] 무를 수 없음 예외 처리
- [ ] 게임 없음 예외 처리

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

---

### 7. 테스트

#### 7.1. Position 테스트
- [ ] 유효한 좌표 생성
- [ ] 유효하지 않은 좌표 예외
- [ ] 인접 좌표 계산
- [ ] equals, hashCode

#### 7.2. Stone 테스트
- [ ] opposite() 메서드

#### 7.3. Board 테스트
- [ ] 초기화
- [ ] 돌 놓기
- [ ] 돌 제거
- [ ] 복사
- [ ] equals

#### 7.4. Move 테스트
- [ ] 생성자
- [ ] getter

#### 7.5. GameState 테스트
- [ ] 초기 상태
- [ ] getter/setter

#### 7.6. MoveHistory 테스트
- [ ] 착수 추가
- [ ] 마지막 착수 제거
- [ ] 조회 메서드들

#### 7.7. StoneGroup 테스트
- [ ] 그룹 찾기 (연결된 돌)
- [ ] 활로 계산
- [ ] 죽은 그룹 찾기

#### 7.8. MoveValidator 테스트
- [ ] 좌표 유효성 검증
- [ ] 빈 자리 확인
- [ ] 자충수 검증
- [ ] 패 규칙 검증

#### 7.9. CaptureHandler 테스트
- [ ] 단일 돌 따내기
- [ ] 그룹 따내기
- [ ] 여러 그룹 동시 따내기

#### 7.10. Game 통합 테스트
- [ ] 정상 착수 시나리오
- [ ] 따내기 시나리오
- [ ] 자충수 시나리오
- [ ] 패 규칙 시나리오
- [ ] 무르기 시나리오

#### 7.11. GameService 테스트
- [ ] 새 게임 시작
- [ ] 착수 처리
- [ ] 무르기 처리
- [ ] 상태 조회

#### 7.12. KataGoService 테스트
- [ ] 블루스팟 요청 (Mocking)
- [ ] 계가 요청 (Mocking)

#### 7.13. GameController 테스트
- [ ] POST /api/game/start (MockMvc)
- [ ] POST /api/game/move (MockMvc)
- [ ] DELETE /api/game/move (MockMvc)
- [ ] GET /api/game (MockMvc)
- [ ] 예외 처리

#### 7.14. KataGoController 테스트
- [ ] GET /api/katago/bluespots (MockMvc)
- [ ] GET /api/katago/score (MockMvc)