package org.gammut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

	private static final Logger logger = LoggerFactory.getLogger(User.class);

	public int id;
	public String username;
	public String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(int id, String username, String password) {
		this(username, password);
		this.id = id;
	}

	public boolean insert() {

		Connection conn = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DatabaseAccess.getConnection();

			stmt = conn.createStatement();
			logger.debug("Calling BCryptPasswordEncoder.encode");
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String safePassword = passwordEncoder.encode(password);


			sql = String.format("INSERT INTO users (Username, Password) VALUES ('%s', '%s');", username, safePassword);

			stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
				stmt.close();
				conn.close();
				return true;
			}
		} catch (SQLException se) {
			System.out.println(se);
		} // Handle errors for JDBC
		catch (Exception e) {
			System.out.println(e);
		} // Handle errors for Class.forName
		finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException se2) {
			} // Nothing we can do
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				System.out.println(se);
			}
		}
		return false;
	}
}