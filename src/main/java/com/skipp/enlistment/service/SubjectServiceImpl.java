package com.skipp.enlistment.service;

import com.skipp.enlistment.dao.SubjectDao;
import com.skipp.enlistment.domain.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService{
    
    private final SubjectDao subjectRepo;

    @Autowired
    public SubjectServiceImpl(SubjectDao subjectRepo){
        this.subjectRepo = subjectRepo;
    }

    // Retrieves all subject records from the database.
    @Override
    public Collection<Subject> findAllSubjects() {
        return subjectRepo.findAllSubjects();
    }

    // Retrieves a subject record by subject ID from the database.
    @Override
    public Subject findSubject(String subjectId) {
        return subjectRepo.findSubject(subjectId);
    }

    // Creates a new subject record in the database.
    @Override
    public Subject create(Subject subject) {

        return subjectRepo.create(subject);
    }

    // Deletes a subject record from the database.
    @Override
    public void delete(String subjectId) {
        subjectRepo.findSubject(subjectId);
        subjectRepo.delete(subjectId);
    }
}