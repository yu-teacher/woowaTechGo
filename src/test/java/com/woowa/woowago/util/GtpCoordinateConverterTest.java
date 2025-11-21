package com.woowa.woowago.util;

import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GtpCoordinateConverterTest {

    @Test
    void Position을_GTP_좌표로_변환() {
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(1, 1))).isEqualTo("A1");
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(4, 4))).isEqualTo("D4");
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(10, 10))).isEqualTo("K10");
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(16, 16))).isEqualTo("Q16");
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(19, 19))).isEqualTo("T19");
    }

    @Test
    void GTP_좌표를_Position으로_변환() {
        assertThat(GtpCoordinateConverter.fromGtpCoordinate("A1")).isEqualTo(new Position(1, 1));
        assertThat(GtpCoordinateConverter.fromGtpCoordinate("D4")).isEqualTo(new Position(4, 4));
        assertThat(GtpCoordinateConverter.fromGtpCoordinate("K10")).isEqualTo(new Position(10, 10));
        assertThat(GtpCoordinateConverter.fromGtpCoordinate("Q16")).isEqualTo(new Position(16, 16));
        assertThat(GtpCoordinateConverter.fromGtpCoordinate("T19")).isEqualTo(new Position(19, 19));
    }

    @Test
    void I는_제외됨() {
        // H 다음은 J
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(8, 1))).isEqualTo("H1");
        assertThat(GtpCoordinateConverter.toGtpCoordinate(new Position(9, 1))).isEqualTo("J1");
    }

    @Test
    void Stone을_GTP_색상으로_변환() {
        assertThat(GtpCoordinateConverter.toGtpColor(Stone.BLACK)).isEqualTo("B");
        assertThat(GtpCoordinateConverter.toGtpColor(Stone.WHITE)).isEqualTo("W");
    }

    @Test
    void EMPTY는_GTP_색상_변환_불가() {
        assertThatThrownBy(() -> GtpCoordinateConverter.toGtpColor(Stone.EMPTY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] EMPTY는 GTP 색상으로 변환할 수 없습니다.");
    }

    @Test
    void 유효하지_않은_GTP_좌표_예외() {
        assertThatThrownBy(() -> GtpCoordinateConverter.fromGtpCoordinate(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> GtpCoordinateConverter.fromGtpCoordinate("Z1"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}