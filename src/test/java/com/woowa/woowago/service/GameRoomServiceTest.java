package com.woowa.woowago.service;

import com.woowa.woowago.domain.GameRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameRoomServiceTest {

    private GameRoomService service;

    @BeforeEach
    void setUp() {
        service = new GameRoomService();
    }

    @Test
    void 방_생성() {
        GameRoom room = service.createOrGetRoom("room1");

        assertThat(room).isNotNull();
        assertThat(room.getRoomId()).isEqualTo("room1");
    }

    @Test
    void 같은_ID로_조회시_동일한_방_반환() {
        GameRoom room1 = service.createOrGetRoom("room1");
        GameRoom room2 = service.createOrGetRoom("room1");

        assertThat(room1).isSameAs(room2);
    }

    @Test
    void 방_입장_시_역할_자동_배정() {
        String role1 = service.joinRoom("room1", "user1");
        String role2 = service.joinRoom("room1", "user2");
        String role3 = service.joinRoom("room1", "user3");

        assertThat(role1).isEqualTo("player1");
        assertThat(role2).isEqualTo("player2");
        assertThat(role3).isEqualTo("spectator");
    }

    @Test
    void 방_퇴장() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");

        service.leaveRoom("room1", "user1");
        GameRoom room = service.getRoom("room1");

        assertThat(room.getPlayer1()).isNull();
        assertThat(room.getPlayer2()).isEqualTo("user2");
    }

    @Test
    void 모든_사용자_퇴장_시_방_자동_삭제() {
        service.joinRoom("room1", "user1");

        service.leaveRoom("room1", "user1");

        assertThat(service.getRoom("room1")).isNull();
    }

    @Test
    void 존재하지_않는_방_퇴장_시_예외_없음() {
        assertThatNoException().isThrownBy(() ->
                service.leaveRoom("unknown-room", "user1")
        );
    }

    @Test
    void 여러_방_동시_관리() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room2", "user2");

        GameRoom room1 = service.getRoom("room1");
        GameRoom room2 = service.getRoom("room2");

        assertThat(room1.getPlayer1()).isEqualTo("user1");
        assertThat(room2.getPlayer1()).isEqualTo("user2");
    }
}