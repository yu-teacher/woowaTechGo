package com.woowa.woowago.domain.room;

import com.woowa.woowago.domain.game.Stone;
import lombok.Getter;

import java.util.Random;

/**
 * 게임 외적 룰 관리
 * - 흑/백 플레이어 배정
 * - 차례 확인
 * (시간 제한, 초읽기, 덤 등 추기힐 수 있도록 확장성 고려)
 */
@Getter
public class GameSettings {
    private String blackPlayer;
    private String whitePlayer;
    private boolean assigned = false;

    /**
     * 흑/백 랜덤 배정
     * @param player1 참가자 1
     * @param player2 참가자 2
     */
    public void assignColors(String player1, String player2) {
        if (assigned) {
            throw new IllegalStateException("[ERROR] 이미 색상이 배정되었습니다.");
        }

        validatePlayers(player1, player2);

        String[] players = {player1, player2};
        int randomIndex = new Random().nextInt(2);
        this.blackPlayer = players[randomIndex];
        this.whitePlayer = players[1 - randomIndex];
        this.assigned = true;
    }

    private void validatePlayers(String player1, String player2) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("[ERROR] 참가자가 2명이어야 합니다.");
        }
        if (player1.equals(player2)) {
            throw new IllegalArgumentException("[ERROR] 같은 사용자는 배정할 수 없습니다.");
        }
    }

    /**
     * 배정 완료 여부 검증
     */
    public void validateAssigned() {
        if (!assigned) {
            throw new IllegalStateException("[ERROR] 색상이 배정되지 않았습니다.");
        }
    }

    /**
     * 내 차례인지 확인
     * @param username 사용자명
     * @param currentTurn 현재 차례 (BLACK/WHITE)
     * @return 내 차례면 true
     */
    public boolean isMyTurn(String username, Stone currentTurn) {
        validateAssigned();

        if (currentTurn == Stone.BLACK) {
            return username.equals(blackPlayer);
        }
        if (currentTurn == Stone.WHITE) {
            return username.equals(whitePlayer);
        }
        return false;
    }

    /**
     * 내 차례 검증 (예외 발생)
     */
    public void validateMyTurn(String username, Stone currentTurn) {
        if (!isMyTurn(username, currentTurn)) {
            String turnName = currentTurn == Stone.BLACK ? "흑" : "백";
            throw new IllegalArgumentException("[ERROR] " + turnName + "의 차례입니다.");
        }
    }

}