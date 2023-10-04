package com.genpact.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import com.genpact.dao.FacultyDAO;
import com.genpact.pojo.Faculty;

@Service
public class FacultyDAOImpl implements FacultyDAO{

	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean addNewFaculty(Faculty faculty) {
		
		String query = "INSERT INTO faculty(facultyName,instituteID)"
				+ " values (?,?)";
		try {
		int count = jdbcTemplate.update(query,
			new Object[] {
			faculty.getFacultyName() , 
			faculty.getInstituteID()});
			
			if(count > 0)
				return true;
			else
				return false;
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Faculty> getAllFaculties() {
		
		List<Faculty> lst = new ArrayList<>();
		
		String query = "SELECT * FROM faculty";
		
		try {
		
		lst = jdbcTemplate.query(query,
				new BeanPropertyRowMapper<Faculty>(Faculty.class));
		
		} catch (Exception e) {
			e.printStackTrace();
			lst.clear();
			return lst;
		}
		
		return lst;
	}

	
	@Override
	public List<Faculty> getAllFacultiesByInstitute(int instituteID) {
	
		List<Faculty> lst = new ArrayList<>();
		String query = "SELECT * FROM faculty WHERE instituteID = ?";
		
		try {
			
			lst = jdbcTemplate.query(query, 
					new BeanPropertyRowMapper<Faculty>(Faculty.class),
					instituteID);
			
			return lst;
			
		} catch (Exception e) {
			e.printStackTrace();
			lst.clear();
			return lst;
		}
	}

	@Override
	public boolean deleteFaculty(int facultyID) {
		
		String query = 
    "DELETE FROM faculty WHERE facultyID = ?";
		
		try {
			
			int count = 
			jdbcTemplate.update(query,
			facultyID);
			
			if(count > 0)
				return true;
			else
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
