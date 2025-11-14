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
 * 예외 처리 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class GameExceptionIntegrationTest {

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
    void 유효하지_않은_좌표_0_이하() throws Exception {
        MoveRequest request = new MoveRequest(0, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 유효하지 않은 좌표입니다."));
    }

    @Test
    void 유효하지_않은_좌표_20_이상() throws Exception {
        MoveRequest request = new MoveRequest(20, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 유효하지 않은 좌표입니다."));
    }

    @Test
    void 유효하지_않은_좌표_음수() throws Exception {
        MoveRequest request = new MoveRequest(-5, 10);

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 유효하지 않은 좌표입니다."));
    }

    @Test
    void 이미_돌이_있는_위치() throws Exception {
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
    void 자충수_예외() throws Exception {
        // 백돌로 빈 자리 둘러싸기
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(9, 10))));  // 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 11))));  // 백 (다른 곳)
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 10))));  // 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(12, 12))));  // 백 (다른 곳)
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(10, 9))));  // 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(13, 13))));  // 백 (다른 곳)
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(10, 11))));  // 흑

        // 백이 둘러싸인 곳에 착수 시도 (자충수)
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 자충수는 둘 수 없습니다."));
    }

    @Test
    void 패_규칙_위반() throws Exception {
        // 패 상황 만들기
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))));  // 1. 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 10))));  // 2. 백
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 11))));  // 3. 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(10, 11))));  // 4. 백
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 9))));   // 5. 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(12, 11))));  // 6. 백
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(12, 10))));  // 7. 흑
        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(11, 12))));  // 8. 백

        mockMvc.perform(post("/api/game/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MoveRequest(1, 1))));    // 9. 흑

        // 10. 백이 (11,10)에 놓아서 흑(11,11) 따냄
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(11, 10))))
                .andExpect(status().isOk());

        // 11. 흑이 바로 (11,11)에 다시 놓으려 함 (패 규칙 위반)
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(11, 11))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 패 규칙 위반입니다."));
    }

    @Test
    void 무를_수_없음_착수_없음() throws Exception {
        // 착수 없이 무르기 시도
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 무를 수 없습니다."));
    }

    @Test
    void 무를_수_없음_이미_무름() throws Exception {
        // 착수 1번
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MoveRequest(10, 10))))
                .andExpect(status().isOk());

        // 무르기 성공
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isOk());

        // 다시 무르기 시도 (실패)
        mockMvc.perform(delete("/api/game/move"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("[ERROR] 무를 수 없습니다."));
    }
}