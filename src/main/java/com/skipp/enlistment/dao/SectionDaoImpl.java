package com.skipp.enlistment.dao;

import com.skipp.enlistment.domain.Section;
import org.springframework.beans.factory.annotation.Autowired;
import com.skipp.enlistment.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
@Repository
public class SectionDaoImpl implements SectionDao{

    JdbcTemplate jdbcTemplate;

    @Autowired
    public SectionDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // Method to find a section by ID, performing joins to populate other entities
    @Override
    public Section findById(String sectionId) {
        String sql = "SELECT * FROM sections " +
                "INNER JOIN subjects ON sections.subject_id = subjects.subject_id " +
                "INNER JOIN rooms ON sections.room_name = rooms.name " +
                "INNER JOIN faculty ON sections.faculty_number = faculty.faculty_number " +
                "WHERE section_id = ?";

        return jdbcTemplate.queryForObject(sql,new SectionDaoWrapper(), sectionId);
    }

    // Method to retrieve all section records with joins to populate other entities
    @Override
    public Collection<Section> findAllSections() {
        String sql = "SELECT * FROM sections " +
                "INNER JOIN subjects ON sections.subject_id = subjects.subject_id " +
                "INNER JOIN rooms ON sections.room_name = rooms.name " +
                "INNER JOIN faculty ON sections.faculty_number = faculty.faculty_number";
        return jdbcTemplate.query(sql, new SectionDaoWrapper());
    }

    // Method to find sections handled by a specific faculty (using faculty number), with joins
    @Override
    public Collection<Section> findByFaculty(int facultyNumber) {
        String sql = "SELECT * FROM sections " +
                "INNER JOIN subjects ON sections.subject_id = subjects.subject_id " +
                "INNER JOIN rooms ON sections.room_name = rooms.name " +
                "INNER JOIN faculty ON sections.faculty_number = faculty.faculty_number " +
                "WHERE sections.faculty_number = ?";
        return jdbcTemplate.query(sql, new SectionDaoWrapper(), facultyNumber);
    }

    // Method to create a new section record
    @Override
    public Section create(Section section) {
        String sql = "INSERT INTO sections (section_id, subject_id, schedule, room_name, faculty_number) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, section.getSectionId(), section.getSubject().getSubjectId(), section.getSchedule().toString(),
                section.getRoom().getName(), section.getFaculty().getFacultyNumber());
        return section;
    }

    // Method to update an existing section record
    @Override
    public Section update(Section section) {
        String sql = "UPDATE sections SET subject_id = ?, schedule = ?, room_name = ?, faculty_number = ? WHERE section_id = ?";
        jdbcTemplate.update(sql, section.getSubject().getSubjectId(), section.getSchedule().toString(),
                section.getRoom().getName(), section.getFaculty().getFacultyNumber(), section.getSectionId());
        return section;
    }

    // Method to delete an existing section record
    @Override
    public void delete(String sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        jdbcTemplate.update(sql, sectionId);
    }
}

// Class to map result set rows to Section objects
class SectionDaoWrapper implements RowMapper<Section>{

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subject subject  = new Subject(
                rs.getString("subject_id")
        );

        Faculty faculty = new Faculty(
                rs.getInt("faculty_number"),
                rs.getString("first_name"),
                rs.getString("last_name")
        );

        Room room = new Room(
                rs.getString("room_name"),
                rs.getInt("capacity")
        );

        return new Section(
                rs.getString("section_id"),
                subject,
                Schedule.valueOf(rs.getString("schedule")),
                room,
                faculty
        );
    }
}