package com.woowa.woowago.domain.move;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MoveTest {

    @Test
    void Move_생성() {
        Position position = new Position(10, 10);

        Move move = new Move(1, position, Stone.BLACK);

        assertThat(move.getMoveNumber()).isEqualTo(1);
        assertThat(move.getPosition()).isEqualTo(position);
        assertThat(move.getColor()).isEqualTo(Stone.BLACK);
    }
}
