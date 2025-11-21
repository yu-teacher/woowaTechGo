package com.woowa.woowago.service;

import com.woowa.woowago.client.KataGoClient;
import com.woowa.woowago.domain.Game;
import com.woowa.woowago.domain.Move;
import com.woowa.woowago.domain.Position;
import com.woowa.woowago.domain.Stone;
import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.util.GtpCoordinateConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class KataGoService {

    private final KataGoClient kataGoClient;
    private final GameService gameService;

    public KataGoService(KataGoClient kataGoClient, GameService gameService) {
        this.kataGoClient = kataGoClient;
        this.gameService = gameService;
    }

    /**
     * 착수 추천 (블루스팟) - 싱글플레이어용
     */
    public BlueSpotsResponse getBlueSpots() {
        Game game = gameService.getGame();
        return getBlueSpots(game);
    }

    /**
     * 착수 추천 (블루스팟) - 멀티플레이어용
     * @param game 특정 GameRoom의 Game 인스턴스
     */
    public BlueSpotsResponse getBlueSpots(Game game) {
        try {
            // KataGo 시작 및 현재 게임 상태 반영
            syncGameToKataGo(game);

            // 다음 차례 색상
            Stone nextPlayer = game.getState().getCurrentTurn();
            String color = GtpCoordinateConverter.toGtpColor(nextPlayer);

            // 착수 추천
            String move = kataGoClient.genmove(color);

            // GTP 좌표를 Position으로 변환
            Position recommendedPosition = GtpCoordinateConverter.fromGtpCoordinate(move);

            return new BlueSpotsResponse(recommendedPosition.getX(), recommendedPosition.getY());

        } catch (Exception e) {
            throw new RuntimeException("착수 추천 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 계가 - 싱글플레이어용
     */
    public ScoreResponse getScore() {
        Game game = gameService.getGame();
        return getScore(game);
    }

    /**
     * 계가 - 멀티플레이어용
     * @param game 특정 GameRoom의 Game 인스턴스
     */
    public ScoreResponse getScore(Game game) {
        try {
            // KataGo 시작 및 현재 게임 상태 반영
            syncGameToKataGo(game);

            // 점수 계산
            String scoreResult = kataGoClient.finalScore();

            return new ScoreResponse(scoreResult);

        } catch (Exception e) {
            throw new RuntimeException("계가 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 게임 상태를 KataGo에 동기화 - 싱글플레이어용
     */
    private void syncGameToKataGo() throws IOException, InterruptedException {
        Game game = gameService.getGame();
        syncGameToKataGo(game);
    }

    /**
     * 현재 게임 상태를 KataGo에 동기화 - 멀티플레이어용
     * @param game 동기화할 Game 인스턴스
     */
    private void syncGameToKataGo(Game game) throws IOException, InterruptedException {
        // KataGo 시작
        kataGoClient.start();
        kataGoClient.setBoardSize(19);
        kataGoClient.clearBoard();

        // 모든 착수 재현
        List<Move> moves = game.getMoveHistory();
        for (Move move : moves) {
            Position pos = move.getPosition();
            Stone color = move.getColor();

            String gtpColor = GtpCoordinateConverter.toGtpColor(color);
            String gtpPos = GtpCoordinateConverter.toGtpCoordinate(pos);

            kataGoClient.play(gtpColor, gtpPos);
        }
    }
}