package com.example.connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection con = null;
	private static ConnectDB instance = new ConnectDB();

	public static Connection getConnection() {
		return con;
	}

	public static ConnectDB getInstance() {
		return instance;
	}

	public void connect() throws SQLException {
		String url = "jdbc:sqlserver://localhost:1433;databaseName=QUANLYKAMINOHEALTHCARE;encrypt=true;trustServerCertificate=true;";
		String user = "sa";
		String password = "sapassword";
		con = DriverManager.getConnection(url, user, password);
	}

	public void disconnnect() {
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void connect1() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/QUANLYKAMINOHEATHCARE?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		String user = "root";
		String pwd = "sapassword";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pwd);
			System.out.println("✅ Kết nối MySQL thành công!");
		} catch (ClassNotFoundException e) {
			System.out.println("❌ Không tìm thấy driver MySQL JDBC!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("❌ Lỗi kết nối MySQL!");
			e.printStackTrace();
		}
	}

}