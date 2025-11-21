package com.woowa.woowago.util;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;

/**
 * GTP 좌표 변환 유틸리티
 * 우리 시스템: (x, y) [1~19]
 * GTP: "A1" ~ "T19" [I 제외]
 */
public class GtpCoordinateConverter {

    private static final char[] GTP_COLUMNS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'
    };  // I는 제외 (1과 헷갈림 방지)

    /**
     * Position을 GTP 좌표로 변환
     * @param position (1~19, 1~19)
     * @return GTP 좌표 (예: "D4", "Q16")
     */
    public static String toGtpCoordinate(Position position) {
        int x = position.getX();  // 1~19
        int y = position.getY();  // 1~19

        // x좌표를 알파벳으로 변환 (1 → A, 2 → B, ...)
        char column = GTP_COLUMNS[x - 1];

        // y좌표를 GTP 행으로 변환 (y=1이 GTP의 1)
        int row = y;

        return "" + column + row;
    }

    /**
     * GTP 좌표를 Position으로 변환
     * @param gtpCoordinate GTP 좌표 (예: "D4", "Q16")
     * @return Position (1~19, 1~19)
     */
    public static Position fromGtpCoordinate(String gtpCoordinate) {
        if (gtpCoordinate == null || gtpCoordinate.length() < 2) {
            throw new IllegalArgumentException("[ERROR] 유효하지 않은 GTP 좌표입니다: " + gtpCoordinate);
        }

        // 첫 글자 = 열 (A~T)
        char column = gtpCoordinate.charAt(0);

        // 나머지 = 행 (1~19)
        int row = Integer.parseInt(gtpCoordinate.substring(1));

        // 알파벳을 x좌표로 변환
        int x = findColumnIndex(column) + 1;
        int y = row;

        return new Position(x, y);
    }

    /**
     * 알파벳의 인덱스 찾기
     */
    private static int findColumnIndex(char column) {
        for (int i = 0; i < GTP_COLUMNS.length; i++) {
            if (GTP_COLUMNS[i] == column) {
                return i;
            }
        }
        throw new IllegalArgumentException("[ERROR] 유효하지 않은 GTP 열: " + column);
    }

    /**
     * Stone을 GTP 색상으로 변환
     * @param color Stone (BLACK 또는 WHITE)
     * @return "B" 또는 "W"
     */
    public static String toGtpColor(Stone color) {
        return switch (color) {
            case BLACK -> "B";
            case WHITE -> "W";
            default -> throw new IllegalArgumentException("[ERROR] EMPTY는 GTP 색상으로 변환할 수 없습니다.");
        };
    }
}