package com.woowa.woowago.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 바둑 게임의 전체 흐름을 관리하는 클래스
 * 착수, 무르기 등 게임 로직 조립
 */
public class Game {
    @Getter
    private final GameState state;
    private final MoveHistory moveHistory;
    @Getter
    private final List<Board> boardHistory;

    /**
     * 새 게임 시작
     * 빈 바둑판, 흑 먼저, 초기 Board를 history에 저장
     */
    public Game() {
        this.state = new GameState();
        this.moveHistory = new MoveHistory();
        this.boardHistory = new ArrayList<>();
        this.boardHistory.add(state.getBoard().copy());
    }

    /**
     * 착수 처리
     * @param position 착수 위치
     * @throws IllegalArgumentException 착수 불가능할 경우
     */
    public void move(Position position) {
        Board previousBoard = getPreviousBoard();
        Stone currentColor = state.getCurrentTurn();

        // 1. 착수 검증
        MoveValidator.validate(state.getBoard(), position, currentColor, previousBoard);

        // 2. 돌 놓기
        state.getBoard().placeStone(position, currentColor);

        // 3. 상대 돌 따내기
        Set<Position> captured = CaptureHandler.execute(state.getBoard(), position, currentColor);

        // 4. Board History 저장
        boardHistory.add(state.getBoard().copy());

        // 5. Move 기록
        int moveNumber = moveHistory.size() + 1;
        moveHistory.add(new Move(moveNumber, position, currentColor));

        // 6. 따낸 돌 개수 업데이트
        if (!captured.isEmpty()) {
            state.addCaptures(currentColor.opposite(), captured.size());
        }

        // 7. 차례 변경
        state.setCurrentTurn(currentColor.opposite());
    }

    /**
     * 마지막 수 무르기
     * @throws IllegalStateException 무를 수 없을 경우
     */
    public void undo() {
        if (moveHistory.isEmpty()) {
            throw new IllegalStateException("[ERROR] 무를 수 없습니다.");
        }

        // 1. Move 제거
        Move lastMove = moveHistory.removeLast();

        // 2. Board History에서 마지막 제거
        boardHistory.remove(boardHistory.size() - 1);

        // 3. 이전 Board로 복원
        Board restoredBoard = boardHistory.get(boardHistory.size() - 1);
        state.setBoard(restoredBoard.copy());

        // 4. 따낸 돌 개수 재계산
        recalculateCaptures();

        // 5. 차례 되돌리기
        state.setCurrentTurn(lastMove.getColor());
    }

    /**
     * 직전 바둑판 조회 (패 규칙용)
     * @return 직전 Board, 없으면 null
     */
    private Board getPreviousBoard() {
        if (boardHistory.size() < 2) {
            return null;
        }
        return boardHistory.get(boardHistory.size() - 2);
    }

    /**
     * 전체 Move History를 기반으로 따낸 돌 개수 재계산
     */
    private void recalculateCaptures() {
        int blackCaptures = 0;
        int whiteCaptures = 0;

        // boardHistory를 순회하며 따낸 돌 계산
        for (int i = 1; i < boardHistory.size(); i++) {
            Board prev = boardHistory.get(i - 1);
            Board curr = boardHistory.get(i);

            int prevBlackCount = countStones(prev, Stone.BLACK);
            int prevWhiteCount = countStones(prev, Stone.WHITE);
            int currBlackCount = countStones(curr, Stone.BLACK);
            int currWhiteCount = countStones(curr, Stone.WHITE);

            // 흑돌이 줄었으면 백이 따냄
            if (prevBlackCount > currBlackCount) {
                whiteCaptures += (prevBlackCount - currBlackCount);
            }

            // 백돌이 줄었으면 흑이 따냄
            if (prevWhiteCount > currWhiteCount) {
                blackCaptures += (prevWhiteCount - currWhiteCount);
            }
        }

        state.setBlackCaptures(blackCaptures);
        state.setWhiteCaptures(whiteCaptures);
    }

    /**
     * 바둑판에서 특정 색 돌의 개수 세기
     */
    private int countStones(Board board, Stone stone) {
        int count = 0;
        for (int x = 1; x <= 19; x++) {
            for (int y = 1; y <= 19; y++) {
                if (board.getStone(new Position(x, y)) == stone) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 착수 기록 조회
     */
    public List<Move> getMoveHistory() {
        return moveHistory.getAll();
    }
}
