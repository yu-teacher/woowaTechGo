package com.woowa.woowago.controller;

import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.service.GameRoomService;
import com.woowa.woowago.service.KataGoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/katago")
@RequiredArgsConstructor
public class KataGoController {

    private final KataGoService kataGoService;
    private final GameRoomService gameRoomService;

    /**
     * 착수 추천 (블루스팟) - 싱글플레이어용
     * GET /api/katago/bluespots
     */
    @GetMapping("/bluespots")
    public ResponseEntity<BlueSpotsResponse> getBlueSpots() {
        BlueSpotsResponse response = kataGoService.getBlueSpots();
        return ResponseEntity.ok(response);
    }

    /**
     * 착수 추천 (블루스팟) - 멀티플레이어용
     * GET /api/katago/bluespots/{gameId}
     */
    @GetMapping("/bluespots/{gameId}")
    public ResponseEntity<BlueSpotsResponse> getBlueSpotsForGame(@PathVariable("gameId") String gameId) {
        BlueSpotsResponse response = gameRoomService.blueSpots(gameId);
        return ResponseEntity.ok(response);
    }

    /**
     * 계가 (형세 판단) - 싱글플레이어용
     * GET /api/katago/score
     */
    @GetMapping("/score")
    public ResponseEntity<ScoreResponse> getScore() {
        ScoreResponse response = kataGoService.getScore();
        return ResponseEntity.ok(response);
    }

    /**
     * 계가 (형세 판단) - 멀티플레이어용
     * GET /api/katago/score/{gameId}
     */
    @GetMapping("/score/{gameId}")
    public ResponseEntity<ScoreResponse> getScoreForGame(@PathVariable("gameId") String gameId) {
        ScoreResponse response = gameRoomService.score(gameId);
        return ResponseEntity.ok(response);
    }
}