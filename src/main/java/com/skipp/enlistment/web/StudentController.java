package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.RecordAlreadyExistsException;
import com.skipp.enlistment.domain.RecordNotFoundException;
import com.skipp.enlistment.domain.ReferentialIntegrityViolationException;
import com.skipp.enlistment.domain.Student;
import com.skipp.enlistment.dto.StudentDto;
import com.skipp.enlistment.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

// TODO What stereotype annotation should be put here?
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // TODO What bean should be wired here?
    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @Operation(summary = "Get all students")

    // TODO What @XXXMapping annotation should be put here?
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to get all existing students.
    public Collection<Student> getStudents() {
        // TODO implement this handler
        return studentService.findAllStudents();
    }

    @Operation(summary = "Get student by student number")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @GetMapping("/{studentNumber}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty get student by student number.
    public StudentDto getStudent(@PathVariable Integer studentNumber, Authentication auth) {
        // TODO implement this handler
        // Hint: 'auth' is where you can get the username of the user accessing the API
        Student student;

        try {
            student = studentService.findByNumber(studentNumber, true);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Student Number: %s not found", studentNumber));
        }

        return new StudentDto(student, true);
    }

    @Operation(summary = "Create a new student")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping
    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)
    // TODO This should only be accessed by faculty. Apply the appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty create a new student.
    public Student createStudent(@RequestBody Student student) {
        // TODO implement this handler
        Student newStudent;

        try {
            newStudent = studentService.create(student);
        } catch (DuplicateKeyException e) {
            throw new RecordAlreadyExistsException("Student with studentNumber " + student.getStudentNumber() + " already exists.");
        }

        return newStudent;
    }

    @Operation(summary = "Update an existing student")

    // TODO What @XXXMapping annotation should be put here?
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty update an existing student.
    public Student updateStudent(@RequestBody Student student) {
        // TODO implement this handler
        Student updatedStudent;

        try {
            updatedStudent = studentService.update(student);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Student Number: %s not found", student.getStudentNumber()));
        }

        return updatedStudent;
    }

    @Operation(summary = "Delete an existing student")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @DeleteMapping("/{studentNumber}")
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty delete an existing student.
    public void deleteStudent(@PathVariable Integer studentNumber) {
        // TODO implement this handler
        try {
            studentService.delete(studentNumber);
        } catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {
            if (e instanceof EmptyResultDataAccessException) {
                throw new RecordNotFoundException(String.format("Student Number: %s not found", studentNumber));
            }
            throw new ReferentialIntegrityViolationException("Student " + studentNumber + " is still enrolled in a section.");
        }
    }

}
