package com.woowa.woowago.domain.game;

public enum Stone {
    BLACK,
    WHITE,
    EMPTY;

    public Stone opposite() {
        if (this == BLACK) {
            return WHITE;
        }
        if (this == WHITE) {
            return BLACK;
        }
        throw new IllegalStateException("[ERROR] EMPTY는 반대 색상이 없습니다.");
    }
}
