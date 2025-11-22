package com.woowa.woowago.dto.websocket;

import com.woowa.woowago.domain.room.GameRoom;
import com.woowa.woowago.dto.GameStateResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 시작 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StartResponse {
    private GameStateResponse gameState;
    private String player1;
    private String player2;
    private String blackPlayer;       // 흑돌 플레이어
    private String whitePlayer;       // 백돌 플레이어

    public static StartResponse from(GameRoom room, GameStateResponse gameState) {
        String blackPlayer = null;
        String whitePlayer = null;

        if (room.getSettings() != null) {
            blackPlayer = room.getSettings().getBlackPlayer();
            whitePlayer = room.getSettings().getWhitePlayer();
        }

        return new StartResponse(
                gameState,
                room.getPlayer1(),
                room.getPlayer2(),
                blackPlayer,
                whitePlayer
        );
    }
}