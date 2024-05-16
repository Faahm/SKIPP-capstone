package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.*;
import com.skipp.enlistment.service.EnlistmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// TODO What stereotype annotation should be put here?
@RestController
@RequestMapping("/enlist")
public class EnlistmentController {

    private final EnlistmentService enlistmentService;
    private final EnlistmentValidation enlistmentValidation;

    // TODO What bean should be wired here?
    @Autowired
    public EnlistmentController(EnlistmentService enlistmentService, EnlistmentValidation enlistmentValidation){
        this.enlistmentService = enlistmentService;
        this.enlistmentValidation = enlistmentValidation;
    }

    @Operation(summary = "Enlist in a section")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping

    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)

    // TODO This should only be accessed by students. Apply the appropriate annotation.
    @PreAuthorize("hasRole('STUDENT')")
    // API endpoint to let student enlist.
    public Enlistment enlist(@RequestBody Enlistment enlistment, Authentication auth) {
        // TODO implement this handler
        // Hint: 'auth' is where you can get the username of the user accessing the API
        Enlistment newEnlistment;
        enlistmentValidation.studentAuthorization(auth, enlistment.studentNumber());
        try {
            newEnlistment = enlistmentService.enlist(enlistment.studentNumber(),enlistment.sectionId());
        } catch(DuplicateEnlistmentException | SameSubjectException | ScheduleConflictException | RoomCapacityReachedException e){
            throw new EnlistmentException(e.getMessage());
        }
        return newEnlistment;
    }

    @Operation(summary = "Cancel enlistment")

    // TODO What @XXXMapping annotation should be put here?
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to let student cancel enlistment.
    public void cancel(@RequestBody Enlistment enlistment) {
        // TODO implement this handler
        try {
            enlistmentService.cancel(enlistment.studentNumber(), enlistment.sectionId());
        } catch(EmptyResultDataAccessException | RecordNotFoundException e){
            if (e instanceof EmptyResultDataAccessException){
                throw new RecordNotFoundException(String.format("Enlistment not found: Student Number: %s, Section ID: %s", enlistment.studentNumber(), enlistment.sectionId()));
            }
            throw new ReferentialIntegrityViolationException(e.getMessage());
        }
    }

}
