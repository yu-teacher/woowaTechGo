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
        assertThat(room.isGameStarted()).isFalse();
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

    @Test
    void 게임_준비_완료_확인() {
        GameRoom room = new GameRoom("test-room");

        assertThat(room.isReady()).isFalse();

        room.addUser("user1");
        assertThat(room.isReady()).isFalse();

        room.addUser("user2");
        assertThat(room.isReady()).isTrue();
    }

    @Test
    void 게임_시작() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.start("user1");

        assertThat(room.isGameStarted()).isTrue();
        assertThat(room.getSettings()).isNotNull();
        assertThat(room.getSettings().isAssigned()).isTrue();
    }

    @Test
    void 참가자가_2명_아니면_시작_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");

        assertThatThrownBy(() -> room.start("user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자가 2명이어야");
    }

    @Test
    void 관전자는_게임_시작_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        assertThatThrownBy(() -> room.start("user3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void 착수_성공() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.start("user1");

        String blackPlayer = room.getSettings().getBlackPlayer();

        assertThatCode(() -> room.move(blackPlayer, new Position(10, 10)))
                .doesNotThrowAnyException();
    }

    @Test
    void 관전자는_착수_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");
        room.start("user1");

        assertThatThrownBy(() -> room.move("user3", new Position(10, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void 차례가_아니면_착수_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.start("user1");

        String whitePlayer = room.getSettings().getWhitePlayer();

        assertThatThrownBy(() -> room.move(whitePlayer, new Position(10, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("차례입니다");
    }

    @Test
    void 무르기_성공() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.start("user1");

        String blackPlayer = room.getSettings().getBlackPlayer();
        room.move(blackPlayer, new Position(10, 10));

        assertThatCode(() -> room.undo("user1"))
                .doesNotThrowAnyException();
    }

    @Test
    void 관전자는_무르기_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");
        room.start("user1");

        assertThatThrownBy(() -> room.undo("user3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void 게임_시작_요청_생성() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");

        assertThat(room.hasPendingRequest()).isTrue();
        assertThat(room.getPendingRequest().getType()).isEqualTo(RequestType.START);
        assertThat(room.getPendingRequest().getRequester()).isEqualTo("user1");
    }

    @Test
    void 무르기_요청_생성() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.start("user1");

        room.createRequest(RequestType.UNDO, "user1");

        assertThat(room.hasPendingRequest()).isTrue();
        assertThat(room.getPendingRequest().getType()).isEqualTo(RequestType.UNDO);
    }

    @Test
    void 계가_요청_생성() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.start("user1");

        room.createRequest(RequestType.SCORE, "user1");

        assertThat(room.hasPendingRequest()).isTrue();
        assertThat(room.getPendingRequest().getType()).isEqualTo(RequestType.SCORE);
    }

    @Test
    void 이미_대기중인_요청이_있으면_새_요청_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");

        assertThatThrownBy(() -> room.createRequest(RequestType.START, "user2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리 중인 요청이 있습니다");
    }

    @Test
    void 관전자는_요청_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        assertThatThrownBy(() -> room.createRequest(RequestType.START, "user3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void 게임_시작_전_무르기_요청_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        assertThatThrownBy(() -> room.createRequest(RequestType.UNDO, "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("게임이 시작되지 않았습니다");
    }

    @Test
    void 게임_시작_전_계가_요청_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        assertThatThrownBy(() -> room.createRequest(RequestType.SCORE, "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("게임이 시작되지 않았습니다");
    }

    @Test
    void 요청_수락() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");
        room.acceptRequest("user2");

        assertThat(room.hasPendingRequest()).isFalse();
    }

    @Test
    void 요청_거절() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");
        room.rejectRequest("user2");

        assertThat(room.hasPendingRequest()).isFalse();
    }

    @Test
    void 대기중인_요청이_없으면_수락_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        assertThatThrownBy(() -> room.acceptRequest("user2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("대기 중인 요청이 없습니다");
    }

    @Test
    void 요청자가_아닌_상대방만_응답_가능() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");

        // 요청자 본인은 응답 불가
        assertThatThrownBy(() -> room.acceptRequest("user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("응답 권한이 없습니다");
    }

    @Test
    void 관전자는_응답_불가() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        room.createRequest(RequestType.START, "user1");

        assertThatThrownBy(() -> room.acceptRequest("user3"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("응답 권한이 없습니다");
    }

    @Test
    void 타겟_플레이어_조회() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");

        String targetPlayer = room.getPendingRequest().getTargetPlayer("user1", "user2");
        assertThat(targetPlayer).isEqualTo("user2");
    }

// ==================== 연결 끊김 처리 테스트 ====================

    @Test
    void 연결_끊김시_사용자_제거() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.handleDisconnect("user1");

        assertThat(room.getPlayer1()).isNull();
        assertThat(room.getPlayer2()).isEqualTo("user2");
    }

    @Test
    void 연결_끊김시_진행중인_요청_취소() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");
        assertThat(room.hasPendingRequest()).isTrue();

        room.handleDisconnect("user1");

        assertThat(room.hasPendingRequest()).isFalse();
    }

    @Test
    void 연결_끊김시_응답자가_끊겨도_요청_취소() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");

        room.createRequest(RequestType.START, "user1");
        assertThat(room.hasPendingRequest()).isTrue();

        // 응답자가 연결 끊김
        room.handleDisconnect("user2");

        assertThat(room.hasPendingRequest()).isFalse();
    }

    @Test
    void 관전자_연결_끊김은_요청에_영향_없음() {
        GameRoom room = new GameRoom("test-room");
        room.addUser("user1");
        room.addUser("user2");
        room.addUser("user3");

        room.createRequest(RequestType.START, "user1");
        assertThat(room.hasPendingRequest()).isTrue();

        // 관전자 연결 끊김
        room.handleDisconnect("user3");

        assertThat(room.hasPendingRequest()).isTrue();
    }
}