package com.woowa.woowago.domain.room;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ParticipantTest {

    @Test
    void 참가자_생성() {
        Participant participant = new Participant("user1", ParticipantRole.PLAYER1);

        assertThat(participant.getUsername()).isEqualTo("user1");
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.PLAYER1);
    }

    @Test
    void 사용자명이_null이면_예외() {
        assertThatThrownBy(() -> new Participant(null, ParticipantRole.PLAYER1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자명은 비어있을 수 없습니다");
    }

    @Test
    void 사용자명이_빈문자열이면_예외() {
        assertThatThrownBy(() -> new Participant("", ParticipantRole.PLAYER1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자명은 비어있을 수 없습니다");
    }

    @Test
    void 사용자명이_공백이면_예외() {
        assertThatThrownBy(() -> new Participant("   ", ParticipantRole.PLAYER1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자명은 비어있을 수 없습니다");
    }

    @Test
    void PLAYER1은_참가자() {
        Participant participant = new Participant("user1", ParticipantRole.PLAYER1);

        assertThat(participant.isPlayer()).isTrue();
        assertThat(participant.isSpectator()).isFalse();
    }

    @Test
    void PLAYER2는_참가자() {
        Participant participant = new Participant("user2", ParticipantRole.PLAYER2);

        assertThat(participant.isPlayer()).isTrue();
        assertThat(participant.isSpectator()).isFalse();
    }

    @Test
    void SPECTATOR는_관전자() {
        Participant participant = new Participant("user3", ParticipantRole.SPECTATOR);

        assertThat(participant.isPlayer()).isFalse();
        assertThat(participant.isSpectator()).isTrue();
    }

    @Test
    void 같은_username이면_동일한_객체() {
        Participant p1 = new Participant("user1", ParticipantRole.PLAYER1);
        Participant p2 = new Participant("user1", ParticipantRole.PLAYER2);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void 다른_username이면_다른_객체() {
        Participant p1 = new Participant("user1", ParticipantRole.PLAYER1);
        Participant p2 = new Participant("user2", ParticipantRole.PLAYER1);

        assertThat(p1).isNotEqualTo(p2);
    }
}