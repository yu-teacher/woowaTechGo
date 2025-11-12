package com.woowa.woowago.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 게임의 현재 상태를 관리하는 클래스
 * 바둑판, 현재 차례, 따낸 돌 개수 정보를 포함
 */
@Getter
@Setter
public class GameState {
    private Board board;
    private Stone currentTurn;
    private int blackCaptures;
    private int whiteCaptures;

    public GameState() {
        this.board = new Board();
        this.currentTurn = Stone.BLACK;
        this.blackCaptures = 0;
        this.whiteCaptures = 0;
    }

    /**
     * 따낸 돌 개수 증가
     * @param stone 따낸 돌의 색상 (상대방 돌)
     * @param count 따낸 개수
     */
    public void addCaptures(Stone stone, int count) {
        if (stone == Stone.BLACK) {
            whiteCaptures += count;  // 백이 흑을 따냄
            return;
        }
        if (stone == Stone.WHITE) {
            blackCaptures += count;  // 흑이 백을 따냄
        }
    }
}