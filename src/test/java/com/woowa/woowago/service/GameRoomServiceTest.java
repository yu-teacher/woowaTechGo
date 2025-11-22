package com.woowa.woowago.service;

import com.woowa.woowago.domain.room.GameRoom;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.dto.websocket.JoinResponse;
import com.woowa.woowago.dto.websocket.StartResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameRoomServiceTest {

    @InjectMocks
    private GameRoomService service;

    @Mock
    private KataGoService kataGoService;

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

    @Test
    void getRoomOrThrow_방이_있으면_반환() {
        service.joinRoom("room1", "user1");

        GameRoom room = service.getRoomOrThrow("room1");

        assertThat(room).isNotNull();
        assertThat(room.getRoomId()).isEqualTo("room1");
    }

    @Test
    void getRoomOrThrow_방이_없으면_예외() {
        assertThatThrownBy(() -> service.getRoomOrThrow("unknown-room"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("게임 방을 찾을 수 없습니다");
    }

    @Test
    void join_입장_처리_및_JoinResponse_반환() {
        JoinResponse response = service.join("room1", "user1");

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo("player1");
        assertThat(response.getPlayer1()).isEqualTo("user1");
        assertThat(response.isReady()).isFalse();
    }

    @Test
    void start_게임_시작_및_StartResponse_반환() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");

        StartResponse response = service.start("room1", "user1");

        assertThat(response).isNotNull();
        assertThat(response.getGameState()).isNotNull();
        assertThat(response.getBlackPlayer()).isNotNull();
        assertThat(response.getWhitePlayer()).isNotNull();
    }

    @Test
    void start_참가자가_아니면_예외() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.joinRoom("room1", "user3");  // 관전자

        assertThatThrownBy(() -> service.start("room1", "user3"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void move_착수_성공() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        GameRoom room = service.getRoom("room1");
        String blackPlayer = room.getSettings().getBlackPlayer();

        GameStateResponse response = service.move("room1", blackPlayer, 10, 10);

        assertThat(response).isNotNull();
        assertThat(response.getMoveCount()).isEqualTo(1);
    }

    @Test
    void move_관전자는_착수_불가() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.joinRoom("room1", "user3");  // 관전자
        service.start("room1", "user1");

        assertThatThrownBy(() -> service.move("room1", "user3", 10, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("참가자만");
    }

    @Test
    void undo_무르기_성공() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        GameRoom room = service.getRoom("room1");
        String blackPlayer = room.getSettings().getBlackPlayer();
        service.move("room1", blackPlayer, 10, 10);

        GameStateResponse response = service.undo("room1", "user1");

        assertThat(response).isNotNull();
        assertThat(response.getMoveCount()).isEqualTo(0);
    }

    @Test
    void score_계가_조회() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        ScoreResponse mockScore = new ScoreResponse("BLACK+2.5");  // ← 이렇게!
        when(kataGoService.getScore(any())).thenReturn(mockScore);

        ScoreResponse response = service.score("room1");

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo("BLACK+2.5");
        verify(kataGoService, times(1)).getScore(any());
    }
}