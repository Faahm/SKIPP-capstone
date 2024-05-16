package com.skipp.enlistment.service;

import com.skipp.enlistment.dao.*;
import com.skipp.enlistment.domain.Enlistment;
import com.skipp.enlistment.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class EnlistmentServiceImpl implements EnlistmentService{

    private final EnlistmentDao enlistmentRepo;
    private final SectionDao sectionRepo;
    private final EnlistmentValidation enlistmentValidation;

    @Autowired
    public EnlistmentServiceImpl(EnlistmentDao enlistmentRepo, SectionDao sectionRepo, EnlistmentValidation enlistmentValidation){
        this.enlistmentRepo = enlistmentRepo;
        this.sectionRepo = sectionRepo;
        this.enlistmentValidation = enlistmentValidation;
    }

    // Enlists a student in a section after performing validations.
    @Override
    public Enlistment enlist(int studentNumber, String sectionId)  {
        Student student = enlistmentValidation.existingStudentValidation(studentNumber);
        Section section = enlistmentValidation.existingSectionValidation(sectionId);

        Collection<Enlistment> enlistments = enlistmentRepo.findAllEnlistedClasses(studentNumber);

        enlistments.forEach(enlistment -> {
            enlistmentValidation.duplicateEnlistmentValidation(enlistment,studentNumber,sectionId);
            Section enlistedSection = sectionRepo.findById(enlistment.sectionId());
            enlistmentValidation.sectionSubjectValidation(enlistedSection, section, sectionId, enlistment);
            enlistmentValidation.scheduleOverlapValidation(section, enlistedSection);
        });

        enlistmentValidation.sectionCapacityValidation(section);

        return enlistmentRepo.create(student, section);
    }

    // Allows a student to cancel their enrollment in a section.
    @Override
    public void cancel(int studentNumber, String sectionId) {
        enlistmentValidation.existingStudentValidation(studentNumber);
        enlistmentValidation.existingSectionValidation(sectionId);

        enlistmentRepo.findAllStudentsEnlisted(sectionId);
        enlistmentRepo.findAllEnlistedClasses(studentNumber);
        enlistmentRepo.delete(studentNumber,sectionId);
    }
}