package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.*;
import com.skipp.enlistment.dto.SectionDto;
import com.skipp.enlistment.service.SectionService;
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
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;
    private final SectionValidation sectionValidation;

    // TODO What bean/s should be wired here?
    @Autowired
    public SectionController(SectionService sectionService, SectionValidation sectionValidation){
        this.sectionService = sectionService;
        this.sectionValidation = sectionValidation;
    }

    @Operation(summary = "Get all sections")

    // TODO What @XXXMapping annotation should be put here?
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to get all existing sections.
    public Collection<SectionDto> getAllSections() {
        // TODO implement this handler
        return sectionService.findAllSections().stream().map((Section section) ->
                new SectionDto(section, true)).toList();
    }

    @Operation(summary = "Get section by section ID")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @GetMapping("/{sectionId}")
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to get section by section ID.
    public SectionDto getSection(@PathVariable String sectionId) {
        // TODO implement this handler
        Section section;

        try {
            section = sectionService.findById(sectionId,true);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Section ID: %s not found", sectionId));
        }

        return new SectionDto(section, true);
    }

    @Operation(summary = "Create a new section")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping
    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)
    // TODO This should only be accessed by faculty. Apply the appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to create a new section.
    public SectionDto createSection(@RequestBody SectionDto section) {
        // TODO implement this handler
        Section newSection;
        Subject subject = sectionValidation.existingSubjectValidation(section.getSubjectId());
        Room room = sectionValidation.existingRoomValidation(section.getRoomName());
        Faculty faculty = sectionValidation.existingFacultyValidation(section.getFacultyNumber());
        Schedule schedule = Schedule.valueOf(section.getSchedule());

        Section sectionDetails = new Section(section.getSectionId(), subject, schedule, room, faculty);

        try {
            newSection = sectionService.create(sectionDetails);
        } catch (DuplicateKeyException | ScheduleConflictException e) {
            if (e instanceof ScheduleConflictException) {
                throw new SectionCreationException(e.getMessage());
            }
            throw new RecordAlreadyExistsException("Section " + section.getSectionId() + " already exists.");
        }

        return new SectionDto(newSection, true);
    }

    @Operation(summary = "Update an existing section")

    // TODO What @XXXMapping annotation should be put here?
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to update an existing section.
    public SectionDto updateSection(@RequestBody SectionDto section) {
        // TODO implement this handler
        Section updatedSection;
        Subject subject = sectionValidation.existingSubjectValidation(section.getSubjectId());
        Room room = sectionValidation.existingRoomValidation(section.getRoomName());
        Faculty faculty = sectionValidation.existingFacultyValidation(section.getFacultyNumber());
        Schedule schedule = Schedule.valueOf(section.getSchedule());

        Section sectionDetails = new Section(section.getSectionId(), subject, schedule, room, faculty);

        try {
            updatedSection = sectionService.update(sectionDetails);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException("Section ID: " + section.getSectionId() + " not found");
        }

        return new SectionDto(updatedSection, true);
    }

    @Operation(summary = "Delete an existing section")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @DeleteMapping("/{sectionId}")
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to delete an existing section.
    public void deleteSection(@PathVariable String sectionId) {
        // TODO implement this handler
        try {
            sectionService.delete(sectionId);
        } catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {
            if (e instanceof EmptyResultDataAccessException) {
                throw new RecordNotFoundException("Section ID: " + sectionId + " not found");
            }
            throw new ReferentialIntegrityViolationException("Section ID: " + sectionId + " is being referenced by other entities");
        }
    }

}
