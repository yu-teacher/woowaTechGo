package com.woowa.woowago.service;

import com.woowa.woowago.client.KataGoClient;
import com.woowa.woowago.config.KataGoProperties;
import com.woowa.woowago.domain.game.Game;
import com.woowa.woowago.domain.move.Move;
import com.woowa.woowago.domain.game.Position;
import com.woowa.woowago.domain.game.Stone;
import com.woowa.woowago.dto.BlueSpotsResponse;
import com.woowa.woowago.dto.ScoreResponse;
import com.woowa.woowago.util.GtpCoordinateConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class KataGoService {

    private final KataGoProperties kataGoProperties;
    private final GameService gameService;

    public KataGoService(KataGoProperties kataGoProperties, GameService gameService) {
        this.kataGoProperties = kataGoProperties;
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
     */
    public BlueSpotsResponse getBlueSpots(Game game) {
        KataGoClient client = null;
        try {
            // 독립적인 KataGo 클라이언트 생성
            client = new KataGoClient(kataGoProperties);

            // 게임 상태 동기화
            syncGameToKataGo(client, game);

            // 다음 차례 색상
            Stone nextPlayer = game.getState().getCurrentTurn();
            String color = GtpCoordinateConverter.toGtpColor(nextPlayer);

            // 착수 추천
            String move = client.genmove(color);
            log.info("===== KataGo genmove 응답: [{}] =====", move);

            // GTP 좌표를 Position으로 변환
            Position recommendedPosition = GtpCoordinateConverter.fromGtpCoordinate(move);

            return new BlueSpotsResponse(recommendedPosition.getX(), recommendedPosition.getY());

        } catch (Exception e) {
            throw new RuntimeException("착수 추천 실패: " + e.getMessage(), e);
        } finally {
            // 반드시 종료
            if (client != null) {
                try {
                    client.stop();
                } catch (Exception e) {
                    log.warn("KataGo 종료 실패", e);
                }
            }
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
     */
    public ScoreResponse getScore(Game game) {
        KataGoClient client = null;
        try {
            // 독립적인 KataGo 클라이언트 생성
            client = new KataGoClient(kataGoProperties);

            // 게임 상태 동기화
            syncGameToKataGo(client, game);

            // 점수 계산
            String scoreResult = client.finalScore();

            return new ScoreResponse(scoreResult);

        } catch (Exception e) {
            throw new RuntimeException("계가 실패: " + e.getMessage(), e);
        } finally {
            // 반드시 종료
            if (client != null) {
                try {
                    client.stop();
                } catch (Exception e) {
                    log.warn("KataGo 종료 실패", e);
                }
            }
        }
    }

    /**
     * 현재 게임 상태를 KataGo에 동기화
     */
    private void syncGameToKataGo(KataGoClient client, Game game) throws IOException, InterruptedException {
        // KataGo 시작
        client.start();
        client.setBoardSize(19);
        client.clearBoard();

        // 모든 착수 재현
        List<Move> moves = game.getMoveHistory();
        for (Move move : moves) {
            Position pos = move.getPosition();
            Stone color = move.getColor();

            String gtpColor = GtpCoordinateConverter.toGtpColor(color);
            String gtpPos = GtpCoordinateConverter.toGtpCoordinate(pos);

            client.play(gtpColor, gtpPos);
        }
    }
}