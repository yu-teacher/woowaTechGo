package com.woowa.woowago.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

/**
 * WebSocket 설정
 * STOMP 프로토콜 사용
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트로 메시지를 보낼 때 prefix
        registry.enableSimpleBroker("/topic");
        // 클라이언트에서 메시지를 보낼 때 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록
        registry.addEndpoint("/ws-game")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    // SUBSCRIBE 메시지에서 gameId와 username 추출
                    String destination = accessor.getDestination();

                    if (destination != null && destination.startsWith("/topic/game.")) {
                        // /topic/game.{gameId} 형식에서 gameId 추출
                        String gameId = destination.substring("/topic/game.".length());

                        // 세션에 gameId 저장
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        if (sessionAttributes != null) {
                            sessionAttributes.put("gameId", gameId);

                            // username은 클라이언트가 헤더로 전달
                            String username = accessor.getFirstNativeHeader("username");
                            if (username != null) {
                                sessionAttributes.put("username", username);
                                log.info("Stored session info - gameId: {}, username: {}", gameId, username);
                            }
                        }
                    }
                }

                return message;
            }
        });
    }
}