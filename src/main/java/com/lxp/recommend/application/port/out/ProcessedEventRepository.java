package com.lxp.recommend.application.port.out;

import com.lxp.recommend.domain.event.entity.ProcessedEvent;

public interface ProcessedEventRepository {

    boolean existsByEventId(String eventId);

    ProcessedEvent save(ProcessedEvent event);
}
