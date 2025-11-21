package com.woowa.woowago.domain.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StoneTest {

    @Test
    void BLACK의_반대는_WHITE() {
        assertThat(Stone.BLACK.opposite()).isEqualTo(Stone.WHITE);
    }

    @Test
    void WHITE의_반대는_BLACK() {
        assertThat(Stone.WHITE.opposite()).isEqualTo(Stone.BLACK);
    }

    @Test
    void EMPTY는_반대_색상이_없음() {
        assertThatThrownBy(Stone.EMPTY::opposite)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[ERROR] EMPTY는 반대 색상이 없습니다.");
    }
}
