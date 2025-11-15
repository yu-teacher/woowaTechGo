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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class KataGoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // 게임 시작
        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk());
    }

    @Test
    void 착수_추천_요청() throws Exception {
        // given - 몇 수 두기
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(4, 4))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(16, 16))))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(get("/api/katago/bluespots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").isNumber())
                .andExpect(jsonPath("$.y").isNumber())
                .andDo(print());
    }

    @Test
    void 빈_바둑판_착수_추천() throws Exception {
        mockMvc.perform(get("/api/katago/bluespots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").exists())
                .andExpect(jsonPath("$.y").exists())
                .andDo(print());
    }

    @Test
    void 계가_요청() throws Exception {
        // given - 몇 수 두기
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(4, 4))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(16, 16))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(4, 16))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(16, 4))))
                .andExpect(status().isOk());

        // when & then
        mockMvc.perform(get("/api/katago/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value(org.hamcrest.Matchers.matchesPattern("^[BW]\\+\\d+\\.\\d+$")))
                .andDo(print());
    }

    @Test
    void 여러_수_후_계가() throws Exception {
        // given - 10수
        int[][] moves = {
                {4, 4}, {16, 16}, {4, 16}, {16, 4},
                {10, 10}, {10, 16}, {16, 10}, {10, 4},
                {4, 10}, {13, 13}
        };

        for (int[] move : moves) {
            mockMvc.perform(post("/api/game/move")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new MoveRequest(move[0], move[1]))))
                    .andExpect(status().isOk());
        }

        // when & then
        mockMvc.perform(get("/api/katago/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists())
                .andDo(print());
    }
}