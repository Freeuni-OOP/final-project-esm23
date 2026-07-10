package com.quizwebsite.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	private static final Properties props = new Properties();

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("MySQL driver not found", e);
		}
		try (InputStream in = DBConnection.class
						.getClassLoader()
						.getResourceAsStream("db.properties")) {
			if (in == null) throw new RuntimeException("db.properties not found on classpath");
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load db.properties", e);
		}
	}

	public static Connection get() throws SQLException {
		return DriverManager.getConnection(
						props.getProperty("db.url"),
						props.getProperty("db.user"),
						props.getProperty("db.password")
		);
	}
}