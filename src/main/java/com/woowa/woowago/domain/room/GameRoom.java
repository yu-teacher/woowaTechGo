package com.woowa.woowago.domain.room;

import com.woowa.woowago.domain.game.Game;
import com.woowa.woowago.domain.game.Position;
import lombok.Getter;

/**
 * 게임 방 도메인
 * 참가자 2명 + 관전자들 관리
 */
@Getter
public class GameRoom {
    private final String roomId;
    private final Participants participants;
    private Game game;
    private GameSettings settings;
    private boolean started = false;
    private PendingRequest pendingRequest;

    public GameRoom(String roomId) {
        this.roomId = roomId;
        this.participants = new Participants();
        this.game = new Game();
    }

    /**
     * 사용자를 방에 추가 (선착순으로 참가자 또는 관전자 배정)
     * @param username 사용자명
     * @return 배정된 역할 (player1/player2/spectator)
     */
    public String addUser(String username) {
        Participant participant = participants.add(username);
        return participant.getRole().toLowerCase();
    }

    /**
     * 사용자 제거
     * @param username 사용자명
     */
    public void removeUser(String username) {
        participants.remove(username);
    }

    /**
     * 사용자의 역할 조회
     * @param username 사용자명
     * @return player1/player2/spectator
     */
    public String getRole(String username) {
        return participants.getRole(username);
    }

    /**
     * 게임 초기화 (새 게임 시작)
     * 사용자는 유지하고 게임만 새로 시작
     */
    public void resetGame() {
        this.game = new Game();
    }

    /**
     * 게임 준비 완료 여부 (player1, player2 모두 있음)
     */
    public boolean isReady() {
        return participants.isReady() && !started;
    }

    /**
     * 게임 시작
     */
    public void startGame() {
        this.started = true;
    }

    /**
     * 게임 시작 여부
     */
    public boolean isGameStarted() {
        return started;
    }

    /**
     * 게임 시작 가능 여부 검증
     */
    public void validateCanStart() {
        if (!participants.isReady()) {
            throw new IllegalArgumentException("[ERROR] 참가자가 2명이어야 게임을 시작할 수 있습니다.");
        }
        if (started) {
            throw new IllegalStateException("[ERROR] 이미 게임이 시작되었습니다.");
        }
    }

    /**
     * 게임 시작 (검증 + 색상 배정 + 초기화)
     * @param username 게임을 시작하는 사용자
     */
    public void start(String username) {
        // 1. 권한 검증
        participants.validatePlayerPermission(username);

        // 2. 시작 가능 검증
        validateCanStart();

        // 3. 색상 배정
        this.settings = new GameSettings();
        this.settings.assignColors(
                participants.getPlayer1Username(),
                participants.getPlayer2Username()
        );

        // 4. 게임 초기화 및 시작
        resetGame();
        this.started = true;
    }

    /**
     * 착수 (권한 + 차례 검증)
     * @param username 착수하는 사용자
     * @param position 착수 위치
     */
    public void move(String username, Position position) {
        // 1. 참가자 권한 검증
        participants.validatePlayerPermission(username);

        // 2. 차례 검증
        if (settings != null) {
            settings.validateMyTurn(username, game.getState().getCurrentTurn());
        }

        // 3. 착수
        game.move(position);
    }

    /**
     * 무르기 (권한 검증)
     * @param username 무르기를 요청하는 사용자
     */
    public void undo(String username) {
        // 1. 참가자 권한 검증
        participants.validatePlayerPermission(username);

        // 2. 무르기
        game.undo();
    }

    /**
     * 내 차례인지 확인
     * @param username 사용자명
     * @return 내 차례면 true
     */
    public boolean isMyTurn(String username) {
        if (settings == null || !started) {
            return false;
        }
        return settings.isMyTurn(username, game.getState().getCurrentTurn());
    }

    /**
     * Player1 username 조회
     */
    public String getPlayer1() {
        return participants.getPlayer1Username();
    }

    /**
     * Player2 username 조회
     */
    public String getPlayer2() {
        return participants.getPlayer2Username();
    }

    /**
     * 관전자 Set 조회
     */
    public java.util.Set<String> getSpectators() {
        return participants.getSpectatorUsernames();
    }

    /**
     * 요청 생성
     */
    public void createRequest(RequestType type, String requester) {
        validateCanRequest(requester, type);
        this.pendingRequest = new PendingRequest(type, requester);
    }

    /**
     * 대기 중인 요청이 있는지
     */
    public boolean hasPendingRequest() {
        return pendingRequest != null;
    }

    /**
     * 요청 수락
     */
    public void acceptRequest(String responder) {
        validateCanRespond(responder);
        clearRequest();
    }

    /**
     * 요청 거절
     */
    public void rejectRequest(String responder) {
        validateCanRespond(responder);
        clearRequest();
    }

    /**
     * 요청 초기화
     */
    public void clearRequest() {
        this.pendingRequest = null;
    }

    /**
     * 요청 가능 여부 검증
     */
    private void validateCanRequest(String username, RequestType type) {
        // 이미 대기 중인 요청이 있으면 불가
        if (hasPendingRequest()) {
            throw new IllegalStateException("[ERROR] 이미 처리 중인 요청이 있습니다.");
        }

        // 참가자만 요청 가능
        participants.validatePlayerPermission(username);

        // 게임 시작 전에는 무르기/계가 불가
        if (!started && (type == RequestType.UNDO || type == RequestType.SCORE)) {
            throw new IllegalStateException("[ERROR] 게임이 시작되지 않았습니다.");
        }
    }

    /**
     * 응답 가능 여부 검증
     */
    private void validateCanRespond(String username) {
        if (!hasPendingRequest()) {
            throw new IllegalStateException("[ERROR] 대기 중인 요청이 없습니다.");
        }

        // 요청자가 아닌 상대방만 응답 가능
        String targetPlayer = pendingRequest.getTargetPlayer(
                participants.getPlayer1Username(),
                participants.getPlayer2Username()
        );

        if (!username.equals(targetPlayer)) {
            throw new IllegalStateException("[ERROR] 응답 권한이 없습니다.");
        }
    }

    /**
     * 연결 끊김 처리
     */
    public void handleDisconnect(String username) {
        if (hasPendingRequest()) {
            String requester = pendingRequest.getRequester();
            String player1 = participants.getPlayer1Username();
            String player2 = participants.getPlayer2Username();

            if (username.equals(requester) || username.equals(player1) || username.equals(player2)) {
                clearRequest();
            }
        }

        removeUser(username);
    }
}