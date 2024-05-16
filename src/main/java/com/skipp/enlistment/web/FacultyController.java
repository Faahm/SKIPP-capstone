package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.Faculty;
import com.skipp.enlistment.domain.RecordAlreadyExistsException;
import com.skipp.enlistment.domain.RecordNotFoundException;
import com.skipp.enlistment.domain.ReferentialIntegrityViolationException;
import com.skipp.enlistment.dto.FacultyDto;
import com.skipp.enlistment.service.FacultyService;
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
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    // TODO What bean should be wired here?
    @Autowired
    public FacultyController(FacultyService facultyService){
        this.facultyService = facultyService;
    }

    @Operation(summary = "Get all faculty")

    // TODO What @XXXMapping annotation should be put here?
    @GetMapping
    // API endpoint to get a collection of all existing faculties.
    public Collection<Faculty> getAllFaculty() {
        // TODO implement this handler
        return facultyService.findAllFaculty();
    }

    @Operation(summary = "Get get faculty by faculty number")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @GetMapping("/{facultyNumber}")
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to get faculty by faculty number.
    public FacultyDto getFaculty(@PathVariable Integer facultyNumber) {
        // TODO implement this handler
        Faculty faculty;
        try {
            faculty = facultyService.findByNumber(facultyNumber, true);
        } catch (EmptyResultDataAccessException e){
            throw new RecordNotFoundException(String.format("Faculty Number: %s not found", facultyNumber));
        }
        return new FacultyDto(faculty,true);
    }

    @Operation(summary = "Create a a new faculty")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping
    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)
    // TODO This should only be accessed by faculty. Apply the appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to create a new faculty.
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        // TODO implement this handler
        Faculty newFaculty;
        try {
            newFaculty = facultyService.create(faculty);
        } catch (DuplicateKeyException e){
            throw new RecordAlreadyExistsException(String.format("Faculty Number: %s already exists", faculty.getFacultyNumber()));
        }
        return newFaculty;
    }

    @Operation(summary = "Update an existing faculty")

    // TODO What @XXXMapping annotation should be put here?
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to update an existing faculty.
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        // TODO implement this handler
        Faculty updatedFaculty;
        try {
            updatedFaculty = facultyService.update(faculty);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Faculty Number: %s not found", faculty.getFacultyNumber()));
        }
        return updatedFaculty;
    }

    @Operation(summary = "Delete an existing faculty")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @DeleteMapping("/{facultyNumber}")

    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to delete an existing faculty.
    public void deleteFaculty(@PathVariable Integer facultyNumber) {
        // TODO implement this handler
        try{
            facultyService.delete(facultyNumber);
        }catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {

            if (e instanceof EmptyResultDataAccessException) {
                throw new RecordNotFoundException(String.format("Faculty Number: %s not found", facultyNumber));
            }
            throw new ReferentialIntegrityViolationException("Faculty " + facultyNumber + " is still teaching a section");
        }
    }

}
