package com.woowa.woowago.scheduler;

import com.woowa.woowago.domain.room.GameRoom;
import com.woowa.woowago.dto.websocket.GameMessage;
import com.woowa.woowago.dto.websocket.TimeoutMessage;
import com.woowa.woowago.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 게임 방 타임아웃 체크 스케줄러
 * 10초마다 대기 중인 요청의 타임아웃 체크
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameRoomScheduler {

    private final GameRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 대기 중인 요청 타임아웃 체크 (10초마다)
     * 30초 경과한 요청은 자동 거절 처리
     */
    @Scheduled(fixedRate = 10000)
    public void checkPendingRequests() {
        Map<String, GameRoom> timeoutRooms = roomService.getRoomsWithTimeoutRequests();

        if (timeoutRooms.isEmpty()) {
            return;
        }

        log.info("Found {} rooms with timeout requests", timeoutRooms.size());

        timeoutRooms.forEach((roomId, room) -> {
            try {
                // 타임아웃 메시지 생성
                TimeoutMessage timeoutMessage = TimeoutMessage.of(
                        room.getPendingRequest().getType(),
                        room.getPendingRequest().getRequester()
                );

                // 요청 자동 거절
                room.clearRequest();

                // 브로드캐스트
                GameMessage message = new GameMessage(
                        "TIMEOUT_REQUEST",
                        timeoutMessage,
                        timeoutMessage.getRequester()
                );
                messagingTemplate.convertAndSend("/topic/game." + roomId, message);

                log.info("Auto-rejected timeout request in room: {}, requester: {}",
                        roomId, timeoutMessage.getRequester());

            } catch (Exception e) {
                log.error("Error handling timeout for room {}: {}", roomId, e.getMessage());
            }
        });
    }
}