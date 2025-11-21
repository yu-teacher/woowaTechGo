package com.woowa.woowago.domain;

import com.woowa.woowago.domain.game.Position;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PositionTest {

    @Test
    void 유효한_좌표로_Position_생성() {
        assertThatNoException().isThrownBy(() -> new Position(1, 1));
        assertThatNoException().isThrownBy(() -> new Position(19, 19));
        assertThatNoException().isThrownBy(() -> new Position(10, 10));
    }

    @Test
    void 좌표가_범위를_벗어나면_예외_발생() {
        assertThatThrownBy(() -> new Position(0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 유효하지 않은 좌표입니다.");

        assertThatThrownBy(() -> new Position(1, 20))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 중앙_좌표의_인접_좌표는_4개() {
        Position center = new Position(10, 10);
        List<Position> adjacents = center.getAdjacentPositions();

        assertThat(adjacents).hasSize(4);
        assertThat(adjacents).containsExactlyInAnyOrder(
                new Position(9, 10),
                new Position(11, 10),
                new Position(10, 9),
                new Position(10, 11)
        );
    }

    @Test
    void 모서리_좌표의_인접_좌표는_2개() {
        Position corner = new Position(1, 1);
        List<Position> adjacents = corner.getAdjacentPositions();

        assertThat(adjacents).hasSize(2);
        assertThat(adjacents).containsExactlyInAnyOrder(
                new Position(2, 1),
                new Position(1, 2)
        );
    }

    @Test
    void 같은_좌표는_equals로_동일() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);

        assertThat(pos1).isEqualTo(pos2);
        assertThat(pos1.hashCode()).isEqualTo(pos2.hashCode());
    }
}