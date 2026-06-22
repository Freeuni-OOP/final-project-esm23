package com.quizwebsite.util;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {

	@Test
	void testConnectionOpens() throws Exception {
		try (Connection c = DBConnection.get()) {
			assertNotNull(c);
			assertFalse(c.isClosed());
		}
	}

	@Test
	void testSelectOne() throws Exception {
		try (Connection c = DBConnection.get();
				 Statement s = c.createStatement();
				 ResultSet rs = s.executeQuery("SELECT 1")) {
			assertTrue(rs.next());
			assertEquals(1, rs.getInt(1));
		}
	}
}