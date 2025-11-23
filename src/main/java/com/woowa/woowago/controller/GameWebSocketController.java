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
     * 새 게임 시작 (참가자만) - 기존 방식 (하위 호환)
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
     * 무르기 (참가자만) - 기존 방식 (하위 호환)
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
     * 형세 판단 (게임 계속)
     * /app/game/analysis
     */
    @MessageMapping("/game/analysis")
    public void analysis(GameActionRequest request) {
        try {
            ScoreResponse response = roomService.analysis(request.getGameId());
            GameMessage message = new GameMessage("ANALYSIS", response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 계가 (모든 사용자에게 브로드캐스트) - 게임 종료
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
     * 게임 시작 요청
     * /app/game/request/start
     */
    @MessageMapping("/game/request/start")
    public void requestStart(GameActionRequest request) {
        try {
            RequestMessage response = roomService.requestStart(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("REQUEST_START", response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 무르기 요청
     * /app/game/request/undo
     */
    @MessageMapping("/game/request/undo")
    public void requestUndo(GameActionRequest request) {
        try {
            RequestMessage response = roomService.requestUndo(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("REQUEST_UNDO", response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 계가 요청
     * /app/game/request/score
     */
    @MessageMapping("/game/request/score")
    public void requestScore(GameActionRequest request) {
        try {
            RequestMessage response = roomService.requestScore(request.getGameId(), request.getUsername());
            GameMessage message = new GameMessage("REQUEST_SCORE", response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 게임 시작 응답
     * /app/game/respond/start
     */
    @MessageMapping("/game/respond/start")
    public void respondStart(RespondRequest request) {
        try {
            Object response = roomService.respondStart(
                    request.getGameId(),
                    request.getUsername(),
                    request.isAccepted()
            );

            String messageType = request.isAccepted() ? "START" : "RESPOND_START";
            GameMessage message = new GameMessage(messageType, response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 무르기 응답
     * /app/game/respond/undo
     */
    @MessageMapping("/game/respond/undo")
    public void respondUndo(RespondRequest request) {
        try {
            Object response = roomService.respondUndo(
                    request.getGameId(),
                    request.getUsername(),
                    request.isAccepted()
            );

            String messageType = request.isAccepted() ? "UNDO" : "RESPOND_UNDO";
            GameMessage message = new GameMessage(messageType, response, request.getUsername());
            broadcastToRoom(request.getGameId(), message);
        } catch (Exception e) {
            sendError(request.getGameId(), request.getUsername(), e.getMessage());
        }
    }

    /**
     * 계가 응답 (수락 시 게임 종료)
     */
    @MessageMapping("/game/respond/score")
    public void respondScore(RespondRequest request) {
        try {
            Object response = roomService.respondScore(
                    request.getGameId(),
                    request.getUsername(),
                    request.isAccepted()
            );

            String messageType = request.isAccepted() ? "SCORE" : "RESPOND_SCORE";
            GameMessage message = new GameMessage(messageType, response, request.getUsername());
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