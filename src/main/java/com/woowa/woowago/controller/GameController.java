package com.woowa.woowago.controller;

import com.woowa.woowago.domain.Move;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.MoveDto;
import com.woowa.woowago.dto.MoveRequest;
import com.woowa.woowago.dto.MoveResponse;
import com.woowa.woowago.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 바둑 게임 API 컨트롤러
 */
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 새 게임 시작
     * POST /api/game/start
     */
    @PostMapping("/start")
    public ResponseEntity<GameStateResponse> startNewGame() {
        gameService.startNewGame();
        GameStateResponse response = gameService.getGameState();
        return ResponseEntity.ok(response);
    }

    /**
     * 착수
     * POST /api/game/move
     */
    @PostMapping("/move")
    public ResponseEntity<MoveResponse> move(@RequestBody MoveRequest request) {
        gameService.move(request.getX(), request.getY());
        GameStateResponse gameState = gameService.getGameState();
        MoveResponse response = new MoveResponse("착수 완료", gameState);
        return ResponseEntity.ok(response);
    }

    /**
     * 무르기
     * DELETE /api/game/move
     */
    @DeleteMapping("/move")
    public ResponseEntity<MoveResponse> undo() {
        gameService.undo();
        GameStateResponse gameState = gameService.getGameState();
        MoveResponse response = new MoveResponse("무르기 완료", gameState);
        return ResponseEntity.ok(response);
    }

    /**
     * 게임 상태 조회
     * GET /api/game
     */
    @GetMapping
    public ResponseEntity<GameStateResponse> getGameState() {
        GameStateResponse response = gameService.getGameState();
        return ResponseEntity.ok(response);
    }

    /**
     * 착수 기록 조회
     * GET /api/game/moves
     */
    @GetMapping("/moves")
    public ResponseEntity<List<MoveDto>> getMoveHistory() {
        List<Move> moves = gameService.getMoveHistory();
        List<MoveDto> dtos = moves.stream()
                .map(MoveDto::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}