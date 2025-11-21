package com.woowa.woowago.domain;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * 게임 방 도메인
 * 참가자 2명 + 관전자들 관리
 */
@Getter
public class GameRoom {
    private final String roomId;
    private String player1;
    private String player2;
    private final Set<String> spectators;
    private Game game;
    private boolean gameStarted = false;

    public GameRoom(String roomId) {
        this.roomId = roomId;
        this.spectators = new HashSet<>();
        this.game = new Game();
    }

    /**
     * 사용자를 방에 추가 (선착순으로 참가자 또는 관전자 배정)
     * @param username 사용자명
     * @return 배정된 역할 (player1/player2/spectator)
     */
    public String addUser(String username) {
        if (player1 == null) {
            player1 = username;
            return "player1";
        }
        if (player2 == null) {
            player2 = username;
            return "player2";
        }
        spectators.add(username);
        return "spectator";
    }

    /**
     * 사용자 제거
     * @param username 사용자명
     */
    public void removeUser(String username) {
        if (username.equals(player1)) {
            player1 = null;
            return;
        }
        if (username.equals(player2)) {
            player2 = null;
            return;
        }
        spectators.remove(username);
    }

    /**
     * 사용자의 역할 조회
     * @param username 사용자명
     * @return player1/player2/spectator
     */
    public String getRole(String username) {
        if (username.equals(player1)) {
            return "player1";
        }
        if (username.equals(player2)) {
            return "player2";
        }
        if (spectators.contains(username)) {
            return "spectator";
        }
        return null;
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
        return player1 != null && player2 != null && !gameStarted;
    }

    /**
     * 게임 시작
     */
    public void startGame() {
        this.gameStarted = true;
    }

}