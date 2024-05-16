package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.RecordAlreadyExistsException;
import com.skipp.enlistment.domain.RecordNotFoundException;
import com.skipp.enlistment.domain.ReferentialIntegrityViolationException;
import com.skipp.enlistment.domain.Subject;
import com.skipp.enlistment.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

// TODO What stereotype annotation should be put here?
@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    // TODO What bean should be wired here?
    @Autowired
    public SubjectController(SubjectService subjectService){
        this.subjectService = subjectService;
    }

    @Operation(summary = "Get all subjects")

    // TODO What @XXXMapping annotation should be put here?
    @GetMapping
    // API endpoint to get a collection of all existing subjects.
    public Collection<Subject> getSubjects() {
        // TODO implement this handler
        return subjectService.findAllSubjects();
    }

    @Operation(summary = "Get create a new subject")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping
    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)
    // TODO This should only be accessed by faculty. Apply the appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty create a new subject.
    public Subject createSubject(@RequestBody Subject subject) {
        // TODO implement this handler
        Subject newSubject;

        try {
            newSubject = subjectService.create(subject);
        } catch (DuplicateKeyException e) {
            throw new RecordAlreadyExistsException("Subject with subjectId " + subject.getSubjectId() + " already exists.");
        }

        return newSubject;
    }

    @Operation(summary = "Delete an existing subject")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @DeleteMapping("/{subjectId}")
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty delete an existing subject.
    public void deleteSubject(@PathVariable String subjectId) {
        // TODO implement this handler
        try {
            subjectService.delete(subjectId);
        } catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {
            if (e instanceof EmptyResultDataAccessException) {
                throw new RecordNotFoundException(String.format("Subject SubjectId: %s not found", subjectId));
            }
            throw new ReferentialIntegrityViolationException("Subject " + subjectId + " is still being used.");
        }
    }

}
