package com.woowa.woowago.domain;

import com.woowa.woowago.domain.capture.StoneGroup;
import com.woowa.woowago.domain.game.Board;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class StoneGroupTest {

    @Test
    void 빈_자리는_그룹이_없음() {
        Board board = new Board();

        Set<Position> group = StoneGroup.findConnectedGroup(board, new Position(10, 10));

        assertThat(group).isEmpty();
    }

    @Test
    void 단일_돌_그룹() {
        Board board = new Board();
        Position position = new Position(10, 10);
        board.placeStone(position, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, position);

        assertThat(group).containsExactly(position);
    }

    @Test
    void 연결된_돌_그룹_찾기() {
        Board board = new Board();
        Position pos1 = new Position(10, 10);
        Position pos2 = new Position(11, 10);
        Position pos3 = new Position(10, 11);

        board.placeStone(pos1, Stone.BLACK);
        board.placeStone(pos2, Stone.BLACK);
        board.placeStone(pos3, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, pos1);

        assertThat(group).containsExactlyInAnyOrder(pos1, pos2, pos3);
    }

    @Test
    void 다른_색_돌은_그룹에_포함_안됨() {
        Board board = new Board();
        Position black1 = new Position(10, 10);
        Position white = new Position(11, 10);
        Position black2 = new Position(12, 10);

        board.placeStone(black1, Stone.BLACK);
        board.placeStone(white, Stone.WHITE);
        board.placeStone(black2, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, black1);

        assertThat(group).containsExactly(black1);
    }

    @Test
    void 단일_돌의_활로는_4개() {
        Board board = new Board();
        Position position = new Position(10, 10);
        board.placeStone(position, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, position);
        int liberties = StoneGroup.countLiberties(board, group);

        assertThat(liberties).isEqualTo(4);
    }

    @Test
    void 모서리_돌의_활로는_2개() {
        Board board = new Board();
        Position position = new Position(1, 1);
        board.placeStone(position, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, position);
        int liberties = StoneGroup.countLiberties(board, group);

        assertThat(liberties).isEqualTo(2);
    }

    @Test
    void 그룹의_활로_계산() {
        Board board = new Board();
        Position pos1 = new Position(10, 10);
        Position pos2 = new Position(11, 10);

        board.placeStone(pos1, Stone.BLACK);
        board.placeStone(pos2, Stone.BLACK);

        Set<Position> group = StoneGroup.findConnectedGroup(board, pos1);
        int liberties = StoneGroup.countLiberties(board, group);

        assertThat(liberties).isEqualTo(6);  // 2개 돌의 공유 활로 제외
    }
}