package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 일반 액션 요청 DTO
 * (무르기, 새 게임, 퇴장 등)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameActionRequest {
    private String gameId;
    private String username;
}