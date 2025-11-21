package com.woowa.woowago.domain.room;

import com.woowa.woowago.domain.game.Position;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameRoomTest {

    @Test
    void 방_생성시_초기_상태_확인() {
        GameRoom room = new GameRoom("test-room");

        assertThat(room.getRoomId()).isEqualTo("test-room");
        assertThat(room.getPlayer1()).isNull();
        assertThat(room.getPlayer2()).isNull();
        assertThat(room.getSpectators()).isEmpty();
        assertThat(room.getGame()).isNotNull();
    }

    @Test
    void 사용자_추가_시_선착순_역할_배정() {
        GameRoom room = new GameRoom("test-room");

        String role1 = room.addUser("user1");
        String role2 = room.addUser("user2");
        String role3 = room.addUser("user3");

        assertThat(role1).isEqualTo("player1");
        assertThat(role2).isEqualTo("player2");
        assertThat(role3).isEqualTo("spectator");
        assertThat(room.getSpectators()).contains("user3");
    }

    @Test
    void 역할_조회() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        assertThat(room.getRole("user1")).isEqualTo("player1");
        assertThat(room.getRole("user2")).isEqualTo("player2");
        assertThat(room.getRole("user3")).isEqualTo("spectator");
        assertThat(room.getRole("unknown")).isNull();
    }

    @Test
    void 사용자_제거() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        room.removeUser("user1");
        room.removeUser("user3");

        assertThat(room.getPlayer1()).isNull();
        assertThat(room.getPlayer2()).isEqualTo("user2");
        assertThat(room.getSpectators()).isEmpty();
    }

    @Test
    void 각_방은_독립적인_게임_인스턴스를_가짐() {
        GameRoom room1 = new GameRoom("room1");
        GameRoom room2 = new GameRoom("room2");

        room1.getGame().move(new Position(10, 10));

        assertThat(room1.getGame().getMoveHistory()).hasSize(1);
        assertThat(room2.getGame().getMoveHistory()).isEmpty();
    }
}