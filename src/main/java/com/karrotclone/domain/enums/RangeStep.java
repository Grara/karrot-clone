package com.karrotclone.domain.enums;

/**
 * 글 공개, 글 탐색 범위를 나타내는 enum입니다.
 */

public enum RangeStep {
    VERY_CLOSE(20000),
    CLOSE(30000),
    MIDDLE(40000),
    FAR(50000);

    private int distance;

    RangeStep(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
