package com.woowa.woowago.domain.room;

/**
 * 참가자 역할
 */
public enum ParticipantRole {
    PLAYER1,    // 참가자 1
    PLAYER2,    // 참가자 2
    SPECTATOR;  // 관전자

    /**
     * 참가자인지 확인
     */
    public boolean isPlayer() {
        return this == PLAYER1 || this == PLAYER2;
    }

    /**
     * 관전자인지 확인
     */
    public boolean isSpectator() {
        return this == SPECTATOR;
    }

    /**
     * 문자열 변환 (소문자)
     */
    public String toLowerCase() {
        return name().toLowerCase();
    }
}