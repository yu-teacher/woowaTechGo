package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게임 메시지 DTO
 * 브로드캐스트용
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GameMessage {
    private String type;      // JOIN, MOVE, UNDO, START, LEAVE, ERROR
    private Object data;      // 실제 데이터
    private String username;  // 액션을 수행한 사용자
}