package com.woowa.woowago.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 응답 요청 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RespondRequest {
    private String gameId;
    private String username;
    private boolean accepted;
}