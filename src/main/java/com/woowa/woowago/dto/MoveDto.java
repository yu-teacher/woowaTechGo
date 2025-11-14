package com.woowa.woowago.dto;

import com.woowa.woowago.domain.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MoveDto {
    private int moveNumber;
    private int x;
    private int y;
    private String color;

    public static MoveDto from(Move move) {
        return new MoveDto(
                move.getMoveNumber(),
                move.getPosition().getX(),
                move.getPosition().getY(),
                move.getColor().name()
        );
    }
}