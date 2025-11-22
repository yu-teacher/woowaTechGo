package com.woowa.woowago.dto.websocket;

import com.woowa.woowago.domain.room.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 타임아웃 메시지 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TimeoutMessage {
    private String type;        // TIMEOUT_REQUEST
    private String requester;   // 요청자
    private String message;     // "요청 시간이 초과되었습니다"

    public static TimeoutMessage of(RequestType requestType, String requester) {
        String message = createMessage(requestType);
        return new TimeoutMessage("TIMEOUT_REQUEST", requester, message);
    }

    private static String createMessage(RequestType requestType) {
        return switch (requestType) {
            case START -> "게임 시작 요청 시간이 초과되었습니다";
            case UNDO -> "무르기 요청 시간이 초과되었습니다";
            case SCORE -> "계가 요청 시간이 초과되었습니다";
        };
    }
}