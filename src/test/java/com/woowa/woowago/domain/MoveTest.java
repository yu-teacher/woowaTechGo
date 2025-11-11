package com.woowa.woowago.domain;

import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class MoveTest {

    @Test
    void Move_생성() {
        Position position = new Position(10, 10);
        Set<Position> captured = Set.of(new Position(11, 10));

        Move move = new Move(1, position, Stone.BLACK, captured);

        assertThat(move.getMoveNumber()).isEqualTo(1);
        assertThat(move.getPosition()).isEqualTo(position);
        assertThat(move.getColor()).isEqualTo(Stone.BLACK);
        assertThat(move.getCapturedPositions()).isEqualTo(captured);
    }

    @Test
    void 따낸_돌이_없는_Move() {
        Position position = new Position(5, 5);

        Move move = new Move(1, position, Stone.BLACK, Set.of());

        assertThat(move.getCapturedPositions()).isEmpty();
    }

    @Test
    void capturedPositions는_불변() {
        Position position = new Position(10, 10);
        Set<Position> captured = Set.of(new Position(11, 10));

        Move move = new Move(1, position, Stone.BLACK, captured);

        assertThatThrownBy(() -> move.getCapturedPositions().add(new Position(12, 10)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
