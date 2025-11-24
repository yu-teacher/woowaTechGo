package com.woowa.woowago.client;

import com.woowa.woowago.config.KataGoProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KataGo와 GTP 프로토콜로 통신하는 클라이언트
 */
@Slf4j
public class KataGoClient {

    private final KataGoProperties properties;

    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public KataGoClient(KataGoProperties properties) {
        this.properties = properties;
    }

    public void start() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                properties.getPath(),
                "gtp",
                "-config", properties.getConfig(),
                "-model", properties.getModel()
        );

        builder.redirectErrorStream(true);

        process = builder.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        Thread.sleep(2000);  // KataGo 초기화 대기

        if (!process.isAlive()) {
            throw new RuntimeException("KataGo 시작 실패");
        }

        sendCommand("time_settings 0 3 1");
    }

    /**
     * GTP 명령 전송 및 응답 받기
     */
    public String sendCommand(String command) throws IOException, InterruptedException {
        return sendCommand(command, 5000);
    }

    /**
     * GTP 명령 전송 (타임아웃 지정)
     */
    public String sendCommand(String command, long timeout) throws IOException, InterruptedException {
        if (process == null || !process.isAlive()) {
            throw new IllegalStateException("KataGo가 시작되지 않았습니다.");
        }

        // 명령 전송
        writer.write(command + "\n");
        writer.flush();

        // 응답 읽기
        StringBuilder response = new StringBuilder();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout) {
            if (reader.ready()) {
                String line = reader.readLine();
                if (line == null) break;

                response.append(line).append("\n");

                // 빈 줄이 응답 끝
                if (line.trim().isEmpty() && !response.isEmpty()) {
                    break;
                }
            } else {
                Thread.sleep(50);
            }
        }

        return response.toString().trim();
    }

    /**
     * 바둑판 초기화
     */
    public void clearBoard() throws IOException, InterruptedException {
        sendCommand("clear_board");
    }

    /**
     * 바둑판 크기 설정
     */
    public void setBoardSize(int size) throws IOException, InterruptedException {
        sendCommand("boardsize " + size);
    }

    /**
     * 돌 놓기
     */
    public void play(String color, String position) throws IOException, InterruptedException {
        sendCommand("play " + color + " " + position);
    }

    /**
     * 착수 추천
     */
    public String genmove(String color) throws IOException, InterruptedException {
        String response = sendCommand("genmove " + color);
        return parseResponse(response);
    }

    /**
     * 점수 계산
     */
    public String finalScore() throws IOException, InterruptedException {
        String response = sendCommand("kata-raw-nn 0", 3000);
        log.info("===== kata-raw-nn 응답: [{}] =====", response);  // 로그 추가
        return parseKataScore(response);
    }

    /**
     * KataGo JSON 응답에서 점수 파싱
     */
    private String parseKataScore(String response) {
        // whiteLead 찾기
        Pattern pattern = Pattern.compile("whiteLead\\s+(-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            double score = Double.parseDouble(matcher.group(1));
            if (score > 0) {
                return String.format("W+%.1f", score);
            } else {
                return String.format("B+%.1f", Math.abs(score));
            }
        }

        throw new RuntimeException("점수 파싱 실패");
    }

    /**
     * GTP 응답 파싱 (= 제거)
     */
    private String parseResponse(String response) {
        if (response.startsWith("=")) {
            return response.substring(1).trim();
        }
        if (response.startsWith("?")) {
            throw new RuntimeException("KataGo error: " + response);
        }
        return response;
    }

    /**
     * KataGo 프로세스 종료
     */
    public void stop() throws IOException, InterruptedException {
        if (process != null && process.isAlive()) {
            sendCommand("quit");
            process.destroy();
        }
    }
}