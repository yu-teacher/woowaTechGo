package com.woowa.woowago.dto;

import com.woowa.woowago.domain.game.Stone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 상태 응답 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameStateResponse {
    private Stone[][] board;  // 19x19 바둑판
    private String currentTurn;  // "BLACK" 또는 "WHITE"
    private int blackCaptures;
    private int whiteCaptures;
    private int moveCount;
}