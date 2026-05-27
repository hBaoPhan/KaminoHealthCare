package com.example.connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection con = null;
	private static ConnectDB instance = new ConnectDB();
	private static ThreadLocal<Connection> threadLocalCon = new ThreadLocal<>();

	public static Connection getConnection() {
		Connection localCon = threadLocalCon.get();
		try {
			if (localCon == null || localCon.isClosed()) {
				String url = "jdbc:sqlserver://localhost:1433;databaseName=QUANLYKAMINOHEALTHCARE;encrypt=true;trustServerCertificate=true;multipleActiveResultSets=true;";
				String user = "sa";
				String password = "sapassword";
				localCon = DriverManager.getConnection(url, user, password);
				threadLocalCon.set(localCon);
				
				// Keep legacy global var updated for any edge cases
				if (con == null || con.isClosed()) {
					con = localCon;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return localCon;
	}

	public static ConnectDB getInstance() {
		return instance;
	}

	public void connect() throws SQLException {
		getConnection();
	}

	public void disconnnect() {
		Connection localCon = threadLocalCon.get();
		if (localCon != null) {
			try {
				localCon.close();
				threadLocalCon.remove();
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
			Connection localCon = DriverManager.getConnection(url, user, pwd);
			threadLocalCon.set(localCon);
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