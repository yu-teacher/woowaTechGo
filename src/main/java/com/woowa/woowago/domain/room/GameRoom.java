package com.woowa.woowago.domain.room;

import com.woowa.woowago.domain.game.Game;
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
        // 임시: 빈 Set 반환 (Service에서 getSpectatorCount 사용하도록 변경 필요)
        return java.util.Collections.emptySet();
    }
}