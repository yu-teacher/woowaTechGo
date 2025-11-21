package com.woowa.woowago.domain;

import com.woowa.woowago.domain.capture.CaptureHandler;
import com.woowa.woowago.domain.game.Board;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CaptureHandlerTest {

    @Test
    void 단일_돌_따내기() {
        Board board = new Board();

        // 백돌을 흑돌로 둘러싸기 (활로 1개 남김)
        Position white = new Position(10, 10);
        board.placeStone(white, Stone.WHITE);
        board.placeStone(new Position(9, 10), Stone.BLACK);
        board.placeStone(new Position(11, 10), Stone.BLACK);
        board.placeStone(new Position(10, 9), Stone.BLACK);

        // 마지막 활로에 흑돌 놓기
        Position lastMove = new Position(10, 11);
        board.placeStone(lastMove, Stone.BLACK);

        Set<Position> captured = CaptureHandler.execute(board, lastMove, Stone.BLACK);

        assertThat(captured).containsExactly(white);
        assertThat(board.getStone(white)).isEqualTo(Stone.EMPTY);
    }

    @Test
    void 그룹_따내기() {
        Board board = new Board();

        // 백돌 2개 그룹
        Position white1 = new Position(10, 10);
        Position white2 = new Position(11, 10);
        board.placeStone(white1, Stone.WHITE);
        board.placeStone(white2, Stone.WHITE);

        // 흑돌로 둘러싸기 (활로 1개 남김)
        board.placeStone(new Position(9, 10), Stone.BLACK);
        board.placeStone(new Position(12, 10), Stone.BLACK);
        board.placeStone(new Position(10, 9), Stone.BLACK);
        board.placeStone(new Position(11, 9), Stone.BLACK);
        board.placeStone(new Position(10, 11), Stone.BLACK);

        // 마지막 활로에 흑돌 놓기
        Position lastMove = new Position(11, 11);
        board.placeStone(lastMove, Stone.BLACK);

        Set<Position> captured = CaptureHandler.execute(board, lastMove, Stone.BLACK);

        assertThat(captured).containsExactlyInAnyOrder(white1, white2);
        assertThat(board.getStone(white1)).isEqualTo(Stone.EMPTY);
        assertThat(board.getStone(white2)).isEqualTo(Stone.EMPTY);
    }

    @Test
    void 여러_그룹_동시_따내기() {
        Board board = new Board();

        // 첫 번째 백돌
        Position white1 = new Position(10, 10);
        board.placeStone(white1, Stone.WHITE);
        board.placeStone(new Position(9, 10), Stone.BLACK);
        board.placeStone(new Position(10, 9), Stone.BLACK);
        board.placeStone(new Position(10, 11), Stone.BLACK);

        // 두 번째 백돌 (분리된 그룹)
        Position white2 = new Position(12, 10);
        board.placeStone(white2, Stone.WHITE);
        board.placeStone(new Position(13, 10), Stone.BLACK);
        board.placeStone(new Position(12, 9), Stone.BLACK);
        board.placeStone(new Position(12, 11), Stone.BLACK);

        // 중간에 흑돌 놓아서 두 그룹 동시에 따내기
        Position lastMove = new Position(11, 10);
        board.placeStone(lastMove, Stone.BLACK);

        Set<Position> captured = CaptureHandler.execute(board, lastMove, Stone.BLACK);

        assertThat(captured).containsExactlyInAnyOrder(white1, white2);
    }

    @Test
    void 따낼_돌이_없으면_빈_Set_반환() {
        Board board = new Board();
        Position position = new Position(10, 10);

        board.placeStone(position, Stone.BLACK);

        Set<Position> captured = CaptureHandler.execute(board, position, Stone.BLACK);

        assertThat(captured).isEmpty();
    }

    @Test
    void findCapturable은_제거하지_않음() {
        Board board = new Board();

        Position white = new Position(10, 10);
        board.placeStone(white, Stone.WHITE);
        board.placeStone(new Position(9, 10), Stone.BLACK);
        board.placeStone(new Position(11, 10), Stone.BLACK);
        board.placeStone(new Position(10, 9), Stone.BLACK);
        board.placeStone(new Position(10, 11), Stone.BLACK);

        Set<Position> capturable = CaptureHandler.findCapturable(board, new Position(10, 11), Stone.BLACK);

        assertThat(capturable).containsExactly(white);
        assertThat(board.getStone(white)).isEqualTo(Stone.WHITE);  // 제거되지 않음
    }
}