package com.woowa.woowago.domain;

import java.util.Set;

/**
 * 착수 가능 여부를 검증하는 클래스
 * 좌표 유효성, 빈 자리, 자충수, 패 규칙 검증
 */
public class MoveValidator {

    /**
     * 착수 가능 여부 종합 검증
     * @param board 현재 바둑판
     * @param position 착수하려는 위치
     * @param stone 놓으려는 돌 색상
     * @param previousBoard 직전 바둑판 상태 (패 규칙용, null 가능)
     * @throws IllegalArgumentException 착수 불가능할 경우
     */
    public static void validate(Board board, Position position, Stone stone, Board previousBoard) {
        validateEmptyPosition(board, position);
        validateSuicide(board, position, stone);
        validateKo(board, position, stone, previousBoard);
    }

    /**
     * 빈 자리인지 확인
     */
    private static void validateEmptyPosition(Board board, Position position) {
        if (board.getStone(position) != Stone.EMPTY) {
            throw new IllegalArgumentException("[ERROR] 이미 돌이 있는 위치입니다.");
        }
    }

    /**
     * 자충수 검증
     * 착수 후 자신의 그룹 활로가 0이 되는지 확인
     * 단, 상대 돌을 따내서 활로가 생기면 가능
     */
    private static void validateSuicide(Board board, Position position, Stone stone) {
        Board simulatedBoard = board.copy();
        simulatedBoard.placeStone(position, stone);

        // 먼저 상대 돌을 따낼 수 있는지 확인
        Set<Position> capturable = CaptureHandler.findCapturable(simulatedBoard, position, stone);

        // 상대를 따낼 수 있으면 자충수 아님
        if (!capturable.isEmpty()) {
            return;
        }

        // 자신의 그룹 활로 확인
        Set<Position> myGroup = StoneGroup.findConnectedGroup(simulatedBoard, position);
        int liberties = StoneGroup.countLiberties(simulatedBoard, myGroup);

        if (liberties == 0) {
            throw new IllegalArgumentException("[ERROR] 자충수는 둘 수 없습니다.");
        }
    }

    /**
     * 패 규칙 검증
     * 착수 후 바둑판이 직전 국면과 동일해지는지 확인
     */
    private static void validateKo(Board board, Position position, Stone stone, Board previousBoard) {
        if (previousBoard == null) {
            return;  // 첫 수 또는 무르기 직후
        }

        Board simulatedBoard = board.copy();
        simulatedBoard.placeStone(position, stone);

        // 상대 돌 따내기 시뮬레이션
        Set<Position> captured = CaptureHandler.findCapturable(simulatedBoard, position, stone);
        simulatedBoard.removeStones(captured);

        if (simulatedBoard.equals(previousBoard)) {
            throw new IllegalArgumentException("[ERROR] 패 규칙 위반입니다.");
        }
    }
}