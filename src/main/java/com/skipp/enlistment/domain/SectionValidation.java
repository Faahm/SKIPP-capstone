package com.skipp.enlistment.domain;

import com.skipp.enlistment.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SectionValidation {

    private final SectionDao sectionRepo;
    private final RoomDao roomRepo;
    private final FacultyDao facultyRepo;
    private final SubjectDao subjectRepo;


    @Autowired
    public SectionValidation(SectionDao sectionRepo, RoomDao roomRepo,
                            FacultyDao facultyRepo, SubjectDao subjectRepo){
        this.sectionRepo = sectionRepo;
        this.roomRepo = roomRepo;
        this.facultyRepo = facultyRepo;
        this.subjectRepo = subjectRepo;
    }

    // Validates if the section ID consists only of alphanumeric characters
    public void alphanumericSectionIdValidation(String sectionId){
        if(!sectionId.matches("^[a-zA-Z0-9]*$")){
            throw new IllegalArgumentException(String.format("sectionId should be alphanumeric, was %s", sectionId));
        }
    }

    // Validates if the new section's schedule conflicts with existing sections using the same room.
    public void roomScheduleOverlapValidation(Section section){
        Collection<Section> sections = sectionRepo.findAllSections();
        sections.forEach(currentSection ->{
            if(section.hasScheduleOverlapWith(currentSection)){
                if(section.getRoom().equals(currentSection.getRoom())){
                    throw new RoomScheduleConflictException(String.format("Room %s has a schedule overlap between" +
                                    " new section %s with schedule %s and current section %s with schedule " +
                                    "%s.", section.getRoom(), section.getSectionId(), section.getSchedule(),
                            currentSection.getSectionId(), currentSection.getSchedule()));
                }
            }
        });
    }

    // Validates if the new section's schedule conflicts with existing sections handled by the same faculty member.
    public void facultyScheduleOverlapValidation(Section section){
        Collection<Section> sections = sectionRepo.findAllSections();
        sections.forEach(currentSection ->{
            if(section.hasScheduleOverlapWith(currentSection)){
                if (section.getFaculty().getFacultyNumber().equals(currentSection.getFaculty().getFacultyNumber())) {
                    throw new ScheduleConflictException(String.format("Faculty %s %s FN#%s has a schedule overlap between new section" +
                                    " %s with schedule %s and current section %s with schedule %s.", section.getFaculty().getFirstName(),
                            section.getFaculty().getLastName(), section.getFaculty().getFacultyNumber(), section.getSectionId(), section.getSchedule(),
                            currentSection.getSectionId(), currentSection.getSchedule()));
                }
            }
        });
    }

    // Validates if a subject with the provided ID exists in the database.
    public Subject existingSubjectValidation(String subjectId){
        try{
            return subjectRepo.findSubject(subjectId);
        } catch (EmptyResultDataAccessException e) {
            throw new ReferentialIntegrityViolationException(String.format("Subject ID: %s not found", subjectId));
        }
    }

    // Validates if a room with the provided name exists in the database.
    public Room existingRoomValidation(String roomName){
        try{
            return roomRepo.findByName(roomName);
        } catch (EmptyResultDataAccessException e) {
            throw new ReferentialIntegrityViolationException(String.format("Room Name: %s not found", roomName));
        }
    }

    // Validated if a faculty with the provided number exists in the database
    public Faculty existingFacultyValidation(int facultyNumber){
        try{
            return facultyRepo.findByNumber(facultyNumber);
        } catch (EmptyResultDataAccessException e) {
            throw new ReferentialIntegrityViolationException(String.format("Faculty Number: %s not found", facultyNumber));
        }
    }

}