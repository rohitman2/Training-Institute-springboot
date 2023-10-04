package com.genpact.daoimpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import com.genpact.dao.RequestDAO;
import com.genpact.pojo.Course;
import com.genpact.pojo.Institute;
import com.genpact.pojo.Request;

@Service
public class RequestDAOImpl implements RequestDAO{

    private JdbcTemplate jdbcTemplate;
	
    @Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}	

	@Override
	public boolean submitRequest(Request request) {
		boolean b = false;
		String query = "INSERT INTO admissionrequest("
+ "userID, courseID, requestDate , status)"
+ " VALUES(?,?,?,?)";
		
		try {
			int count = jdbcTemplate.update(query, 
					new Object[] {request.getUserID() ,
					request.getCourseID() ,
					request.getRequestDate() , false});
				
			if(count > 0)
				b = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return b;
	}

	
	@Override
	public Request getRequestInfo(int requestID) {
		
		Request request = null;
		String query = "SELECT * FROM admissionrequest "
				+ "WHERE requestID = ?";
		
		try {
			
			request = jdbcTemplate.queryForObject(query, 
					new BeanPropertyRowMapper<Request>
					(Request.class),
					requestID);
			
			return request;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	@Override
	public List<List<Request>> 
	viewAllRequestForInstitute(
			int instituteID) {
		
		List<List<Request>> finalList = 
				new ArrayList<>();
		List<Request> lst = new ArrayList<>();
		List<Integer> clist = 
				new ArrayList<>();
		
		String query = 
"SELECT courseID FROM course where instituteID = ?";
		
		clist = jdbcTemplate.execute(query,
new PreparedStatementCallback<List<Integer>>() {
			@Override
			public List<Integer> doInPreparedStatement(
PreparedStatement ps) throws SQLException, DataAccessException {
				List<Integer> list = 
						new ArrayList<>();
				ps.setInt(1, instituteID);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) {
					while(rs.next()) {
						list.add(
							rs.getInt("courseID"));
					}
					return list;
				}
				else {
					list.clear();
					return list;
				}
			}
});
		if(clist.size() == 0)
		{
			finalList.clear();
			return finalList;
		}
		
		try {
			
		for(int cr_id : clist) {
			lst = jdbcTemplate.query(
"SELECT * FROM admissionrequest WHERE courseID = ?",
new BeanPropertyRowMapper<Request>(
		Request.class),cr_id);
			
			finalList.add(lst);
		}
		return finalList;
		
		} catch (Exception e) {
			e.printStackTrace();
			finalList.clear();
			return finalList;
		}
	}
	
	/*
	@Override
	public List<Request> viewAllRequestForInstitute(
			int instituteID) {
		
		List<Request> lst = new ArrayList<>();
		
		String query = 
"SELECT courseID FROM course where instituteID = ?";
		
		int courseID = jdbcTemplate.execute(query,
new PreparedStatementCallback<Integer>() {
			@Override
			public Integer doInPreparedStatement(
PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setInt(1, instituteID);
				ResultSet rs = ps.executeQuery();
				if(rs.isBeforeFirst()) {
				rs.next();
				return rs.getInt("courseID");
				}
				else
					return 0;
			}
});
		if(courseID == 0)
		{
			lst.clear();
			return lst;
		}
		
		try {
			
			lst = jdbcTemplate.query(
"SELECT * FROM admissionrequest WHERE courseID = ?",
new BeanPropertyRowMapper<Request>(Request.class),
courseID);
			
		} catch (Exception e) {
			e.printStackTrace();
			lst.clear();
			return lst;
		}
		
		return lst;
	}
*/
	@Override
    public boolean isAlreadyRequested(Request request) {
        
		boolean b = false;
        
        String query = "SELECT count(*) AS reqCount "
+ "FROM admissionrequest WHERE userID = ? AND courseID = ?";
        
        try {
            b = jdbcTemplate.execute(query, new PreparedStatementCallback<Boolean>() {

                @Override
                public Boolean doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {

                    ps.setString(1, request.getUserID());
                    ps.setInt(2, request.getCourseID());

                    ResultSet rs = ps.executeQuery();

                    if (rs.isBeforeFirst()) {
                        rs.next();
                        int count = rs.getInt("reqCount");
                        if(count == 0) {
                            return false;
                        }
                        return true;
                    }
                    else
                        return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return b;
    }

	@Override
	public List<Request> getAllRequests(int courseID) {
		
		List<Request> lstList = new ArrayList<Request>();
		
		int instituteID = 0;
        
        String query = "SELECT instituteID FROM course WHERE "
        		+ "courseID = ?";
        
        try {
			instituteID = jdbcTemplate.execute(query, 
					new PreparedStatementCallback<Integer>() {
				@Override
				public Integer doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setInt(1, courseID);
					ResultSet rs = ps.executeQuery();
					if(rs.next())
						return rs.getInt("instituteID");
					else return 0;
				}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
        
			try {
					lstList = 
					jdbcTemplate.query(
					"SELECT * FROM admissionrequest WHERE"
					+ " courseID IN (SELECT courseID FROM course WHERE"
					+ " instituteID = ?)",
					new BeanPropertyRowMapper<Request>(Request.class),
					instituteID);

					return lstList;
				}
		catch(Exception ex) {
			ex.printStackTrace();
			lstList.clear();
			return lstList;
		}
	}
}
