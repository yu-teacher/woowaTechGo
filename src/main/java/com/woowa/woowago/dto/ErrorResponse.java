package com.woowa.woowago.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 에러 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorResponse {
    private String error;
}
