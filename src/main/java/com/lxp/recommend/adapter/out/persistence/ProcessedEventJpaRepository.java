package com.lxp.recommend.adapter.out.persistence;

import com.lxp.recommend.domain.event.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, String> {
}
