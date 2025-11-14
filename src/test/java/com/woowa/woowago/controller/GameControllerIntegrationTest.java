package com.woowa.woowago.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowa.woowago.dto.MoveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GameController 통합 테스트
 * 실제 Spring Context를 띄워서 전체 흐름 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // 매 테스트마다 새 게임 시작
        mockMvc.perform(post("/api/game/start"));
    }

    @Test
    void 새_게임_시작() throws Exception {
        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTurn").value("BLACK"))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.blackCaptures").value(0))
                .andExpect(jsonPath("$.whiteCaptures").value(0))
                .andExpect(jsonPath("$.moveCount").value(0));
    }

    @Test
    void 착수_성공() throws Exception {
        MoveRequest request = new MoveRequest(10, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("착수 완료"))
                .andExpect(jsonPath("$.gameState.currentTurn").value("WHITE"))
                .andExpect(jsonPath("$.gameState.moveCount").value(1));
    }

    @Test
    void 연속_착수() throws Exception {
        // 흑
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))))
                .andExpect(status().isOk());

        // 백
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(11, 11))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameState.currentTurn").value("BLACK"))
                .andExpect(jsonPath("$.gameState.moveCount").value(2));
    }

    @Test
    void 무르기_성공() throws Exception {
        // 착수
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))))
                .andExpect(status().isOk());

        // 무르기
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("무르기 완료"))
                .andExpect(jsonPath("$.gameState.currentTurn").value("BLACK"))
                .andExpect(jsonPath("$.gameState.moveCount").value(0));
    }

    @Test
    void 게임_상태_조회() throws Exception {
        mockMvc.perform(get("/api/game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTurn").value("BLACK"))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.board[0]").isArray())
                .andExpect(jsonPath("$.board[0][0]").value("EMPTY"));
    }

    @Test
    void 착수_기록_조회() throws Exception {
        // 착수 2번
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))));
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 11))));

        // 기록 조회
        mockMvc.perform(get("/api/game/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].moveNumber").value(1))
                .andExpect(jsonPath("$[0].x").value(10))
                .andExpect(jsonPath("$[0].y").value(10))
                .andExpect(jsonPath("$[0].color").value("BLACK"))
                .andExpect(jsonPath("$[1].moveNumber").value(2))
                .andExpect(jsonPath("$[1].x").value(11))
                .andExpect(jsonPath("$[1].y").value(11))
                .andExpect(jsonPath("$[1].color").value("WHITE"));
    }

    @Test
    void 유효하지_않은_좌표_예외() throws Exception {
        MoveRequest request = new MoveRequest(0, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 유효하지 않은 좌표입니다."));
    }

    @Test
    void 이미_돌이_있는_위치_예외() throws Exception {
        MoveRequest request = new MoveRequest(10, 10);

        // 첫 번째 착수
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 같은 위치에 다시 착수
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 이미 돌이 있는 위치입니다."));
    }

    @Test
    void 무를_수_없음_예외() throws Exception {
        // 착수 없이 무르기 시도
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 무를 수 없습니다."));
    }
}