package com.loan.util;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConUtil {
	private static final String fileName="db.properties";
	public static Connection getDbConnection() {
		Connection con=null;
		String connString=null;
		try {
		connString=DBPropertyUtil.getConnectionString(fileName);
		}catch (IOException e) {
			System.out.println("Connection String Creation Failed");
			e.printStackTrace();
		}
		if(connString!=null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con=DriverManager.getConnection(connString);
			}catch (SQLException | ClassNotFoundException e) {
				System.out.println("Error While Establishing DBConnection........");
				e.printStackTrace();
			}
		}
		return con;
	}

}
