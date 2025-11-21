package com.woowa.woowago.domain;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import com.woowa.woowago.domain.move.Move;
import com.woowa.woowago.domain.move.MoveHistory;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MoveHistoryTest {

    @Test
    void 초기_상태는_비어있음() {
        MoveHistory history = new MoveHistory();

        assertThat(history.isEmpty()).isTrue();
        assertThat(history.size()).isZero();
    }

    @Test
    void 착수_추가() {
        MoveHistory history = new MoveHistory();
        Move move = new Move(1, new Position(10, 10), Stone.BLACK);

        history.add(move);

        assertThat(history.isEmpty()).isFalse();
        assertThat(history.size()).isEqualTo(1);
    }

    @Test
    void 마지막_착수_조회() {
        MoveHistory history = new MoveHistory();
        Move move1 = new Move(1, new Position(10, 10), Stone.BLACK);
        Move move2 = new Move(2, new Position(11, 11), Stone.WHITE);

        history.add(move1);
        history.add(move2);

        assertThat(history.getLast()).isEqualTo(move2);
    }

    @Test
    void 마지막_착수_제거() {
        MoveHistory history = new MoveHistory();
        Move move = new Move(1, new Position(10, 10), Stone.BLACK);
        history.add(move);

        Move removed = history.removeLast();

        assertThat(removed).isEqualTo(move);
        assertThat(history.isEmpty()).isTrue();
    }

    @Test
    void 빈_기록에서_제거_시도_시_예외() {
        MoveHistory history = new MoveHistory();

        assertThatThrownBy(history::removeLast)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] 무를 수 없습니다.");
    }

    @Test
    void 빈_기록에서_조회_시도_시_예외() {
        MoveHistory history = new MoveHistory();

        assertThatThrownBy(history::getLast)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] 착수 기록이 없습니다.");
    }

    @Test
    void 전체_기록_조회() {
        MoveHistory history = new MoveHistory();
        Move move1 = new Move(1, new Position(10, 10), Stone.BLACK);
        Move move2 = new Move(2, new Position(11, 11), Stone.WHITE);

        history.add(move1);
        history.add(move2);

        List<Move> all = history.getAll();

        assertThat(all).hasSize(2);
        assertThat(all).containsExactly(move1, move2);
    }

    @Test
    void 반환된_리스트는_불변() {
        MoveHistory history = new MoveHistory();
        Move move = new Move(1, new Position(10, 10), Stone.BLACK);
        history.add(move);

        List<Move> all = history.getAll();

        assertThatThrownBy(() -> all.add(new Move(2, new Position(11, 11), Stone.WHITE)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}