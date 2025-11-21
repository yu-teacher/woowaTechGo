package com.woowa.woowago.domain.game;

import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class BoardTest {

    @Test
    void 빈_바둑판으로_초기화() {
        Board board = new Board();

        assertThat(board.getStone(new Position(1, 1))).isEqualTo(Stone.EMPTY);
        assertThat(board.getStone(new Position(10, 10))).isEqualTo(Stone.EMPTY);
        assertThat(board.getStone(new Position(19, 19))).isEqualTo(Stone.EMPTY);
    }

    @Test
    void 착수() {
        Board board = new Board();
        Position position = new Position(10, 10);

        board.placeStone(position, Stone.BLACK);

        assertThat(board.getStone(position)).isEqualTo(Stone.BLACK);
    }

    @Test
    void 돌_따냄() {
        Board board = new Board();
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(6, 6);

        board.placeStone(pos1, Stone.BLACK);
        board.placeStone(pos2, Stone.WHITE);

        board.removeStones(Set.of(pos1, pos2));

        assertThat(board.getStone(pos1)).isEqualTo(Stone.EMPTY);
        assertThat(board.getStone(pos2)).isEqualTo(Stone.EMPTY);
    }

    @Test
    void 바둑판_복사() {
        Board original = new Board();
        original.placeStone(new Position(5, 5), Stone.BLACK);

        Board copied = original.copy();
        copied.placeStone(new Position(10, 10), Stone.WHITE);

        assertThat(original.getStone(new Position(10, 10))).isEqualTo(Stone.EMPTY);
        assertThat(copied.getStone(new Position(5, 5))).isEqualTo(Stone.BLACK);
    }

    @Test
    void 같은_상태의_바둑판은_equals() {
        Board board1 = new Board();
        Board board2 = new Board();

        board1.placeStone(new Position(5, 5), Stone.BLACK);
        board2.placeStone(new Position(5, 5), Stone.BLACK);

        assertThat(board1).isEqualTo(board2);
    }

    @Test
    void 다른_상태의_바둑판은_not_equals() {
        Board board1 = new Board();
        Board board2 = new Board();

        board1.placeStone(new Position(5, 5), Stone.BLACK);
        board2.placeStone(new Position(6, 6), Stone.BLACK);

        assertThat(board1).isNotEqualTo(board2);
    }
}
