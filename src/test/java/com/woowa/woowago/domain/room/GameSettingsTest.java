package com.woowa.woowago.domain.room;

import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameSettingsTest {

    @Test
    void 흑백_랜덤_배정() {
        GameSettings settings = new GameSettings();

        settings.assignColors("user1", "user2");

        assertThat(settings.isAssigned()).isTrue();
        assertThat(settings.getBlackPlayer()).isIn("user1", "user2");
        assertThat(settings.getWhitePlayer()).isIn("user1", "user2");
        assertThat(settings.getBlackPlayer()).isNotEqualTo(settings.getWhitePlayer());
    }

    @Test
    void 중복_배정_불가() {
        GameSettings settings = new GameSettings();
        settings.assignColors("user1", "user2");

        assertThatThrownBy(() -> settings.assignColors("user1", "user2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 색상이 배정되었습니다");
    }

    @Test
    void null_참가자_배정_불가() {
        GameSettings settings = new GameSettings();

        assertThatThrownBy(() -> settings.assignColors(null, "user2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자가 2명이어야 합니다");
    }

    @Test
    void 같은_사용자_배정_불가() {
        GameSettings settings = new GameSettings();

        assertThatThrownBy(() -> settings.assignColors("user1", "user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 사용자는 배정할 수 없습니다");
    }

    @Test
    void 흑돌_차례_확인() {
        GameSettings settings = new GameSettings();
        settings.assignColors("user1", "user2");

        String blackPlayer = settings.getBlackPlayer();

        assertThat(settings.isMyTurn(blackPlayer, Stone.BLACK)).isTrue();
        assertThat(settings.isMyTurn(settings.getWhitePlayer(), Stone.BLACK)).isFalse();
    }

    @Test
    void 백돌_차례_확인() {
        GameSettings settings = new GameSettings();
        settings.assignColors("user1", "user2");

        String whitePlayer = settings.getWhitePlayer();

        assertThat(settings.isMyTurn(whitePlayer, Stone.WHITE)).isTrue();
        assertThat(settings.isMyTurn(settings.getBlackPlayer(), Stone.WHITE)).isFalse();
    }

    @Test
    void 배정_전_차례_확인_불가() {
        GameSettings settings = new GameSettings();

        assertThatThrownBy(() -> settings.isMyTurn("user1", Stone.BLACK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("색상이 배정되지 않았습니다");
    }

    @Test
    void 차례_검증_실패() {
        GameSettings settings = new GameSettings();
        settings.assignColors("user1", "user2");

        String whitePlayer = settings.getWhitePlayer();

        assertThatThrownBy(() -> settings.validateMyTurn(whitePlayer, Stone.BLACK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("흑의 차례입니다");
    }
}