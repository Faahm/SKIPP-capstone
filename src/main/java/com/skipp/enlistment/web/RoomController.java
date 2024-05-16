package com.skipp.enlistment.web;

import com.skipp.enlistment.domain.RecordAlreadyExistsException;
import com.skipp.enlistment.domain.RecordNotFoundException;
import com.skipp.enlistment.domain.ReferentialIntegrityViolationException;
import com.skipp.enlistment.domain.Room;
import com.skipp.enlistment.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

// TODO What stereotype annotation should be put here?
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    // TODO What bean should be wired here?
    @Autowired
    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @Operation(summary = "Get all rooms")

    // TODO What @XXXMapping annotation should be put here?
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    // API endpoint to get a collection of all existing rooms
    public Collection<Room> getRooms() {
        // TODO implement this handler
        return roomService.findAllRooms();
    }

    @Operation(summary = "Create a room")

    // TODO What @XXXMapping annotation should be put here?
    @PostMapping
    // TODO What's the annotation to use to define the status code returned when a handler is successful?
    // TODO What's the appropriate status code for this handler?
    // Hint: What does the test expect?
    @ResponseStatus(HttpStatus.CREATED)
    // TODO This should only be accessed by faculty. Apply the appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty create a room.
    public Room createRoom(@RequestBody Room room) {
        // TODO implement this handler
        Room newRoom;

        try {
            newRoom = roomService.create(room);
        } catch (DuplicateKeyException e) {
            throw new RecordAlreadyExistsException(String.format("Room Name: %s not found", room.getName()));
        }
        return newRoom;
    }

    @Operation(summary = "Update an existing room")

    // TODO What @XXXMapping annotation should be put here?
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty update an existing room.
    public Room updateRoom(@RequestBody Room room) {
        // TODO implement this handler
        Room updatedRoom;

        try {
            updatedRoom = roomService.update(room);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException(String.format("Room Name: %s not found", room.getName()));
        }

        return updatedRoom;
    }

    @Operation(summary = "Delete an existing room")

    // TODO What @XXXMapping annotation should be put here?
    // Hint: The method argument should give you an idea how it would look like.
    @DeleteMapping("/{roomName}")
    @ResponseStatus(HttpStatus.OK)
    // TODO This should only be accessed by faculty. Apply appropriate annotation.
    @PreAuthorize("hasRole('FACULTY')")
    // API endpoint to let faculty delete an existing room.
    public void deleteRoom(@PathVariable String roomName) {
        // TODO implement this handler
        try {
            roomService.delete(roomName);
        } catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {
            if (e instanceof EmptyResultDataAccessException) {
                throw new RecordNotFoundException(String.format("Room Name: %s not found", roomName));
            }
            throw new ReferentialIntegrityViolationException("Room " + roomName + " is still being used.");
        }
    }

}
