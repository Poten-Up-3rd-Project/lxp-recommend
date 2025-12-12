package com.lxp.enrollment.interfaces;

import com.lxp.enrollment.application.port.provided.dto.command.CancelEnrollmentCommand;
import com.lxp.enrollment.application.port.provided.dto.command.EnrollCourseCommand;
import com.lxp.enrollment.application.port.provided.dto.result.CancelEnrollmentResult;
import com.lxp.enrollment.application.port.provided.dto.result.EnrollCourseResult;
import com.lxp.enrollment.application.usecase.CancelEnrollmentUseCase;
import com.lxp.enrollment.application.usecase.EnrollCourseUseCase;
import com.lxp.enrollment.interfaces.dto.request.CancelEnrollmentRequest;
import com.lxp.enrollment.interfaces.dto.request.EnrollCourseRequest;
import com.lxp.enrollment.interfaces.dto.response.CancelEnrollmentResponse;
import com.lxp.enrollment.interfaces.dto.response.EnrollCourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentCommandController {
    private final EnrollCourseUseCase enrollCourseUseCase;
    private final CancelEnrollmentUseCase cancelEnrollmentUseCase;

    @PostMapping
    public ResponseEntity<EnrollCourseResponse> enrollCourse(@RequestBody EnrollCourseRequest request) {
        EnrollCourseResult result = enrollCourseUseCase.enroll(new EnrollCourseCommand(
                request.userId(),
                request.courseId()
        ));
        EnrollCourseResponse response = new EnrollCourseResponse(
                result.enrollmentId(),
                result.state(),
                result.enrolledAt()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{enrollmentId}/cancel")
    public ResponseEntity<CancelEnrollmentResponse> cancelEnrollment(
            @PathVariable("enrollmentId") Long enrollmentId,
            @RequestBody CancelEnrollmentRequest request
    ) {
        CancelEnrollmentResult result = cancelEnrollmentUseCase.cancel(
                new CancelEnrollmentCommand(
                        enrollmentId,
                        request.reason()
                )
        );
        return ResponseEntity.ok(new CancelEnrollmentResponse(
                result.enrollmentId(),
                result.state(),
                result.enrolledAt()
        ));
    }
}
