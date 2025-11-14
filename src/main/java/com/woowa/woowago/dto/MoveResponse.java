package com.woowa.woowago.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 착수 결과 응답 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MoveResponse {
    private String message;
    private GameStateResponse gameState;
}
