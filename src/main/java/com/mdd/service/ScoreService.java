package com.mdd.service;

public interface ScoreService {
    double getScore(long sourceId, long targetId, int trustLevel);

    void updateScore(long sourceId, long targetId, int level);
}
