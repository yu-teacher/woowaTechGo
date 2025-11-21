package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 역할 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoleResponse {
    private String role;  // player1, player2, spectator
}