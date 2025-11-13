package com.woowa.woowago.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * 따내기 처리를 담당하는 클래스
 * 착수 후 활로가 0이 된 상대 돌을 찾아서 제거
 */
public class CaptureHandler {

    /**
     * 따낼 수 있는 돌들을 찾아서 실제로 제거
     * @param board 바둑판
     * @param position 방금 착수한 위치
     * @param stone 놓은 돌의 색상
     * @return 제거된 돌들의 위치
     */
    public static Set<Position> execute(Board board, Position position, Stone stone) {
        Set<Position> captured = findCapturable(board, position, stone);
        board.removeStones(captured);
        return captured;
    }

    /**
     * 따낼 수 있는 돌들 찾기 (실제로 제거하지는 않음)
     * @param board 바둑판
     * @param position 착수한 위치
     * @param stone 놓은 돌의 색상
     * @return 따낼 수 있는 돌들의 위치
     */
    public static Set<Position> findCapturable(Board board, Position position, Stone stone) {
        Set<Position> captured = new HashSet<>();
        Stone opponentColor = stone.opposite();

        for (Position adjacent : position.getAdjacentPositions()) {
            if (board.getStone(adjacent) == opponentColor) {
                Set<Position> group = StoneGroup.findConnectedGroup(board, adjacent);
                if (StoneGroup.countLiberties(board, group) == 0) {
                    captured.addAll(group);
                }
            }
        }

        return captured;
    }
}