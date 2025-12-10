package com.lxp.user.domain.user.event;

import com.lxp.common.domain.event.BaseDomainEvent;

public class UserWithdrawEvent extends BaseDomainEvent {
    protected UserWithdrawEvent(String aggregateId) {
        super(aggregateId);
    }
}
