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
     * 착수 추천 (블루스팟)
     */
    public BlueSpotsResponse getBlueSpots() {
        try {
            // KataGo 시작 및 현재 게임 상태 반영
            syncGameToKataGo();

            // 다음 차례 색상
            Game game = gameService.getGame();
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
     * 계가
     */
    public ScoreResponse getScore() {
        try {
            // KataGo 시작 및 현재 게임 상태 반영
            syncGameToKataGo();

            // 점수 계산
            String scoreResult = kataGoClient.finalScore();

            return new ScoreResponse(scoreResult);

        } catch (Exception e) {
            // KataGo 실패 시 간단한 계가로 fallback (TODO: 나중에 구현)
            throw new RuntimeException("계가 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 게임 상태를 KataGo에 동기화
     */
    private void syncGameToKataGo() throws IOException, InterruptedException {
        Game game = gameService.getGame();

        // KataGo 시작
        kataGoClient.start();
        kataGoClient.setBoardSize(19);
        kataGoClient.clearBoard();

        // 모든 착수 재현
        List<Move> moves = game.getMoveHistory();
        for (Move move : moves) {
            Position pos = move.getPosition();
            Stone color = move.getColor();  // ← 수정!

            String gtpColor = GtpCoordinateConverter.toGtpColor(color);
            String gtpPos = GtpCoordinateConverter.toGtpCoordinate(pos);

            kataGoClient.play(gtpColor, gtpPos);
        }
    }
}