package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.application.port.out.ProcessedEventRepository;
import com.lxp.recommend.domain.event.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository jpaRepository;

    @Override
    public boolean existsByEventId(String eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    public ProcessedEvent save(ProcessedEvent event) {
        return jpaRepository.save(event);
    }
}
