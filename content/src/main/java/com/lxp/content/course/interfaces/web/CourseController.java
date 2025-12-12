package com.lxp.content.course.interfaces.web;

import com.lxp.content.course.interfaces.web.dto.response.CourseDetailResponse;
import com.lxp.content.course.interfaces.web.dto.reuqest.create.CourseCreateRequest;
import com.lxp.content.course.interfaces.web.mapper.CourseApplicationFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseApplicationFacade facade;

    @PostMapping
    public ResponseEntity<CourseDetailResponse> create(
           @RequestBody CourseCreateRequest input) {
        CourseDetailResponse response = facade.createCourse(input);
        return ResponseEntity.ok(response);
    }
}
