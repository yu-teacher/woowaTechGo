package com.woowa.woowago.dto.websocket;

import com.woowa.woowago.domain.room.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 요청 메시지 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequestMessage {
    private String type;        // REQUEST_START, REQUEST_UNDO, REQUEST_SCORE
    private String requester;   // 요청한 사람
    private String message;     // "OOO님이 게임 시작을 요청했습니다"

    public static RequestMessage of(RequestType requestType, String requester) {
        String type = "REQUEST_" + requestType.name();
        String message = createMessage(requestType, requester);
        return new RequestMessage(type, requester, message);
    }

    private static String createMessage(RequestType requestType, String requester) {
        return switch (requestType) {
            case START -> requester + "님이 게임 시작을 요청했습니다";
            case UNDO -> requester + "님이 무르기를 요청했습니다";
            case SCORE -> requester + "님이 계가를 요청했습니다";
        };
    }
}