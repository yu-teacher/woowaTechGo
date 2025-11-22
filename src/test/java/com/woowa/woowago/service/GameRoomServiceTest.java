package com.woowa.woowago.service;

import com.woowa.woowago.domain.room.GameRoom;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.dto.websocket.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

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

        ScoreResponse mockScore = new ScoreResponse("BLACK+2.5");
        when(kataGoService.getScore(any())).thenReturn(mockScore);

        ScoreResponse response = service.score("room1");

        assertThat(response).isNotNull();
        assertThat(response.getResult()).isEqualTo("BLACK+2.5");
    }

    @Test
    void requestStart_게임_시작_요청() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");

        RequestMessage request = service.requestStart("room1", "user1");

        assertThat(request.getType()).isEqualTo("REQUEST_START");
        assertThat(request.getRequester()).isEqualTo("user1");
        assertThat(request.getMessage()).contains("게임 시작");
    }

    @Test
    void respondStart_수락시_게임_시작() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.requestStart("room1", "user1");

        Object response = service.respondStart("room1", "user2", true);

        assertThat(response).isInstanceOf(StartResponse.class);
        assertThat(service.getRoom("room1").isGameStarted()).isTrue();
    }

    @Test
    void respondStart_거절시_ResponseMessage() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.requestStart("room1", "user1");

        Object response = service.respondStart("room1", "user2", false);

        assertThat(response).isInstanceOf(ResponseMessage.class);
        ResponseMessage msg = (ResponseMessage) response;
        assertThat(msg.isAccepted()).isFalse();
    }

    @Test
    void requestUndo_무르기_요청() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        RequestMessage request = service.requestUndo("room1", "user1");

        assertThat(request.getType()).isEqualTo("REQUEST_UNDO");
        assertThat(request.getMessage()).contains("무르기");
    }

    @Test
    void respondUndo_수락시_무르기_실행() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        GameRoom room = service.getRoom("room1");
        String blackPlayer = room.getSettings().getBlackPlayer();
        service.move("room1", blackPlayer, 10, 10);

        service.requestUndo("room1", "user1");
        Object response = service.respondUndo("room1", "user2", true);

        assertThat(response).isInstanceOf(GameStateResponse.class);
        GameStateResponse state = (GameStateResponse) response;
        assertThat(state.getMoveCount()).isEqualTo(0);
    }

    @Test
    void requestScore_계가_요청() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        RequestMessage request = service.requestScore("room1", "user1");

        assertThat(request.getType()).isEqualTo("REQUEST_SCORE");
        assertThat(request.getMessage()).contains("계가");
    }

    @Test
    void respondScore_수락시_계가_실행() {
        when(kataGoService.getScore(any())).thenReturn(new ScoreResponse("B+5.5"));

        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.start("room1", "user1");

        service.requestScore("room1", "user1");
        Object response = service.respondScore("room1", "user2", true);

        assertThat(response).isInstanceOf(ScoreResponse.class);
    }

    @Test
    void handleDisconnect_연결_끊김_처리() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");

        DisconnectMessage message = service.handleDisconnect("room1", "user1");

        assertThat(message.getUsername()).isEqualTo("user1");
        assertThat(message.getMessage()).contains("연결이 끊어졌습니다");
        assertThat(service.getRoom("room1").getPlayer1()).isNull();
    }

    @Test
    void handleDisconnect_요청_진행중이면_취소() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.requestStart("room1", "user1");

        service.handleDisconnect("room1", "user1");

        assertThat(service.getRoom("room1").hasPendingRequest()).isFalse();
    }

    @Test
    void getRoomsWithTimeoutRequests_타임아웃_요청_조회() {
        service.joinRoom("room1", "user1");
        service.joinRoom("room1", "user2");
        service.requestStart("room1", "user1");

        Map<String, GameRoom> timeoutRooms = service.getRoomsWithTimeoutRequests();

        // 타임아웃 전에는 빈 맵
        assertThat(timeoutRooms).isEmpty();
    }
}