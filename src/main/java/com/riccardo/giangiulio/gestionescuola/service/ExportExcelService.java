package com.riccardo.giangiulio.gestionescuola.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riccardo.giangiulio.gestionescuola.model.Course;
import com.riccardo.giangiulio.gestionescuola.model.Exam;

@Service
public class ExportExcelService {
    
    private static final Logger log = LoggerFactory.getLogger(ExportExcelService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final CourseService courseService;
    private final ExamService examService;
    
    @Autowired
    public ExportExcelService(CourseService courseService, ExamService examService) {
        this.courseService = courseService;
        this.examService = examService;
        log.info("ExportExcelService initialized");
    }
    
    public byte[] generateCoursesAndExamsExcel() throws IOException {
        log.info("Generating Excel file with courses and exams");
        
        List<Course> courses = courseService.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Create courses sheet
            Sheet coursesSheet = workbook.createSheet("Courses");
            
            // Create header row for courses
            Row headerRow = coursesSheet.createRow(0);
            createCell(headerRow, 0, "ID", headerStyle);
            createCell(headerRow, 1, "Title", headerStyle);
            createCell(headerRow, 2, "Description", headerStyle);
            createCell(headerRow, 3, "Duration", headerStyle);
            createCell(headerRow, 4, "Price", headerStyle);
            
            // Fill data rows for courses
            int rowNum = 1;
            for (Course course : courses) {
                Row row = coursesSheet.createRow(rowNum++);
                createCell(row, 0, course.getId().toString(), dataStyle);
                createCell(row, 1, course.getTitle(), dataStyle);
                createCell(row, 2, course.getDescription(), dataStyle);
                createCell(row, 3, course.getDuration(), dataStyle);
                createCell(row, 4, course.getPrice().toString(), dataStyle);
            }
            
            // Auto size columns
            for (int i = 0; i < 5; i++) {
                coursesSheet.autoSizeColumn(i);
            }
            
            // Create exams sheet
            Sheet examsSheet = workbook.createSheet("Exams by Course");
            
            // Course counter for exams sheet
            int courseCounter = 0;
            int currentRow = 0;
            
            for (Course course : courses) {
                // Create course header
                Row courseHeaderRow = examsSheet.createRow(currentRow++);
                createCell(courseHeaderRow, 0, "Course: " + course.getTitle(), headerStyle);
                
                // Create exam header row
                Row examHeaderRow = examsSheet.createRow(currentRow++);
                createCell(examHeaderRow, 0, "ID", headerStyle);
                createCell(examHeaderRow, 1, "Title", headerStyle);
                createCell(examHeaderRow, 2, "Description", headerStyle);
                createCell(examHeaderRow, 3, "Date", headerStyle);
                createCell(examHeaderRow, 4, "Duration (min)", headerStyle);
                createCell(examHeaderRow, 5, "Max Score", headerStyle);
                createCell(examHeaderRow, 6, "Passing Score", headerStyle);
                
                // Get exams for this course
                List<Exam> exams = course.getExams() != null && !course.getExams().isEmpty() ?
                        course.getExams().stream().toList() : examService.findByCourse(course);
                
                if (exams.isEmpty()) {
                    // If no exams, add a message
                    Row noExamsRow = examsSheet.createRow(currentRow++);
                    createCell(noExamsRow, 0, "No exams found for this course", dataStyle);
                } else {
                    // Add exam data
                    for (Exam exam : exams) {
                        Row examRow = examsSheet.createRow(currentRow++);
                        createCell(examRow, 0, exam.getId().toString(), dataStyle);
                        createCell(examRow, 1, exam.getTitle(), dataStyle);
                        createCell(examRow, 2, exam.getDescription(), dataStyle);
                        createCell(examRow, 3, exam.getDate() != null ? 
                                exam.getDate().format(DATE_TIME_FORMATTER) : "N/A", dataStyle);
                        createCell(examRow, 4, String.valueOf(exam.getDuration()), dataStyle);
                        createCell(examRow, 5, String.valueOf(exam.getMaxScore()), dataStyle);
                        createCell(examRow, 6, String.valueOf(exam.getPassingScore()), dataStyle);
                    }
                }
                
                // Add empty row as separator
                currentRow++;
                courseCounter++;
            }
            
            // Auto size columns for exams sheet
            for (int i = 0; i < 7; i++) {
                examsSheet.autoSizeColumn(i);
            }
            
            // Write to output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Excel generation completed with {} courses", courseCounter);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating Excel file: ", e);
            throw e;
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        return cell;
    }
}
