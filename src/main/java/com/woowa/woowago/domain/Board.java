package com.woowa.woowago.domain;

import java.util.Arrays;
import java.util.Set;

public class Board {
    private static final int BOARD_SIZE = 19;

    private final Stone[][] stones;

    public Board() {
        this.stones = new Stone[BOARD_SIZE][BOARD_SIZE];
        initializeEmpty();
    }

    private Board(Stone[][] stones) {
        this.stones = stones;
    }

    private void initializeEmpty() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(stones[i], Stone.EMPTY);
        }
    }

    public Stone getStone(Position position) {
        return stones[position.getX() - 1][position.getY() - 1];
    }


    public void placeStone(Position position, Stone stone) {
        stones[position.getX() - 1][position.getY() - 1] = stone;
    }

    public void removeStones(Set<Position> positions) {
        for (Position position : positions) {
            placeStone(position, Stone.EMPTY);
        }
    }

    /**
     * 현재 바둑판을 복사하여 새로운 Board 객체 생성
     * 패 규칙 검증을 위해 사용
     * @return 복사된 Board 객체
     */
    public Board copy() {
        Stone[][] copiedStones = new Stone[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            copiedStones[i] = Arrays.copyOf(stones[i], BOARD_SIZE);
        }
        return new Board(copiedStones);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.deepEquals(stones, board.stones);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(stones);
    }
}
