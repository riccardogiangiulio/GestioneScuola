package com.riccardo.giangiulio.gestionescuola.service;

import java.util.List;
import java.util.Optional;
    
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.CourseNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.ExamNotFoundException;
import com.riccardo.giangiulio.gestionescuola.exception.NotFoundException.SubjectNotFoundException;
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
                return new CourseNotFoundException(id);
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
            throw new CourseNotFoundException(id);
        }

        Course existingCourse = existingCourseOptional.get();
        if (course.getTitle() != null && !course.getTitle().isEmpty()) {
            existingCourse.setTitle(course.getTitle());
        }
        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            existingCourse.setDescription(course.getDescription());
        }
        if (course.getDuration() != null && !course.getDuration().isEmpty()) {
            existingCourse.setDuration(course.getDuration());
        }
        if (course.getPrice() != null) {
            existingCourse.setPrice(course.getPrice());
        }
        if (course.getSubjects() != null && !course.getSubjects().isEmpty()) {
            existingCourse.setSubjects(course.getSubjects());
        }
        if (course.getExams() != null && !course.getExams().isEmpty()) {
            existingCourse.setExams(course.getExams());
        }
        Course updatedCourse = courseRepository.save(existingCourse);
        log.info("Course updated successfully with ID: {}", id);
        return updatedCourse;
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete course with id: {}", id);
        if (!courseRepository.existsById(id)) {
            log.error("Failed to delete course: Not found with ID: {}", id);
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
        log.info("Course deleted successfully with ID: {}", id);
    }
    
    public Course findByTitle(String title) {
        log.debug("Finding courses by title: {}", title);
        Optional<Course> course = courseRepository.findByTitle(title);
        if (course.isPresent()) {
            log.info("Found course with title: {}", title);
            return course.get();
        } else {
            log.error("No courses found with title: {}", title);
            throw new CourseNotFoundException(title);
        }
    }
    
    @Transactional
    public void addSubject(Long courseId, Long subjectId) {
        log.info("Attempting to add subject {} to course {}", subjectId, courseId);
        
        // Utilizza SubjectService.findById che a sua volta lancia SubjectNotFoundException
        Course course = findById(courseId);
        Subject subject = subjectService.findById(subjectId);
        
        if (course.getSubjects().contains(subject)) {
            log.warn("Subject {} is already associated with course {}", subjectId, courseId);
            return;
        }
        
        course.getSubjects().add(subject);
        courseRepository.save(course);
        log.info("Successfully added subject {} to course {}", subjectId, courseId);
    }
    
    @Transactional
    public void removeSubject(Long courseId, Long subjectId) {
        log.info("Attempting to remove subject {} from course {}", subjectId, courseId);
        
        // Utilizza CourseService.findById che a sua volta lancia CourseNotFoundException
        Course course = findById(courseId);
        Subject subject = subjectService.findById(subjectId);
        
        if (!course.getSubjects().contains(subject)) {
            log.warn("Subject {} was not associated with course {}", subjectId, courseId);
            throw new SubjectNotFoundException(subjectId);
        }
        
        course.getSubjects().remove(subject);
        courseRepository.save(course);
        log.info("Successfully removed subject {} from course {}", subjectId, courseId);
    }
    
    @Transactional
    public void addExam(Long courseId, Long examId) {
        log.info("Adding exam {} to course {}", examId, courseId);
        
        // Utilizza ExamService.findById che a sua volta lancia ExamNotFoundException
        Course course = findById(courseId);
        Exam exam = examService.findById(examId);
        
        if (course.getExams().contains(exam)) {
            log.warn("Exam {} is already associated with course {}", examId, courseId);
            return;
        }
        
        course.getExams().add(exam);
        courseRepository.save(course);
        log.info("Exam {} added successfully to course {}", examId, courseId);
    }
    
    @Transactional
    public void removeExam(Long courseId, Long examId) {
        log.info("Attempting to remove exam {} from course {}", examId, courseId);
        
        // Utilizza CourseService.findById che a sua volta lancia CourseNotFoundException
        Course course = findById(courseId);
        Exam exam = examService.findById(examId);
        
        if (!course.getExams().contains(exam)) {
            log.warn("Exam {} was not associated with course {}", examId, courseId);
            throw new ExamNotFoundException(examId);
        }
        
        course.getExams().remove(exam);
        courseRepository.save(course);
        log.info("Exam {} removed successfully from course {}", examId, courseId);
    }
}
