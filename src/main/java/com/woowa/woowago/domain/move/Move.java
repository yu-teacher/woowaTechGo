package com.woowa.woowago.domain.move;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import lombok.Getter;

@Getter
public class Move {
    private final int moveNumber;
    private final Position position;
    private final Stone color;

    /**
     * 착수 기록 생성 (어노테이션으로 만들 수 있었으나 주석 및 불변복사를 위해 남겨둠)
     * @param moveNumber 착수 번호 (1부터 시작)
     * @param position 착수 위치
     * @param color 놓은 돌의 색상
     */
    public Move(int moveNumber, Position position, Stone color) {
        this.moveNumber = moveNumber;
        this.position = position;
        this.color = color;
    }
}
