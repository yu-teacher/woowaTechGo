package com.woowa.woowago.service;
import com.woowa.woowago.domain.game.Game;
import com.woowa.woowago.domain.move.Move;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import com.woowa.woowago.dto.GameStateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 게임 비즈니스 로직을 처리하는 서비스
 * 싱글톤으로 하나의 게임만 관리
 */
@Service
public class GameService {

    private Game game;

    public GameService() {
        this.game = new Game();
    }

    /**
     * 새 게임 시작
     */
    public void startNewGame() {
        this.game = new Game();
    }

    /**
     * 착수 요청 처리
     * @param x 가로 좌표 (1~19)
     * @param y 세로 좌표 (1~19)
     * @throws IllegalArgumentException 착수 불가능할 경우
     */
    public void move(int x, int y) {
        Position position = new Position(x, y);
        game.move(position);
    }

    /**
     * 무르기 처리
     * @throws IllegalStateException 무를 수 없을 경우
     */
    public void undo() {
        game.undo();
    }

    /**
     * 현재 게임 조회
     * @return Game 객체
     */
    public Game getGame() {
        return game;
    }

    /**
     * 게임 상태 DTO 반환
     * @return GameStateResponse
     */
    public GameStateResponse getGameState() {
        Stone[][] board = convertBoardToArray();
        String currentTurn = game.getState().getCurrentTurn().name();
        int blackCaptures = game.getState().getBlackCaptures();
        int whiteCaptures = game.getState().getWhiteCaptures();
        int moveCount = game.getMoveHistory().size();

        return new GameStateResponse(board, currentTurn, blackCaptures, whiteCaptures, moveCount);
    }

    /**
     * 착수 기록 반환
     * @return 착수 기록 리스트
     */
    public List<Move> getMoveHistory() {
        return game.getMoveHistory();
    }

    private Stone[][] convertBoardToArray() {
        Stone[][] boardArray = new Stone[19][19];
        for (int x = 1; x <= 19; x++) {
            for (int y = 1; y <= 19; y++) {
                boardArray[x - 1][y - 1] = game.getState()
                        .getBoard()
                        .getStone(new Position(x, y));
            }
        }
        return boardArray;
    }
}