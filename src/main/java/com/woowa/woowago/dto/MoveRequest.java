package com.woowa.woowago.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 착수 요청 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MoveRequest {
    private int x;
    private int y;
}
