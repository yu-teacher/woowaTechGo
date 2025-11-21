package com.woowa.woowago.service;

import com.woowa.woowago.domain.move.Move;
import com.woowa.woowago.domain.game.Stone;
import com.woowa.woowago.dto.GameStateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    void 새_게임_시작() {
        gameService.startNewGame();

        assertThat(gameService.getGame().getState().getCurrentTurn()).isEqualTo(Stone.BLACK);
        assertThat(gameService.getGame().getMoveHistory()).isEmpty();
    }

    @Test
    void 착수_처리() {
        gameService.move(10, 10);

        assertThat(gameService.getGame().getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
        assertThat(gameService.getGame().getMoveHistory()).hasSize(1);
    }

    @Test
    void 무르기_처리() {
        gameService.move(10, 10);
        gameService.move(11, 11);

        gameService.undo();

        assertThat(gameService.getGame().getState().getCurrentTurn()).isEqualTo(Stone.WHITE);
        assertThat(gameService.getGame().getMoveHistory()).hasSize(1);
    }

    @Test
    void 유효하지_않은_좌표_예외() {
        assertThatThrownBy(() -> gameService.move(0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 유효하지 않은 좌표입니다.");
    }

    @Test
    void 게임_상태_조회() {
        gameService.move(10, 10);
        gameService.move(11, 11);

        GameStateResponse response = gameService.getGameState();

        assertThat(response.getCurrentTurn()).isEqualTo("BLACK");
        assertThat(response.getMoveCount()).isEqualTo(2);
        assertThat(response.getBlackCaptures()).isZero();
        assertThat(response.getWhiteCaptures()).isZero();
        assertThat(response.getBoard()[9][9]).isEqualTo(Stone.BLACK);  // (10,10)
        assertThat(response.getBoard()[10][10]).isEqualTo(Stone.WHITE);  // (11,11)
    }

    @Test
    void 착수_기록_조회() {
        gameService.move(10, 10);
        gameService.move(11, 11);

        List<Move> history = gameService.getMoveHistory();

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getMoveNumber()).isEqualTo(1);
        assertThat(history.get(0).getColor()).isEqualTo(Stone.BLACK);
        assertThat(history.get(1).getMoveNumber()).isEqualTo(2);
        assertThat(history.get(1).getColor()).isEqualTo(Stone.WHITE);
    }
}