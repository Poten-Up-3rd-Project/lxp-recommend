package com.lxp.recommend.application.service;

import com.lxp.recommend.application.port.in.EventIdempotencyUseCase;
import com.lxp.recommend.application.port.out.ProcessedEventRepository;
import com.lxp.recommend.domain.event.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventIdempotencyService implements EventIdempotencyUseCase {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    public boolean isDuplicate(String eventId) {
        return processedEventRepository.existsByEventId(eventId);
    }

    @Override
    public void markAsProcessed(String eventId) {
        processedEventRepository.save(new ProcessedEvent(eventId));
        log.debug("Marked event as processed: {}", eventId);
    }
}
