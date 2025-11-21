package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 착수 요청 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameMoveRequest {
    private String gameId;
    private String username;
    private int x;
    private int y;
}