package com.woowa.woowago.service;

import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class KataGoServiceTest {

    @Autowired
    private KataGoService kataGoService;

    @Autowired
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService.startNewGame();
    }

    @Test
    void 초반_착수_추천() {
        // given
        gameService.move(4, 4);   // 흑 D4
        gameService.move(16, 16); // 백 Q16

        // when
        BlueSpotsResponse response = kataGoService.getBlueSpots();

        // then
        assertThat(response.getX()).isBetween(1, 19);
        assertThat(response.getY()).isBetween(1, 19);
        System.out.println("추천 수: (" + response.getX() + ", " + response.getY() + ")");
    }

    @Test
    void 빈_바둑판_착수_추천() {
        // when
        BlueSpotsResponse response = kataGoService.getBlueSpots();

        // then
        assertThat(response.getX()).isBetween(1, 19);
        assertThat(response.getY()).isBetween(1, 19);
        System.out.println("추천 수: (" + response.getX() + ", " + response.getY() + ")");
    }

    @Test
    void 몇_수_후_계가() {
        // given
        gameService.move(4, 4);   // 흑
        gameService.move(16, 16); // 백
        gameService.move(4, 16);  // 흑
        gameService.move(16, 4);  // 백

        // when
        ScoreResponse response = kataGoService.getScore();

        // then
        assertThat(response.getResult()).isNotEmpty();
        System.out.println("점수: " + response.getResult());
    }

    @Test
    void 여러_수_후_계가() {
        // given - 10수 정도
        gameService.move(4, 4);
        gameService.move(16, 16);
        gameService.move(4, 16);
        gameService.move(16, 4);
        gameService.move(10, 10);
        gameService.move(10, 16);
        gameService.move(16, 10);
        gameService.move(10, 4);
        gameService.move(4, 10);
        gameService.move(13, 13);

        // when
        ScoreResponse response = kataGoService.getScore();

        // then
        assertThat(response.getResult()).matches("^[BW]\\+\\d+\\.\\d+$");
        System.out.println("점수: " + response.getResult());
    }
}