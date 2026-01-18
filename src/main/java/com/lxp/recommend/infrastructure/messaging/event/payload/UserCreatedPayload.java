package com.lxp.recommend.infrastructure.messaging.event.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserCreatedPayload {
    String userId;
    List<Long> tagIds;
    String level;
}
