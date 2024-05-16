package com.skipp.enlistment.domain;

import org.springframework.stereotype.Component;

@Component
public class SubjectValidation {

    // Validates if the subject ID is not blank.
    public void subjectIdValidation (String subjectId){
        if(subjectId.isBlank()){
            throw new IllegalArgumentException("subjectId should not be blank");
        }
    }

    // Validates if the subject ID consists only of alphanumeric characters
    public void alphanumericSubjectIdValidation(String subjectId){
        if(!subjectId.matches("^[a-zA-Z0-9]*$")){
            throw new IllegalArgumentException(String.format("subjectId should be alphanumeric, was %s", subjectId));
        }
    }
}