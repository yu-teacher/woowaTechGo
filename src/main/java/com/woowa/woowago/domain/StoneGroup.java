package com.woowa.woowago.domain;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * 바둑돌 그룹 분석을 위한 유틸리티 클래스
 * 연결된 돌 찾기, 활로 계산, 죽은 그룹 찾기 기능 제공
 */
public class StoneGroup {

    /**
     * 특정 위치에서 연결된 같은 색 돌 그룹을 BFS로 찾기
     * @param board 바둑판
     * @param start 시작 위치
     * @return 연결된 같은 색 돌들의 위치 집합
     */
    public static Set<Position> findConnectedGroup(Board board, Position start) {
        Stone color = board.getStone(start);
        if (color == Stone.EMPTY) {
            return Set.of();
        }

        Set<Position> group = new HashSet<>();
        Queue<Position> queue = new ArrayDeque<>();

        queue.offer(start);
        group.add(start);

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            for (Position adjacent : current.getAdjacentPositions()) {
                if (board.getStone(adjacent) == color && !group.contains(adjacent)) {
                    group.add(adjacent);
                    queue.offer(adjacent);
                }
            }
        }

        return group;
    }

    /**
     * 그룹의 활로(liberty) 개수 계산
     * 활로 = 그룹에 인접한 빈 교점의 개수
     * @param board 바둑판
     * @param group 돌 그룹
     * @return 활로 개수
     */
    public static int countLiberties(Board board, Set<Position> group) {
        Set<Position> liberties = new HashSet<>();

        for (Position position : group) {
            for (Position adjacent : position.getAdjacentPositions()) {
                if (board.getStone(adjacent) == Stone.EMPTY) {
                    liberties.add(adjacent);
                }
            }
        }

        return liberties.size();
    }
}
