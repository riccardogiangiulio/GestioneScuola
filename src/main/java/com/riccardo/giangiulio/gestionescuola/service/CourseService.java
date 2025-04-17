package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;
import java.util.Optional;
    
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;
import com.riccardo.giangiulio.gestionescuola.model.Subject;
import com.riccardo.giangiulio.gestionescuola.repository.CourseRepository;

@Service
public class CourseService {
    
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);
    
    private final CourseRepository courseRepository;
    private final SubjectService subjectService;
    private final ExamService examService;
    
    @Autowired
    public CourseService(CourseRepository courseRepository, SubjectService subjectService, ExamService examService) {
        this.courseRepository = courseRepository;
        this.subjectService = subjectService;
        this.examService = examService;
        log.info("CourseService initialized");
    }
    
    public List<Course> findAll() {
        log.debug("Retrieving all courses");
        List<Course> courses = courseRepository.findAll();
        log.info("Retrieved {} courses", courses.size());
        return courses;
    }
    
    public Course findById(Long id) {
        log.debug("Finding course with id: {}", id);
        return courseRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Course not found with ID: {}", id);
                return new RuntimeException("Course not found with ID: " + id);
            });
    }
    
    @Transactional
    public Course save(Course course) {
        log.info("Saving course: {}", course.getTitle());
        Course savedCourse = courseRepository.save(course);
        log.info("Course saved successfully with title: {}", savedCourse.getTitle());
        return savedCourse;
    }
    
    @Transactional
    public Course update(Long id, Course course) {
        log.info("Updating course with id: {}", id);
        Optional<Course> existingCourseOptional = courseRepository.findById(id);

        if (!existingCourseOptional.isPresent()) {
            log.error("Failed to update course: Not found with ID: {}", id);
            throw new RuntimeException("Course not found with ID: " + id);
        }

        Course existingCourse = existingCourseOptional.get();
        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setDuration(course.getDuration());
        existingCourse.setPrice(course.getPrice());
        existingCourse.setSubjects(course.getSubjects());
        
        Course updatedCourse = courseRepository.save(existingCourse);
        log.info("Course updated successfully with ID: {}", id);
        return updatedCourse;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete course with id: {}", id);
        if (!courseRepository.existsById(id)) {
            log.error("Failed to delete course: Not found with ID: {}", id);
            throw new RuntimeException("Course not found with ID: " + id);
        }
        courseRepository.deleteById(id);
        log.info("Course deleted successfully with ID: {}", id);
    }
    
    public List<Course> findByTitle(String title) {
        log.debug("Finding courses by title: {}", title);
        List<Course> courses = courseRepository.findByTitle(title);
        if (courses.isEmpty()) {
            log.warn("No courses found with title: {}", title);
        } else {
            log.info("Found {} courses with title: {}", courses.size(), title);
        }
        return courses;
    }
    
    public List<Course> findByTitleContaining(String keyword) {
        log.debug("Finding courses by title containing: {}", keyword);
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(keyword);
        if (courses.isEmpty()) {
            log.warn("No courses found containing keyword: {}", keyword);
        } else {
            log.info("Found {} courses containing keyword: {}", courses.size(), keyword);
        }
        return courses;
    }
    
    @Transactional
    public Course addSubject(Long courseId, Long subjectId) {
        log.info("Adding subject {} to course {}", subjectId, courseId);
        Course course = findById(courseId);
        Subject subject = subjectService.findById(subjectId);
        
        if (course.getSubjects().stream().anyMatch(s -> s.getId().equals(subjectId))) {
            log.warn("Subject {} is already associated with course {}", subjectId, courseId);
            return course;
        }
        
        course.getSubjects().add(subject);
        Course updatedCourse = courseRepository.save(course);
        log.info("Subject {} added successfully to course {}", subjectId, courseId);
        return updatedCourse;
    }
    
    @Transactional
    public Course removeSubject(Long courseId, Long subjectId) {
        log.warn("Removing subject {} from course {}", subjectId, courseId);
        Course course = findById(courseId);
        
        boolean removed = course.getSubjects().removeIf(subject -> subject.getId().equals(subjectId));
        if (!removed) {
            log.warn("Subject {} was not associated with course {}", subjectId, courseId);
            return course;
        }
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Subject {} removed successfully from course {}", subjectId, courseId);
        return updatedCourse;
    }
    
    @Transactional
    public Course addExam(Long courseId, Long examId) {
        log.info("Adding exam {} to course {}", examId, courseId);
        Course course = findById(courseId);
        Exam exam = examService.findById(examId);
        
        if (course.getExams().stream().anyMatch(e -> e.getId().equals(examId))) {
            log.warn("Exam {} is already associated with course {}", examId, courseId);
            return course;
        }
        
        course.getExams().add(exam);
        Course updatedCourse = courseRepository.save(course);
        log.info("Exam {} added successfully to course {}", examId, courseId);
        return updatedCourse;
    }
    
    @Transactional
    public Course removeExam(Long courseId, Long examId) {
        log.warn("Removing exam {} from course {}", examId, courseId);
        Course course = findById(courseId);
        
        boolean removed = course.getExams().removeIf(exam -> exam.getId().equals(examId));
        if (!removed) {
            log.warn("Exam {} was not associated with course {}", examId, courseId);
            return course;
        }
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Exam {} removed successfully from course {}", examId, courseId);
        return updatedCourse;
    }
}
