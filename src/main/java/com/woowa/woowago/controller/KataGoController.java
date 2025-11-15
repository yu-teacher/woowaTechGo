package com.woowa.woowago.controller;

import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.service.KataGoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/katago")
public class KataGoController {

    private final KataGoService kataGoService;

    public KataGoController(KataGoService kataGoService) {
        this.kataGoService = kataGoService;
    }

    /**
     * 착수 추천 (블루스팟)
     * GET /api/katago/bluespots
     */
    @GetMapping("/bluespots")
    public ResponseEntity<BlueSpotsResponse> getBlueSpots() {
        BlueSpotsResponse response = kataGoService.getBlueSpots();
        return ResponseEntity.ok(response);
    }

    /**
     * 계가
     * GET /api/katago/score
     */
    @GetMapping("/score")
    public ResponseEntity<ScoreResponse> getScore() {
        ScoreResponse response = kataGoService.getScore();
        return ResponseEntity.ok(response);
    }
}