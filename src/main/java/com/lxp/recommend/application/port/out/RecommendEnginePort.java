package com.lxp.recommend.application.port.out;

public interface RecommendEnginePort {

    void requestProcess(String batchId, String usersFilePath, String coursesFilePath, int topK, String callbackUrl);
}
