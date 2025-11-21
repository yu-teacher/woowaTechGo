package com.woowa.woowago.service;

import com.woowa.woowago.domain.room.GameRoom;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 방 관리 서비스
 */
@Service
public class GameRoomService {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

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
     * @return 배정된 역할 (player1/player2/spectator)
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

            if (isEmpty(room)) {
                rooms.remove(roomId);
            }
        }
    }

    /**
     * 방이 비어있는지 확인
     */
    private boolean isEmpty(GameRoom room) {
        return room.getPlayer1() == null
                && room.getPlayer2() == null
                && room.getSpectators().isEmpty();
    }

    /**
     * 방 조회
     * @param roomId 방 ID
     * @return GameRoom (없으면 null)
     */
    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }
}