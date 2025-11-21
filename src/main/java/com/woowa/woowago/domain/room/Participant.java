package com.woowa.woowago.domain.room;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 참가자 Value Object
 */
@Getter
@EqualsAndHashCode(of = "username")
public class Participant {
    private final String username;
    private final ParticipantRole role;

    public Participant(String username, ParticipantRole role) {
        validateUsername(username);
        this.username = username;
        this.role = role;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("[ERROR] 사용자명은 비어있을 수 없습니다.");
        }
    }

    /**
     * 참가자인지 확인
     */
    public boolean isPlayer() {
        return role.isPlayer();
    }

    /**
     * 관전자인지 확인
     */
    public boolean isSpectator() {
        return role.isSpectator();
    }
}