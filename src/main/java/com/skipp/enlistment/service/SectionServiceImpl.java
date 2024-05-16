package com.skipp.enlistment.service;

import com.skipp.enlistment.dao.EnlistmentDao;
import com.skipp.enlistment.dao.SectionDao;
import com.skipp.enlistment.dao.StudentDao;
import com.skipp.enlistment.domain.Section;
import com.skipp.enlistment.domain.SectionValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
@Service
@Transactional
public class SectionServiceImpl implements SectionService{
    private final SectionDao sectionRepo;
    private final SectionValidation sectionValidation;
    private final EnlistmentDao enlistmentRepo;
    private final StudentDao studentRepo;

    @Autowired
    public SectionServiceImpl(SectionDao sectionRepo, SectionValidation sectionValidation, EnlistmentDao enlistmentRepo, StudentDao studentRepo){
        this.sectionRepo = sectionRepo;
        this.sectionValidation = sectionValidation;
        this.enlistmentRepo = enlistmentRepo;
        this.studentRepo = studentRepo;
    }

    // Retrieves a section record by ID from the database.
    @Override
    public Section findById(String sectionId, boolean includeStudents) {
        Section section = sectionRepo.findById(sectionId);

        if (includeStudents) {
            enlistmentRepo.findAllStudentsEnlisted(sectionId).stream().forEach(enlistment -> section.addStudent(studentRepo.findByNumber(enlistment.studentNumber())));
        }

        return section;
    }

    // Retrieves all section records from the database.
    @Override
    public Collection<Section> findAllSections() {
        return sectionRepo.findAllSections();
    }

    // Creates a new section record in the database after performing validations.
    @Override
    public Section create(Section section) {
        sectionValidation.alphanumericSectionIdValidation(section.getSectionId());
        sectionValidation.roomScheduleOverlapValidation(section);
        sectionValidation.facultyScheduleOverlapValidation(section);

        return sectionRepo.create(section);
    }

    // Updates an existing section record in the database after performing validations for schedule conflicts.
    @Override
    public Section update(Section section) {
        sectionValidation.roomScheduleOverlapValidation(section);;

        sectionRepo.findById(section.getSectionId());

        return sectionRepo.update(section);
    }

    // Deletes a section record from the database.
    @Override
    public void delete(String sectionId) {
        sectionRepo.findById(sectionId);
        sectionRepo.delete(sectionId);
    }
}