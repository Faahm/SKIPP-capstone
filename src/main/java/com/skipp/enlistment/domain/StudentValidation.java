package com.skipp.enlistment.domain;

import org.springframework.stereotype.Component;

@Component
public class StudentValidation {

    // Validates if the provided student number is non-negative.
    public void studentNumberValidation(int studentNumber){
        if (studentNumber < 0){
            throw new IllegalArgumentException(String.format("studentNumber must be non-negative, was %s", studentNumber));
        }
    }

    // Validates if the student's first name is not blank.
    public void studentFNameValidation (String firstName){
        if(firstName.isBlank()){
            throw new IllegalArgumentException("firstName should not be blank");
        }
    }

    // Validates if the student's last name is not blank.
    public void studentLNameValidation (String lastName){
        if(lastName.isBlank()){
            throw new IllegalArgumentException("lastName should not be blank");
        }
    }
}