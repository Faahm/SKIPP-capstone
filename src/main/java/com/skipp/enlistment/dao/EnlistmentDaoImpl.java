package com.skipp.enlistment.dao;

import com.skipp.enlistment.domain.Enlistment;
import com.skipp.enlistment.domain.Section;
import com.skipp.enlistment.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
@Repository
public class EnlistmentDaoImpl implements EnlistmentDao{

    JdbcTemplate jdbcTemplate;

    @Autowired
    public EnlistmentDaoImpl(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Method to create a new enlistment record
    @Override
    public Enlistment create(Student student, Section section) {
        String sql = "INSERT INTO enlistments (student_number, section_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, student.getStudentNumber(), section.getSectionId());
        return new Enlistment(student.getStudentNumber(), section.getSectionId());
    }

    // Method to delete an enlistment record
    @Override
    public void delete(int studentNumber, String sectionId) {
        String sql = "DELETE FROM enlistments WHERE student_number = ? AND section_id = ?";
        jdbcTemplate.update(sql, studentNumber, sectionId);
    }

    // Method to retrieve all students enrolled in a specific section
    @Override
    public Collection<Enlistment> findAllStudentsEnlisted(String sectionId) {
        String sql = "SELECT * FROM enlistments WHERE section_id = ?";
        return jdbcTemplate.query(sql, new EnlistmentWrapper(), sectionId);
    }

    // Method to retrieve all sections a student is enrolled in
    @Override
    public Collection<Enlistment> findAllEnlistedClasses(int studentNumber) {
        String sql = "SELECT * FROM enlistments WHERE student_number = ?";
        return jdbcTemplate.query(sql, new EnlistmentWrapper(), studentNumber);
    }
}

// Class to map result set rows to Enlistment objects
class EnlistmentWrapper implements RowMapper<Enlistment>{
    @Override
    public Enlistment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Enlistment(
                rs.getInt("student_number"),
                rs.getString("section_id")
        );
    }
}