package com.woowa.woowago.domain.room;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * 대기 중인 요청
 * Value Object
 */
@Getter
@EqualsAndHashCode
public class PendingRequest {

    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(30);

    private final RequestType type;
    private final String requester;
    private final Instant requestedAt;

    public PendingRequest(RequestType type, String requester) {
        this.type = type;
        this.requester = requester;
        this.requestedAt = Instant.now();
    }

    /**
     * 타임아웃 여부 확인 (30초 경과)
     */
    public boolean isTimeout() {
        return Duration.between(requestedAt, Instant.now()).compareTo(TIMEOUT_DURATION) > 0;
    }

    /**
     * 응답해야 할 플레이어 찾기
     * @param player1 참가자1
     * @param player2 참가자2
     * @return 응답자 username
     */
    public String getTargetPlayer(String player1, String player2) {
        if (requester.equals(player1)) {
            return player2;
        }
        if (requester.equals(player2)) {
            return player1;
        }
        throw new IllegalStateException("[ERROR] 요청자가 참가자가 아닙니다.");
    }
}