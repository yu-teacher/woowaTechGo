package com.woowa.woowago.service;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.room.GameRoom;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.dto.websocket.JoinResponse;
import com.woowa.woowago.dto.websocket.StartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 방 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final KataGoService kataGoService;

    /**
     * 방 생성 또는 조회
     * @param roomId 방 ID
     * @return GameRoom
     */
    public GameRoom createOrGetRoom(String roomId) {
        return rooms.computeIfAbsent(roomId, GameRoom::new);
    }

    /**
     * 방 입장 (자동 역할 배정)
     * @param roomId 방 ID
     * @param username 사용자명
     * @return 배정된 역할
     */
    public String joinRoom(String roomId, String username) {
        GameRoom room = createOrGetRoom(roomId);
        return room.addUser(username);
    }

    /**
     * 방 퇴장
     * @param roomId 방 ID
     * @param username 사용자명
     */
    public void leaveRoom(String roomId, String username) {
        GameRoom room = rooms.get(roomId);
        if (room != null) {
            room.removeUser(username);
            if (room.getParticipants().isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    /**
     * 방 조회
     * @param roomId 방 ID
     * @return GameRoom (없으면 null)
     */
    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * 방 조회 (없으면 예외)
     * @param gameId 방 ID
     * @return GameRoom
     * @throws IllegalStateException 방이 없을 경우
     */
    public GameRoom getRoomOrThrow(String gameId) {
        GameRoom room = getRoom(gameId);
        if (room == null) {
            throw new IllegalStateException("[ERROR] 게임 방을 찾을 수 없습니다.");
        }
        return room;
    }

    /**
     * 입장 처리 (비즈니스 로직 + DTO 변환)
     * @param gameId 방 ID
     * @param username 사용자명
     * @return JoinResponse
     */
    public JoinResponse join(String gameId, String username) {
        String role = joinRoom(gameId, username);
        GameRoom room = getRoomOrThrow(gameId);

        GameStateResponse gameState = GameStateResponse.from(room.getGame());
        return JoinResponse.from(room, gameState, role);
    }

    /**
     * 게임 시작 (비즈니스 로직 + DTO 변환)
     * @param gameId 방 ID
     * @param username 시작을 요청한 사용자명
     * @return StartResponse
     */
    public StartResponse start(String gameId, String username) {
        GameRoom room = getRoomOrThrow(gameId);

        room.start(username);

        GameStateResponse gameState = GameStateResponse.from(room.getGame());
        return StartResponse.from(room, gameState);
    }

    /**
     * 착수 (비즈니스 로직 + DTO 변환)
     * @param gameId 방 ID
     * @param username 착수하는 사용자명
     * @param x 착수 x 좌표
     * @param y 착수 y 좌표
     * @return GameStateResponse
     */
    public GameStateResponse move(String gameId, String username, int x, int y) {
        GameRoom room = getRoomOrThrow(gameId);

        Position position = new Position(x, y);
        room.move(username, position);

        return GameStateResponse.from(room.getGame());
    }

    /**
     * 무르기 (비즈니스 로직 + DTO 변환)
     * @param gameId 방 ID
     * @param username 무르기를 요청한 사용자명
     * @return GameStateResponse
     */
    public GameStateResponse undo(String gameId, String username) {
        GameRoom room = getRoomOrThrow(gameId);

        room.undo(username);

        return GameStateResponse.from(room.getGame());
    }

    /**
     * 계가 (KataGo 연동)
     * @param gameId 방 ID
     * @return ScoreResponse
     */
    public ScoreResponse score(String gameId) {
        GameRoom room = getRoomOrThrow(gameId);

        return kataGoService.getScore(room.getGame());
    }
}