package com.skipp.enlistment.domain;

import org.springframework.stereotype.Component;

@Component
public class RoomValidation {

    // Validates if the room name is not blank.
    public void roomNameValidation(String name){
        if(name.isBlank()){
            throw new IllegalArgumentException("name should not be blank");
        }
    }

    // Validates if the room capacity is non-negative.
    public void roomCapacityValidation(int capacity){
        if(capacity < 0 ){
            throw new IllegalArgumentException(String.format("capacity must be non-negative, was %s", capacity));
        }
    }
}