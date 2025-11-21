package com.woowa.woowago.domain.room;

import java.util.HashSet;
import java.util.Set;

/**
 * 참가자 목록을 관리하는 Collection Domain
 */
public class Participants {
    private Participant player1;
    private Participant player2;
    private final Set<Participant> spectators;

    public Participants() {
        this.spectators = new HashSet<>();
    }

    /**
     * 사용자 추가 (역할 자동 배정)
     * @param username 사용자명
     * @return 배정된 역할
     */
    public Participant add(String username) {
        // 이미 존재하는지 확인
        if (contains(username)) {
            throw new IllegalArgumentException("[ERROR] 이미 참가한 사용자입니다.");
        }

        // 선착순 배정: player1 → player2 → spectator
        if (player1 == null) {
            player1 = new Participant(username, ParticipantRole.PLAYER1);
            return player1;
        }

        if (player2 == null) {
            player2 = new Participant(username, ParticipantRole.PLAYER2);
            return player2;
        }

        Participant spectator = new Participant(username, ParticipantRole.SPECTATOR);
        spectators.add(spectator);
        return spectator;
    }

    /**
     * 사용자 제거
     * @param username 사용자명
     */
    public void remove(String username) {
        if (player1 != null && player1.getUsername().equals(username)) {
            player1 = null;
            return;
        }

        if (player2 != null && player2.getUsername().equals(username)) {
            player2 = null;
            return;
        }

        spectators.removeIf(s -> s.getUsername().equals(username));
    }

    /**
     * 역할 조회
     * @param username 사용자명
     * @return 역할 (player1/player2/spectator/null)
     */
    public String getRole(String username) {
        if (player1 != null && player1.getUsername().equals(username)) {
            return "player1";
        }

        if (player2 != null && player2.getUsername().equals(username)) {
            return "player2";
        }

        if (spectators.stream().anyMatch(s -> s.getUsername().equals(username))) {
            return "spectator";
        }

        return null;
    }

    /**
     * 사용자 존재 여부 확인
     */
    private boolean contains(String username) {
        return getRole(username) != null;
    }

    /**
     * 2명 모였는지 확인
     */
    public boolean isReady() {
        return player1 != null && player2 != null;
    }

    /**
     * 참가자 권한 검증
     * @param username 사용자명
     * @throws IllegalArgumentException 참가자가 아닐 경우
     */
    public void validatePlayerPermission(String username) {
        String role = getRole(username);
        if (!"player1".equals(role) && !"player2".equals(role)) {
            throw new IllegalArgumentException("[ERROR] 참가자만 이 작업을 수행할 수 있습니다.");
        }
    }

    public String getPlayer1Username() {
        return player1 != null ? player1.getUsername() : null;
    }

    public String getPlayer2Username() {
        return player2 != null ? player2.getUsername() : null;
    }

    public int getSpectatorCount() {
        return spectators.size();
    }

    public boolean isEmpty() {
        return player1 == null && player2 == null && spectators.isEmpty();
    }
}