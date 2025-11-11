package com.woowa.woowago.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public class Move {
    private final int moveNumber;
    private final Position position;
    private final Stone color;
    private final Set<Position> capturedPositions;

    /**
     * 착수 기록 생성 (어노테이션으로 만들 수 있었으나 주석 및 불변복사를 위해 남겨둠)
     * @param moveNumber 착수 번호 (1부터 시작)
     * @param position 착수 위치
     * @param color 놓은 돌의 색상
     * @param capturedPositions 이 수로 따낸 돌들의 위치 (없으면 빈 Set)
     */
    public Move(int moveNumber, Position position, Stone color, Set<Position> capturedPositions) {
        this.moveNumber = moveNumber;
        this.position = position;
        this.color = color;
        this.capturedPositions = Set.copyOf(capturedPositions);  // 불변 복사
    }
}
