package com.woowa.woowago.controller;

import com.woowa.woowago.dto.ErrorResponse;
import com.woowa.woowago.dto.GameStateResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.dto.websocket.*;
import com.woowa.woowago.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 게임 컨트롤러
 * STOMP 메시지 처리 (라우팅만 담당)
 */
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameRoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 게임 방 입장 및 역할 배정
     * /app/game/join
     */
    @MessageMapping("/game/join")
    public void joinGame(GameJoinRequest request) {
        try {
            JoinResponse response = roomService.join(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("JOIN", response, request.getUsername());
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
            StartResponse response = roomService.start(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("START", response, request.getUsername());
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
            GameStateResponse response = roomService.move(
                    request.getGameId(),
                    request.getUsername(),
                    request.getX(),
                    request.getY()
            );
            GameMessage message = new GameMessage("MOVE", response, request.getUsername());
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
            GameStateResponse response = roomService.undo(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("UNDO", response, request.getUsername());
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
            ScoreResponse response = roomService.score(request.getGameId());
            GameMessage message = new GameMessage("SCORE", response, request.getUsername());
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
                new ErrorResponse(errorMessage),
                username
        );
        broadcastToRoom(gameId, message);
    }
}