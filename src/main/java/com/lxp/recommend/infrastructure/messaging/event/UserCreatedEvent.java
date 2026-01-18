package com.lxp.recommend.infrastructure.messaging.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lxp.common.infrastructure.event.BaseConsumedEvent;
import com.lxp.recommend.infrastructure.messaging.event.payload.UserCreatedPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreatedEvent extends BaseConsumedEvent<UserCreatedPayload> {
}
