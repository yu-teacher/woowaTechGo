package com.woowa.woowago.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveHistory {
    private final List<Move> moves;

    public MoveHistory() {
        this.moves = new ArrayList<>();
    }

    public void add(Move move) {
        moves.add(move);
    }

    /**
     * 마지막 착수 제거 (무르기)
     * @return 제거된 착수
     * @throws IllegalStateException 착수 기록이 없을 경우
     */
    public Move removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("[ERROR] 무를 수 없습니다.");
        }
        return moves.removeLast();
    }

    /**
     * 마지막 착수 조회 (제거하지 않음)
     * @return 마지막 착수
     * @throws IllegalStateException 착수 기록이 없을 경우
     */
    public Move getLast() {
        if (isEmpty()) {
            throw new IllegalStateException("[ERROR] 착수 기록이 없습니다.");
        }
        return moves.getLast();
    }

    public List<Move> getAll() {
        return Collections.unmodifiableList(moves);
    }

    public boolean isEmpty() {
        return moves.isEmpty();
    }

    public int size() {
        return moves.size();
    }
}
