package com.skipp.enlistment.service;

import com.skipp.enlistment.domain.AppUser;
import com.skipp.enlistment.domain.Faculty;
import com.skipp.enlistment.dao.*;
import com.skipp.enlistment.domain.FacultyValidation;
import com.skipp.enlistment.domain.Section;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
@Service
@Transactional
public class FacultyServiceImpl implements FacultyService{
    private final FacultyDao facultyRepo;
    private final SectionDao sectionRepo;
    private final AppUserDao appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final FacultyValidation facultyValidation;

    @Autowired
    public FacultyServiceImpl(FacultyDao facultyRepo, SectionDao sectionRepo,
                              AppUserDao appUserRepo, PasswordEncoder passwordEncoder,
                              FacultyValidation facultyValidation){
        this.facultyRepo = facultyRepo;
        this.sectionRepo = sectionRepo;
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.facultyValidation = facultyValidation;
    }

    // Retrieves all faculty records from the database.
    @Override
    public Collection<Faculty> findAllFaculty() {
        return facultyRepo.findAllFaculty();
    }

    // Retrieves a faculty record by faculty number.
    @Override
    public Faculty findByNumber(int facultyNumber, boolean includeSections) {
        Faculty faculty = facultyRepo.findByNumber(facultyNumber);
        if(includeSections){
            Collection<Section> sections = sectionRepo.findByFaculty(facultyNumber);
            sections.forEach(section -> faculty.addSection(section));
        }
        return faculty;
    }

    // Creates a new faculty record and corresponding user account in the database.
    @Override
    public Faculty create(Faculty faculty) {
        facultyValidation.facultyNumberValidation(faculty.getFacultyNumber());
        facultyValidation.facultyFNameValidation(faculty.getFirstName());
        facultyValidation.facultyLNameValidation(faculty.getLastName());

        Faculty newFaculty = facultyRepo.create(faculty);
        AppUser appUser = facultyAppUser(faculty);
        appUserRepo.create(appUser);
        return newFaculty;
    }

    // Updates an existing faculty record and potentially user account details in the database.
    @Override
    public Faculty update(Faculty faculty) {
        facultyValidation.facultyFNameValidation(faculty.getFirstName());
        facultyValidation.facultyLNameValidation(faculty.getLastName());

        facultyRepo.findByNumber(faculty.getFacultyNumber());
        AppUser appUser = facultyAppUser(faculty);
        Faculty updatedFaculty = facultyRepo.update(faculty);
        appUserRepo.update(appUser);
        return updatedFaculty;
    }

    // Deletes a faculty record from the database.
    @Override
    public void delete(int facultyNumber) {
        facultyRepo.findByNumber(facultyNumber);
        facultyRepo.delete(facultyNumber);
    }

    private AppUser facultyAppUser(Faculty faculty){
        String rawPassword = StringUtils.replaceChars(faculty.getFirstName() + faculty.getLastName(), " ", "");
        final String PASSWORD = passwordEncoder.encode(rawPassword);
        return new AppUser(String.format("FC-%s", faculty.getFacultyNumber()), PASSWORD, "FACULTY");
    }
}