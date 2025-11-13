package com.woowa.woowago.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class MoveValidatorTest {

    @Test
    void 빈_자리에_착수_가능() {
        Board board = new Board();
        Position position = new Position(10, 10);

        assertThatNoException().isThrownBy(() ->
                MoveValidator.validate(board, position, Stone.BLACK, null)
        );
    }

    @Test
    void 이미_돌이_있는_위치는_착수_불가() {
        Board board = new Board();
        Position position = new Position(10, 10);
        board.placeStone(position, Stone.BLACK);

        assertThatThrownBy(() ->
                MoveValidator.validate(board, position, Stone.WHITE, null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 이미 돌이 있는 위치입니다.");
    }

    @Test
    void 자충수는_불가() {
        Board board = new Board();

        // 흑돌로 둘러싸인 빈 자리에 흑돌을 놓으려 함
        Position target = new Position(10, 10);
        board.placeStone(new Position(9, 10), Stone.WHITE);
        board.placeStone(new Position(11, 10), Stone.WHITE);
        board.placeStone(new Position(10, 9), Stone.WHITE);
        board.placeStone(new Position(10, 11), Stone.WHITE);

        assertThatThrownBy(() ->
                MoveValidator.validate(board, target, Stone.BLACK, null)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 자충수는 둘 수 없습니다.");
    }

    @Test
    void 상대를_따내면_자충수_아님() {
        Board board = new Board();

        // 백돌 1개를 거의 둘러쌈 (활로 1개)
        Position white = new Position(10, 10);
        board.placeStone(white, Stone.WHITE);
        board.placeStone(new Position(9, 10), Stone.BLACK);
        board.placeStone(new Position(11, 10), Stone.BLACK);
        board.placeStone(new Position(10, 9), Stone.BLACK);

        // 흑돌을 백돌 옆에 놓으면 흑도 활로 0이지만, 백을 따내므로 가능
        Position lastMove = new Position(10, 11);

        assertThatNoException().isThrownBy(() ->
                MoveValidator.validate(board, lastMove, Stone.BLACK, null)
        );
    }

    @Test
    void 패_규칙_위반() {
        Board board = new Board();

        // 패 상황 만들기
        // 흑: (10,10), 백: (11,10), (10,11), (11,12)
        board.placeStone(new Position(10, 10), Stone.BLACK);
        board.placeStone(new Position(11, 10), Stone.WHITE);
        board.placeStone(new Position(10, 11), Stone.WHITE);
        board.placeStone(new Position(11, 12), Stone.BLACK);
        board.placeStone(new Position(12, 11), Stone.BLACK);

        // 흑이 (11,11)에 놓아서 백 따냄
        Position koPosition = new Position(11, 11);
        board.placeStone(koPosition, Stone.BLACK);

        // 이 상태를 previousBoard로 저장
        Board previousBoard = board.copy();

        // 백을 제거 (따내기 시뮬레이션)
        board.removeStones(Set.of(new Position(11, 10)));

        // 백이 바로 (11,10)에 다시 놓으려 함 (패 규칙 위반)
        assertThatThrownBy(() ->
                MoveValidator.validate(board, new Position(11, 10), Stone.WHITE, previousBoard)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 패 규칙 위반입니다.");
    }

    @Test
    void previousBoard가_null이면_패_규칙_검증_안함() {
        Board board = new Board();
        Position position = new Position(10, 10);

        assertThatNoException().isThrownBy(() ->
                MoveValidator.validate(board, position, Stone.BLACK, null)
        );
    }
}