package com.woowa.woowago.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Position {
    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 19;

    private final int x;
    private final int y;

    public Position(int x, int y) {
        validateCoordinate(x);
        validateCoordinate(y);
        this.x = x;
        this.y = y;
    }

    private void validateCoordinate(int coordinate) {
        if (coordinate < MIN_COORDINATE || coordinate > MAX_COORDINATE) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 좌표입니다.");
        }
    }

    public List<Position> getAdjacentPositions() {
        List<Position> adjacents = new ArrayList<>();

        tryAddPosition(adjacents, x - 1, y);  // 상
        tryAddPosition(adjacents, x + 1, y);  // 하
        tryAddPosition(adjacents, x, y - 1);  // 좌
        tryAddPosition(adjacents, x, y + 1);  // 우

        return adjacents;
    }

    private void tryAddPosition(List<Position> positions, int x, int y) {
        if (isValidCoordinate(x) && isValidCoordinate(y)) {
            positions.add(new Position(x, y));
        }
    }

    private boolean isValidCoordinate(int coordinate) {
        return coordinate >= MIN_COORDINATE && coordinate <= MAX_COORDINATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
