package com.woowa.woowago.dto.websocket;

import com.woowa.woowago.domain.GameRoom;
import com.woowa.woowago.dto.GameStateResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 방 입장 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JoinResponse {
    private GameStateResponse gameState;
    private String role;              // player1, player2, spectator
    private String player1;
    private String player2;
    private int spectatorCount;
    private boolean isReady;          // 2명 모였는지
    private boolean gameStarted;      // 게임 시작했는지

    public static JoinResponse from(GameRoom room, GameStateResponse gameState, String role) {
        return new JoinResponse(
                gameState,
                role,
                room.getPlayer1(),
                room.getPlayer2(),
                room.getSpectators().size(),
                room.isReady(),
                room.isGameStarted()
        );
    }
}