package com.woowa.woowago.domain;

import com.woowa.woowago.domain.game.Game;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameTest {

    @Test
    void 게임_초기_상태() {
        Game game = new Game();

        assertThat(game.getState().getCurrentTurn()).isEqualTo(Stone.BLACK);
        assertThat(game.getState().getBlackCaptures()).isZero();
        assertThat(game.getState().getWhiteCaptures()).isZero();
        assertThat(game.getMoveHistory()).isEmpty();
    }

    @Test
    void 정상_착수() {
        Game game = new Game();
        Position position = new Position(10, 10);

        game.move(position);

        assertThat(game.getState().getBoard().getStone(position)).isEqualTo(Stone.BLACK);
        assertThat(game.getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
        assertThat(game.getMoveHistory()).hasSize(1);
    }

    @Test
    void 흑백_교대_착수() {
        Game game = new Game();

        game.move(new Position(10, 10));
        game.move(new Position(11, 11));
        game.move(new Position(12, 12));

        assertThat(game.getState().getBoard().getStone(new Position(10, 10))).isEqualTo(Stone.BLACK);
        assertThat(game.getState().getBoard().getStone(new Position(11, 11))).isEqualTo(Stone.WHITE);
        assertThat(game.getState().getBoard().getStone(new Position(12, 12))).isEqualTo(Stone.BLACK);
        assertThat(game.getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
    }

    @Test
    void 이미_돌이_있는_위치_착수_불가() {
        Game game = new Game();
        Position position = new Position(10, 10);

        game.move(position);

        assertThatThrownBy(() -> game.move(position))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 이미 돌이 있는 위치입니다.");
    }

    @Test
    void 따내기_시나리오() {
        Game game = new Game();

        // 백돌 1개를 흑돌로 둘러싸기
        game.move(new Position(10, 9));   // 흑
        game.move(new Position(10, 10));  // 백
        game.move(new Position(9, 10));   // 흑
        game.move(new Position(11, 11));  // 백 (다른 곳)
        game.move(new Position(11, 10));  // 흑
        game.move(new Position(12, 12));  // 백 (다른 곳)
        game.move(new Position(10, 11));  // 흑 - 백 따냄

        assertThat(game.getState().getBoard().getStone(new Position(10, 10))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getBlackCaptures()).isEqualTo(1);
        assertThat(game.getState().getWhiteCaptures()).isZero();
    }

    @Test
    void 자충수_불가() {
        Game game = new Game();

        // 흑돌로 빈 자리 둘러싸기
        game.move(new Position(9, 10));   // 흑
        game.move(new Position(11, 11));  // 백 (다른 곳)
        game.move(new Position(11, 10));  // 흑
        game.move(new Position(12, 12));  // 백 (다른 곳)
        game.move(new Position(10, 9));   // 흑
        game.move(new Position(13, 13));  // 백 (다른 곳)
        game.move(new Position(10, 11));  // 흑

        // 백이 둘러싸인 곳에 착수 시도 (자충수)
        assertThatThrownBy(() -> game.move(new Position(10, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 자충수는 둘 수 없습니다.");
    }

    @Test
    void 패_규칙_위반() {
        Game game = new Game();

        // 패 상황 만들기
        game.move(new Position(10, 10));  // 흑
        game.move(new Position(11, 10));  // 백
        game.move(new Position(11, 11));  // 흑
        game.move(new Position(10, 11));  // 백
        game.move(new Position(11, 9));  // 흑
        game.move(new Position(12, 11));  // 백
        game.move(new Position(12, 10));  // 흑
        game.move(new Position(11, 12));  // 백

        game.move(new Position(1, 1));  // 흑

        // 백이 (11,10)에 놓아서 백 (11,11) 따냄
        game.move(new Position(11, 10));  // 백

        // 백이 바로 (11,11)에 다시 놓으려 함 (패 규칙 위반)
        assertThatThrownBy(() -> game.move(new Position(11, 11)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 패 규칙 위반입니다.");
    }

    @Test
    void 무르기_성공() {
        Game game = new Game();

        game.move(new Position(10, 10));  // 흑
        game.move(new Position(11, 11));  // 백

        game.undo();

        assertThat(game.getState().getBoard().getStone(new Position(11, 11))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
        assertThat(game.getMoveHistory()).hasSize(1);
    }

    @Test
    void 무르기_후_따낸_돌_복원() {
        Game game = new Game();

        // 백돌 따내기 시나리오
        game.move(new Position(10, 9));   // 흑
        game.move(new Position(10, 10));  // 백
        game.move(new Position(9, 10));   // 흑
        game.move(new Position(11, 11));  // 백
        game.move(new Position(11, 10));  // 흑
        game.move(new Position(12, 12));  // 백
        game.move(new Position(10, 11));  // 흑 - 백 따냄

        assertThat(game.getState().getBlackCaptures()).isEqualTo(1);

        game.undo();

        assertThat(game.getState().getBoard().getStone(new Position(10, 10))).isEqualTo(Stone.WHITE);
        assertThat(game.getState().getBoard().getStone(new Position(10, 11))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getBlackCaptures()).isZero();
    }

    @Test
    void 무르기_여러_번() {
        Game game = new Game();

        game.move(new Position(10, 10));  // 흑
        game.move(new Position(11, 11));  // 백
        game.move(new Position(12, 12));  // 흑

        game.undo();  // 12,12 취소
        game.undo();  // 11,11 취소

        assertThat(game.getState().getBoard().getStone(new Position(11, 11))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getBoard().getStone(new Position(12, 12))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
        assertThat(game.getMoveHistory()).hasSize(1);
    }

    @Test
    void 첫_수에서_무르기_불가() {
        Game game = new Game();

        assertThatThrownBy(game::undo)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] 무를 수 없습니다.");
    }

    @Test
    void 무르기_후_다시_착수_가능() {
        Game game = new Game();

        game.move(new Position(10, 10));  // 흑
        game.undo();
        game.move(new Position(11, 11));  // 흑 (다른 위치)

        assertThat(game.getState().getBoard().getStone(new Position(10, 10))).isEqualTo(Stone.EMPTY);
        assertThat(game.getState().getBoard().getStone(new Position(11, 11))).isEqualTo(Stone.BLACK);
    }

    @Test
    void Move_기록_확인() {
        Game game = new Game();

        game.move(new Position(10, 10));
        game.move(new Position(11, 11));

        assertThat(game.getMoveHistory()).hasSize(2);
        assertThat(game.getMoveHistory().get(0).getMoveNumber()).isEqualTo(1);
        assertThat(game.getMoveHistory().get(0).getColor()).isEqualTo(Stone.BLACK);
        assertThat(game.getMoveHistory().get(1).getMoveNumber()).isEqualTo(2);
        assertThat(game.getMoveHistory().get(1).getColor()).isEqualTo(Stone.WHITE);
    }
}
