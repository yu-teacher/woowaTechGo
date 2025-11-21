package com.woowa.woowago.domain.room;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ParticipantsTest {

    @Test
    void 선착순_역할_배정() {
        Participants participants = new Participants();

        Participant p1 = participants.add("user1");
        Participant p2 = participants.add("user2");
        Participant p3 = participants.add("user3");

        assertThat(p1.getRole()).isEqualTo(ParticipantRole.PLAYER1);
        assertThat(p2.getRole()).isEqualTo(ParticipantRole.PLAYER2);
        assertThat(p3.getRole()).isEqualTo(ParticipantRole.SPECTATOR);
    }

    @Test
    void 중복_사용자_추가_불가() {
        Participants participants = new Participants();
        participants.add("user1");

        assertThatThrownBy(() -> participants.add("user1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 사용자_제거() {
        Participants participants = new Participants();
        participants.add("user1");
        participants.add("user2");

        participants.remove("user1");

        assertThat(participants.getRole("user1")).isNull();
    }

    @Test
    void 두명_모이면_준비완료() {
        Participants participants = new Participants();
        participants.add("user1");
        assertThat(participants.isReady()).isFalse();

        participants.add("user2");
        assertThat(participants.isReady()).isTrue();
    }

    @Test
    void 참가자_권한_검증_성공() {
        Participants participants = new Participants();
        participants.add("user1");

        assertThatCode(() -> participants.validatePlayerPermission("user1"))
                .doesNotThrowAnyException();
    }

    @Test
    void 관전자_권한_검증_실패() {
        Participants participants = new Participants();
        participants.add("user1");
        participants.add("user2");
        participants.add("user3");

        assertThatThrownBy(() -> participants.validatePlayerPermission("user3"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 관전자_여러명_관리() {
        Participants participants = new Participants();
        participants.add("user1");
        participants.add("user2");
        participants.add("user3");
        participants.add("user4");

        assertThat(participants.getSpectatorCount()).isEqualTo(2);
    }
}