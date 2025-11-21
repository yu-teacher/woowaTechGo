package com.woowa.woowago.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowa.woowago.domain.game.Stone;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.MoveRequest;
import com.woowa.woowago.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GameService gameService;

    @BeforeEach
    void setUp() {
        // Mock GameStateResponse 생성
        Stone[][] board = new Stone[19][19];
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                board[i][j] = Stone.EMPTY;
            }
        }
        GameStateResponse mockResponse = new GameStateResponse(board, "BLACK", 0, 0, 0);

        // Mock 설정
        when(gameService.getGameState()).thenReturn(mockResponse);
    }

    @Test
    void 새_게임_시작() throws Exception {
        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTurn").exists())
                .andExpect(jsonPath("$.board").exists());
    }

    @Test
    void 착수_요청() throws Exception {
        MoveRequest request = new MoveRequest(10, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("착수 완료"))
                .andExpect(jsonPath("$.gameState").exists());
    }

    @Test
    void 무르기_요청() throws Exception {
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("무르기 완료"))
                .andExpect(jsonPath("$.gameState").exists());
    }

    @Test
    void 게임_상태_조회() throws Exception {
        mockMvc.perform(get("/api/game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTurn").exists())
                .andExpect(jsonPath("$.board").exists())
                .andExpect(jsonPath("$.blackCaptures").exists())
                .andExpect(jsonPath("$.whiteCaptures").exists());
    }

    @Test
    void 착수_기록_조회() throws Exception {
        mockMvc.perform(get("/api/game/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}