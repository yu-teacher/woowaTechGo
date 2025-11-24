package com.woowa.woowago.controller;

import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.service.GameRoomService;
import com.woowa.woowago.service.KataGoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/katago")
@RequiredArgsConstructor
public class KataGoController {

    private final KataGoService kataGoService;
    private final GameRoomService gameRoomService;

    @GetMapping("/bluespots")
    public ResponseEntity<BlueSpotsResponse> getBlueSpots() {
        try {
            BlueSpotsResponse response = kataGoService.getBlueSpots();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("===== KataGo 블루스팟 에러 =====", e);
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/bluespots/{gameId}")
    public ResponseEntity<BlueSpotsResponse> getBlueSpotsForGame(@PathVariable("gameId") String gameId) {
        try {
            BlueSpotsResponse response = gameRoomService.blueSpots(gameId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("===== KataGo 블루스팟 에러 (gameId: {}) =====", gameId, e);
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/score")
    public ResponseEntity<ScoreResponse> getScore() {
        try {
            ScoreResponse response = kataGoService.getScore();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("===== KataGo 점수 에러 =====", e);
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/score/{gameId}")
    public ResponseEntity<ScoreResponse> getScoreForGame(@PathVariable("gameId") String gameId) {
        try {
            ScoreResponse response = gameRoomService.score(gameId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("===== KataGo 점수 에러 (gameId: {}) =====", gameId, e);
            e.printStackTrace();
            throw e;
        }
    }
}