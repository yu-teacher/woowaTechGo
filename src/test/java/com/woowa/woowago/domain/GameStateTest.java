package com.woowa.woowago.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameStateTest {

    @Test
    void 초기_상태_생성() {
        GameState state = new GameState();

        assertThat(state.getBoard()).isNotNull();
        assertThat(state.getCurrentTurn()).isEqualTo(Stone.BLACK);
        assertThat(state.getBlackCaptures()).isZero();
        assertThat(state.getWhiteCaptures()).isZero();
    }

    @Test
    void 현재_차례_변경() {
        GameState state = new GameState();

        state.setCurrentTurn(Stone.WHITE);

        assertThat(state.getCurrentTurn()).isEqualTo(Stone.WHITE);
    }

    @Test
    void 흑이_백을_따냄() {
        GameState state = new GameState();

        state.addCaptures(Stone.WHITE, 3);

        assertThat(state.getBlackCaptures()).isEqualTo(3);
        assertThat(state.getWhiteCaptures()).isZero();
    }

    @Test
    void 백이_흑을_따냄() {
        GameState state = new GameState();

        state.addCaptures(Stone.BLACK, 2);

        assertThat(state.getWhiteCaptures()).isEqualTo(2);
        assertThat(state.getBlackCaptures()).isZero();
    }

    @Test
    void 따낸_돌_누적() {
        GameState state = new GameState();

        state.addCaptures(Stone.WHITE, 2);
        state.addCaptures(Stone.WHITE, 3);

        assertThat(state.getBlackCaptures()).isEqualTo(5);
    }
}
