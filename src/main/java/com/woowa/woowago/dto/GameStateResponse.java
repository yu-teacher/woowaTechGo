package com.woowa.woowago.dto;

import com.woowa.woowago.domain.game.Game;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameStateResponse {
    private Stone[][] board;
    private String currentTurn;
    private int blackCaptures;
    private int whiteCaptures;
    private int moveCount;

    /**
     * Game 도메인으로부터 GameStateResponse 생성
     * @param game 게임 도메인
     * @return GameStateResponse
     */
    public static GameStateResponse from(Game game) {
        Stone[][] board = convertBoardToArray(game);
        String currentTurn = game.getState().getCurrentTurn().name();
        int blackCaptures = game.getState().getBlackCaptures();
        int whiteCaptures = game.getState().getWhiteCaptures();
        int moveCount = game.getMoveHistory().size();

        return new GameStateResponse(board, currentTurn, blackCaptures, whiteCaptures, moveCount);
    }

    /**
     * Board를 2차원 배열로 변환
     * EMPTY는 null로 변환 (JSON 직렬화를 위해)
     */
    private static Stone[][] convertBoardToArray(Game game) {
        Stone[][] boardArray = new Stone[19][19];
        for (int x = 1; x <= 19; x++) {
            for (int y = 1; y <= 19; y++) {
                Stone stone = game.getState()
                        .getBoard()
                        .getStone(new Position(x, y));
                boardArray[x - 1][y - 1] = (stone == Stone.EMPTY) ? null : stone;
            }
        }
        return boardArray;
    }
}