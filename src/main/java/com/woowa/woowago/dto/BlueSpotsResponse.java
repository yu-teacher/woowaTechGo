package com.woowa.woowago.dto;

import lombok.Getter;

@Getter
public class BlueSpotsResponse {
    private final int x;
    private final int y;

    public BlueSpotsResponse(int x, int y) {
        this.x = x;
        this.y = y;
    }

}