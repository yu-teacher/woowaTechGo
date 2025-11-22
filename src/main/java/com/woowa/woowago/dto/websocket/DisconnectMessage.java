package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 연결 끊김 메시지 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DisconnectMessage {
    private String username;    // 연결이 끊긴 사용자
    private String message;     // "OOO님의 연결이 끊어졌습니다"

    public static DisconnectMessage of(String username) {
        String message = username + "님의 연결이 끊어졌습니다";
        return new DisconnectMessage(username, message);
    }
}