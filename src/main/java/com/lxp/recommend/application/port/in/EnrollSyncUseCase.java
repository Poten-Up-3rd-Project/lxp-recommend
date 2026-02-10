package com.lxp.recommend.application.port.in;

import com.lxp.recommend.application.dto.EnrollSyncCommand;

public interface EnrollSyncUseCase {

    void createEnrollment(EnrollSyncCommand command);

    void deleteEnrollment(EnrollSyncCommand command);
}
