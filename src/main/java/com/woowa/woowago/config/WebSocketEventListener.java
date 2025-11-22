package com.woowa.woowago.config;

import com.woowa.woowago.dto.websocket.DisconnectMessage;
import com.woowa.woowago.dto.websocket.GameMessage;
import com.woowa.woowago.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocket 이벤트 리스너
 * 연결 끊김 감지 및 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final GameRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket 연결 끊김 이벤트 처리
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String gameId = (String) headerAccessor.getSessionAttributes().get("gameId");

        if (username != null && gameId != null) {
            log.info("User {} disconnected from game {}", username, gameId);

            try {
                // 연결 끊김 처리
                DisconnectMessage disconnectMessage = roomService.handleDisconnect(gameId, username);

                // 다른 사용자들에게 알림
                GameMessage message = new GameMessage("DISCONNECT", disconnectMessage, username);
                messagingTemplate.convertAndSend("/topic/game." + gameId, message);

            } catch (Exception e) {
                log.error("Error handling disconnect for user {} in game {}: {}",
                        username, gameId, e.getMessage());
            }
        }
    }
}