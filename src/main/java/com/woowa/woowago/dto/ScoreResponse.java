package com.woowa.woowago.dto;

import lombok.Getter;

@Getter
public class ScoreResponse {
    private final String result;

    public ScoreResponse(String result) {
        this.result = result;
    }

}