package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 입장 요청 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameJoinRequest {
    private String gameId;
    private String username;
}