package com.lxp.recommend.application.port.in;

import com.lxp.recommend.dto.response.EngineCallbackResponse;

public interface RecommendResultUseCase {

    void processCallback(EngineCallbackResponse callback);
}
