package com.lxp.recommend.infrastructure.messaging.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lxp.common.infrastructure.event.BaseConsumedEvent;
import com.lxp.recommend.infrastructure.messaging.event.payload.CourseCreatedPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCreatedEvent extends BaseConsumedEvent<CourseCreatedPayload> {
}