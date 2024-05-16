package com.skipp.enlistment.domain;
import org.springframework.stereotype.Component;

@Component
public class FacultyValidation {

    // Validates if the provided faculty number is non-negative.
    public void facultyNumberValidation(int facultyNumber){
        if (facultyNumber < 0){
            throw new IllegalArgumentException("facultyNumber must be non-negative, was " + facultyNumber);
        }
    }

    // Validates if the faculty's first name is not blank.
    public void facultyFNameValidation (String firstName){
        if(firstName.isBlank()){
            throw new IllegalArgumentException("firstName should not be blank");
        }
    }

    // Validates if the faculty's last name is not blank.
    public void facultyLNameValidation (String lastName){
        if(lastName.isBlank()){
            throw new IllegalArgumentException("lastName should not be blank");
        }
    }
}