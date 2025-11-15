package com.woowa.woowago.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

/**
 * 실제 프로 기보로 점수 계산 테스트
 */
@SpringBootTest
class KataGoRealGameTest {

    @Autowired
    private KataGoClient client;

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        client.stop();
    }

    @Test
    void 실제_기보_273수_점수_계산() throws IOException, InterruptedException {
        // SGF 파일 읽기 및 파싱
        String sgfContent = Files.readString(Paths.get("C:\\Users\\yusm1\\project\\wooteco\\kibo.sgf"));
        List<Move> moves = parseSgf(sgfContent);

        // KataGo 시작 및 착수 재현
        client.start();
        client.setBoardSize(19);
        client.clearBoard();

        for (Move move : moves) {
            client.play(move.color, move.position);
        }

        // 점수 계산
        String score = client.finalScore();

        System.out.println("총 착수: " + moves.size() + "수");
        System.out.println("계산 점수: " + score);

        assertThat(score).isNotEmpty();
        assertThat(score).startsWith("W+");  // SGF: RE[W+0.50]
    }

    /**
     * SGF 파싱
     */
    private List<Move> parseSgf(String sgf) {
        List<Move> moves = new ArrayList<>();
        Pattern pattern = Pattern.compile(";([BW])\\[([a-s]{2})\\]");
        Matcher matcher = pattern.matcher(sgf);

        while (matcher.find() && moves.size() < 280) {
            String color = matcher.group(1);
            String gtpPos = convertSgfToGtp(matcher.group(2));
            moves.add(new Move(color, gtpPos));
        }

        return moves;
    }

    /**
     * SGF → GTP 좌표 변환
     */
    private String convertSgfToGtp(String sgfPos) {
        char col = (char) (sgfPos.charAt(0) - 'a' + 'A');
        if (col >= 'I') col++;  // I 건너뛰기

        int row = 19 - (sgfPos.charAt(1) - 'a');

        return "" + col + row;
    }

    private static class Move {
        String color;
        String position;

        Move(String color, String position) {
            this.color = color;
            this.position = position;
        }
    }
}