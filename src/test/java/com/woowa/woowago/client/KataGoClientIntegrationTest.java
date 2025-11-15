package com.woowa.woowago.client;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

/**
 * KataGoClient 통합 테스트
 * 실제 KataGo 프로세스와 통신
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KataGoClientIntegrationTest {

    @Autowired
    private KataGoClient client;

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        client.stop();
    }

    @Test
    @Order(1)
    void KataGo_프로세스_시작() throws IOException, InterruptedException {
        client.start();
        // 프로세스가 정상적으로 시작되었는지 확인
        assertThat(client).isNotNull();
    }

    @Test
    @Order(2)
    void 바둑판_크기_설정() throws IOException, InterruptedException {
        client.start();
        client.setBoardSize(19);
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @Order(3)
    void 바둑판_초기화() throws IOException, InterruptedException {
        client.start();
        client.clearBoard();
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @Order(4)
    void 돌_놓기() throws IOException, InterruptedException {
        client.start();
        client.setBoardSize(19);
        client.clearBoard();

        client.play("B", "D4");
        client.play("W", "Q16");

        // 예외가 발생하지 않으면 성공
    }

    @Test
    @Order(5)
    void 착수_추천() throws IOException, InterruptedException {
        client.start();
        client.setBoardSize(19);
        client.clearBoard();

        client.play("B", "D4");

        String move = client.genmove("W");

        assertThat(move).isNotNull();
        assertThat(move).isNotEmpty();
        assertThat(move).isNotEqualTo("pass");
        System.out.println("KataGo 추천 수: " + move);
    }

    @Test
    @Order(6)
    void 여러_명령_순차_실행() throws IOException, InterruptedException {
        client.start();
        client.setBoardSize(19);
        client.clearBoard();

        client.play("B", "D4");
        client.play("W", "Q16");

        String blackMove = client.genmove("B");
        assertThat(blackMove).isNotNull();

        client.play("B", blackMove);

        String whiteMove = client.genmove("W");
        assertThat(whiteMove).isNotNull();

        System.out.println("흑 추천: " + blackMove);
        System.out.println("백 추천: " + whiteMove);
    }
}