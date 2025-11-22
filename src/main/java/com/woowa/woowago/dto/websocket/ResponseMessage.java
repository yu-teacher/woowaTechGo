package com.woowa.woowago.dto.websocket;

import com.woowa.woowago.domain.room.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 응답 메시지 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseMessage {
    private String type;        // RESPOND_START, RESPOND_UNDO, RESPOND_SCORE
    private String responder;   // 응답한 사람
    private boolean accepted;   // 수락 여부
    private String message;     // "OOO님이 수락했습니다" / "OOO님이 거절했습니다"

    public static ResponseMessage of(RequestType requestType, String responder, boolean accepted) {
        String type = "RESPOND_" + requestType.name();
        String message = createMessage(responder, accepted);
        return new ResponseMessage(type, responder, accepted, message);
    }

    private static String createMessage(String responder, boolean accepted) {
        if (accepted) {
            return responder + "님이 수락했습니다";
        } else {
            return responder + "님이 거절했습니다";
        }
    }
}