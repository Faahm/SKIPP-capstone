package com.skipp.enlistment.domain;

import com.skipp.enlistment.dao.EnlistmentDao;
import com.skipp.enlistment.dao.SectionDao;
import com.skipp.enlistment.dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public class EnlistmentValidation {

    private final EnlistmentDao enlistmentRepo;
    private final StudentDao studentRepo;
    private final SectionDao sectionRepo;

    @Autowired
    public EnlistmentValidation(EnlistmentDao enlistmentRepo, StudentDao studentRepo, SectionDao sectionRepo){
        this.enlistmentRepo=enlistmentRepo;
        this.studentRepo=studentRepo;
        this.sectionRepo=sectionRepo;

    }

    // Validates if a student exists based on student number.
    public Student existingStudentValidation(int studentNumber){
        try{
            return studentRepo.findByNumber(studentNumber);
        }catch(EmptyResultDataAccessException e){
            throw new RecordNotFoundException(String.format("Student with number %s not found", studentNumber));
        }
    }

    // Validates if a section exists based on section ID.
    public Section existingSectionValidation(String sectionId){
        try{
            return sectionRepo.findById(sectionId);
        }catch(EmptyResultDataAccessException e){
            throw new RecordNotFoundException(String.format("Section with id %s not found", sectionId));
        }
    }

    // Validates for duplicate enlistments for a student in a section.
    public void duplicateEnlistmentValidation(Enlistment enlistment, int studentNumber, String sectionId){
        if(enlistment.studentNumber()==(studentNumber) && enlistment.sectionId().equals(sectionId)){
            throw new DuplicateEnlistmentException(String.format("Enlisted more than once: %s", sectionId));
        }
    }

    // Validates if a student is enrolling in a section with the same subject as an existing enlistment.
    public void sectionSubjectValidation(Section section, Section enlistedSection, String sectionId, Enlistment enlistment){
        if(section.getSubject().getSubjectId().equals(enlistedSection.getSubject().getSubjectId())){
            throw new SameSubjectException(String.format("Section %s with subject %s has same subject as currently enlisted section %s", sectionId, section.getSubject().getSubjectId(), enlistment.sectionId()));
        }
    }

    // Validates if the new section's schedule overlaps with an existing enlisted section's schedule
    public void scheduleOverlapValidation(Section section, Section enlistedSection){
        enlistedSection.getSchedule().notOverlappingWith(section.getSchedule());
    }

    // Validates if the number of existing enlistments reaches the room capacity
    public void sectionCapacityValidation(Section section){
        Collection<Enlistment> enlistments = enlistmentRepo.findAllStudentsEnlisted(section.getSectionId());
        if(enlistments.size() >= section.getRoom().getCapacity()){
            throw new RoomCapacityReachedException(String.format("Capacity Reached - enlistments: %s; capacity: %s",enlistments.size(),section.getRoom().getCapacity()));
        }
    }

    // Validates if student is not enlisting for themselves
    public void studentAuthorization(Authentication auth, Integer studentNumber){
        String authenticationID = auth.getName();
        if(!authenticationID.equals("ST-"+studentNumber)){
            throw new AccessDeniedException("You cannot enlist for another student");
        }
    }
}