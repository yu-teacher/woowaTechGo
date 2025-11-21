package com.woowa.woowago.controller;

import com.woowa.woowago.domain.*;
import com.woowa.woowago.dto.ErrorResponse;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.dto.websocket.*;
import com.woowa.woowago.service.GameRoomService;
import com.woowa.woowago.service.KataGoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 게임 컨트롤러
 * STOMP 메시지 처리
 */
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final KataGoService kataGoService;

    /**
     * 게임 방 입장 및 역할 배정
     * /app/game/join
     */
    @MessageMapping("/game/join")
    public void joinGame(GameJoinRequest request) {
        try {
            String role = roomService.joinRoom(request.getGameId(), request.getUsername());
            GameRoom room = roomService.getRoom(request.getGameId());

            // 방 전체에 입장 알림 + 게임 상태 전송
            GameStateResponse gameState = buildGameStateResponse(room.getGame());
            GameMessage message = new GameMessage("JOIN", gameState, request.getUsername());

            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 새 게임 시작 (참가자만)
     * /app/game/start
     */
    @MessageMapping("/game/start")
    public void startNewGame(GameActionRequest request) {
        try {
            GameRoom room = roomService.getRoom(request.getGameId());
            if (room == null) {
                throw new IllegalStateException("게임 방을 찾을 수 없습니다.");
            }

            // 참가자만 시작 가능
            String role = room.getRole(request.getUsername());
            if (!"player1".equals(role) && !"player2".equals(role)) {
                throw new IllegalArgumentException("참가자만 게임을 시작할 수 있습니다.");
            }

            // 게임 초기화
            room.resetGame();

            GameStateResponse gameState = buildGameStateResponse(room.getGame());
            GameMessage message = new GameMessage("START", gameState, request.getUsername());

            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 착수 (권한 체크)
     * /app/game/move
     */
    @MessageMapping("/game/move")
    public void makeMove(GameMoveRequest request) {
        try {
            GameRoom room = roomService.getRoom(request.getGameId());
            if (room == null) {
                throw new IllegalStateException("게임 방을 찾을 수 없습니다.");
            }

            // 참가자만 착수 가능
            String role = room.getRole(request.getUsername());
            if (!"player1".equals(role) && !"player2".equals(role)) {
                throw new IllegalArgumentException("참가자만 착수할 수 있습니다.");
            }

            // 착수 처리
            Position position = new Position(request.getX(), request.getY());
            room.getGame().move(position);

            GameStateResponse gameState = buildGameStateResponse(room.getGame());
            GameMessage message = new GameMessage("MOVE", gameState, request.getUsername());

            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 무르기 (참가자만)
     * /app/game/undo
     */
    @MessageMapping("/game/undo")
    public void undo(GameActionRequest request) {
        try {
            GameRoom room = roomService.getRoom(request.getGameId());
            if (room == null) {
                throw new IllegalStateException("게임 방을 찾을 수 없습니다.");
            }

            // 참가자만 무르기 가능
            String role = room.getRole(request.getUsername());
            if (!"player1".equals(role) && !"player2".equals(role)) {
                throw new IllegalArgumentException("참가자만 무르기를 할 수 있습니다.");
            }

            room.getGame().undo();

            GameStateResponse gameState = buildGameStateResponse(room.getGame());
            GameMessage message = new GameMessage("UNDO", gameState, request.getUsername());

            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 계가 (모든 사용자에게 브로드캐스트)
     * /app/game/score
     */
    @MessageMapping("/game/score")
    public void calculateScore(GameActionRequest request) {
        try {
            GameRoom room = roomService.getRoom(request.getGameId());
            if (room == null) {
                throw new IllegalStateException("게임 방을 찾을 수 없습니다.");
            }

            // KataGo로 계가 계산
            ScoreResponse scoreResponse = kataGoService.getScore(room.getGame());

            GameMessage message = new GameMessage("SCORE", scoreResponse, request.getUsername());
            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 방 퇴장
     * /app/game/leave
     */
    @MessageMapping("/game/leave")
    public void leaveGame(GameActionRequest request) {
        try {
            roomService.leaveRoom(request.getGameId(), request.getUsername());

            GameMessage message = new GameMessage("LEAVE", null, request.getUsername());
            broadcastToRoom(request.getGameId(), message);

        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 게임 상태 응답 생성
     */
    private GameStateResponse buildGameStateResponse(Game game) {
        Stone[][] board = convertBoardToArray(game);
        String currentTurn = game.getState().getCurrentTurn().name();
        int blackCaptures = game.getState().getBlackCaptures();
        int whiteCaptures = game.getState().getWhiteCaptures();
        int moveCount = game.getMoveHistory().size();

        return new GameStateResponse(board, currentTurn, blackCaptures, whiteCaptures, moveCount);
    }

    /**
     * Board를 2차원 배열로 변환
     */
    private Stone[][] convertBoardToArray(Game game) {
        Stone[][] boardArray = new Stone[19][19];
        for (int x = 1; x <= 19; x++) {
            for (int y = 1; y <= 19; y++) {
                boardArray[x - 1][y - 1] = game.getState()
                        .getBoard()
                        .getStone(new Position(x, y));
            }
        }
        return boardArray;
    }

    /**
     * 방 전체에 메시지 브로드캐스트
     */
    private void broadcastToRoom(String gameId, GameMessage message) {
        messagingTemplate.convertAndSend("/topic/game." + gameId, message);
    }

    /**
     * 에러 메시지 전송
     */
    private void sendError(String gameId, String username, String errorMessage) {
        GameMessage message = new GameMessage(
                "ERROR",
                new ErrorResponse("[ERROR] " + errorMessage),
                username
        );
        broadcastToRoom(gameId, message);
    }
}