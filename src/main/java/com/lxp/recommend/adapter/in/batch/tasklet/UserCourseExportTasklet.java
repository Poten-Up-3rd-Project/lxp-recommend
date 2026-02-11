package com.lxp.recommend.adapter.in.batch.tasklet;

import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport;
import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport.CourseParquetRecord;
import com.lxp.recommend.adapter.in.batch.writer.ParquetWriterSupport.UserParquetRecord;
import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.application.port.out.CourseRepository;
import com.lxp.recommend.application.port.out.UserRepository;
import com.lxp.recommend.domain.course.entity.RecommendCourse;
import com.lxp.recommend.domain.user.entity.RecommendUser;
import com.lxp.recommend.domain.user.entity.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCourseExportTasklet implements Tasklet {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String batchId = (String) chunkContext.getStepContext()
                .getJobParameters().get("batchId");

        log.info("Starting export for batch: {}", batchId);

        List<RecommendUser> users = userRepository.findByStatus(Status.ACTIVE);
        List<RecommendCourse> courses = courseRepository.findByStatus(Status.ACTIVE);

        log.info("Exporting {} users and {} courses", users.size(), courses.size());

        List<UserParquetRecord> userRecords = users.stream()
                .map(this::toUserRecord)
                .collect(Collectors.toList());

        List<CourseParquetRecord> courseRecords = courses.stream()
                .map(this::toCourseRecord)
                .collect(Collectors.toList());

        Path usersPath = ParquetWriterSupport.writeUsersParquet(userRecords);
        Path coursesPath = ParquetWriterSupport.writeCoursesParquet(courseRecords);

        chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext()
                .put("usersParquetPath", usersPath.toString());
        chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext()
                .put("coursesParquetPath", coursesPath.toString());

        log.info("Export completed. Users: {}, Courses: {}", usersPath, coursesPath);

        return RepeatStatus.FINISHED;
    }

    private UserParquetRecord toUserRecord(RecommendUser user) {
        return new UserParquetRecord(
                user.getId(),
                user.getInterestTags(),
                user.getLevel().getValue(),
                user.getEnrolledCourseIds(),
                user.getCreatedCourseIds()
        );
    }

    private CourseParquetRecord toCourseRecord(RecommendCourse course) {
        return new CourseParquetRecord(
                course.getId(),
                course.getTags(),
                course.getLevel().getValue()
        );
    }
}
