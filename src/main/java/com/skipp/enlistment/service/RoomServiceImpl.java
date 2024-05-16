package com.skipp.enlistment.service;

import com.skipp.enlistment.dao.RoomDao;
import com.skipp.enlistment.domain.Room;
import com.skipp.enlistment.domain.RoomValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class RoomServiceImpl implements RoomService{

    private final RoomDao roomRepo;
    private final RoomValidation roomValidation;

    @Autowired
    public RoomServiceImpl (RoomDao roomRepo, RoomValidation roomValidation){
        this.roomRepo = roomRepo;
        this.roomValidation = roomValidation;
    }

    // Retrieves all room records from the database.
    @Override
    public Collection<Room> findAllRooms() {
        return roomRepo.findAllRooms();
    }

    // Retrieves a room record by name from the database.
    @Override
    public Room findByName(String name) {
        return roomRepo.findByName(name);
    }

    // Creates a new room record in the database after performing validations.
    @Override
    public Room create(Room room) {
        roomValidation.roomNameValidation(room.getName());
        roomValidation.roomCapacityValidation(room.getCapacity());

        return roomRepo.create(room);
    }

    // Updates an existing room record in the database after validating capacity.
    @Override
    public Room update(Room room) {
        roomValidation.roomCapacityValidation(room.getCapacity());
        roomRepo.findByName(room.getName());

        return roomRepo.update(room);
    }

    // Deletes a room record from the database.
    @Override
    public void delete(String name) {
        roomRepo.findByName(name);
        roomRepo.delete(name);
    }
}