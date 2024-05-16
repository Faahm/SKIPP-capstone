package com.skipp.enlistment.service;

import com.skipp.enlistment.dao.*;
import com.skipp.enlistment.domain.*;
import com.skipp.enlistment.domain.StudentValidation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class StudentServiceImpl implements StudentService{

    private final StudentDao studentRepo;
    private final EnlistmentDao enlistmentRepo;
    private final SectionDao sectionRepo;
    private final AppUserDao appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final StudentValidation studentValidation;

    @Autowired
    public StudentServiceImpl(StudentDao studentRepo, EnlistmentDao enlistmentRepo,
                              SectionDao sectionRepo, AppUserDao appUserRepo,
                              PasswordEncoder passwordEncoder, StudentValidation studentValidation){
        this.studentRepo = studentRepo;
        this.enlistmentRepo = enlistmentRepo;
        this.sectionRepo = sectionRepo;
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.studentValidation = studentValidation;
    }

    // Retrieves all student records from the database.
    @Override
    public Collection<Student> findAllStudents() {
        return studentRepo.findAllStudents();
    }

    // Retrieves a student record by student number from the database.
    @Override
    public Student findByNumber(int studentNumber, boolean includeSections) {
        Student student = studentRepo.findByNumber(studentNumber);
        if(includeSections){
            Collection<Enlistment> enlistments = enlistmentRepo.findAllEnlistedClasses(studentNumber);
            enlistments.forEach(enlistment -> {
                Section section = sectionRepo.findById(enlistment.sectionId());
                student.addSection(section);
            });
        }
        return student;
    }

    // Creates a new student record and corresponding user account in the database after performing validations.
    @Override
    public Student create(Student student) {
        Student newStudent = studentRepo.create(student);
        studentValidation.studentNumberValidation(student.getStudentNumber());
        studentValidation.studentFNameValidation(student.getFirstName());
        studentValidation.studentLNameValidation(student.getLastName());

        AppUser appUser = studentAppUser(newStudent);
        appUserRepo.create(appUser);

        return newStudent;

    }

    // Updates an existing student record and corresponding user account in the database after performing validations.
    @Override
    public Student update(Student student) {
        studentValidation.studentFNameValidation(student.getFirstName());
        studentValidation.studentLNameValidation(student.getLastName());

        studentRepo.findByNumber(student.getStudentNumber());

        Student updatedStudent = studentRepo.update(student);
        AppUser appUser = studentAppUser(updatedStudent);
        appUserRepo.update(appUser);

        return updatedStudent;
    }

    // Deletes an existing student by student number in the database.
    @Override
    public void delete(int studentNumber) {
        studentRepo.findByNumber(studentNumber);

        studentRepo.delete(studentNumber);
    }

    // Create a new AppUser object representing the student's user account.
    private AppUser studentAppUser(Student student){
        String rawPassword = StringUtils.replaceChars(student.getFirstName() + student.getLastName(), " ", "");
        final String PASSWORD = passwordEncoder.encode(rawPassword);
        return new AppUser(String.format("ST-%s", student.getStudentNumber()), PASSWORD, "STUDENT");
    }
}