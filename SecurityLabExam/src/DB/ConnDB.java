package DB;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class ConnDB {
	public static Connection conn = null;
	public static Statement stmt = null;
	public static ResultSet rs = null;
	private static String dbClassName = "org.mariadb.jdbc.Driver";
	private static String url = "jdbc:mariadb://localhost/SecurityLabExam";
	private static String user = "root";
	private static String password = "root";

	public ConnDB() {
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		conn = null;
		try {
			Class.forName(dbClassName).newInstance();
			conn = DriverManager.getConnection(url, user, password);
			if(conn == null){
			}else{
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		if (conn == null) {
			System.err.println("����: DbConnectionManager.getConnection() ������ݿ�����ʧ��.\r\n\r\n��������:" + dbClassName
					+ "\r\n����λ��:" + url);
		}
		return conn;
	}

	/*
	 * ���ܣ�ִ�в�ѯ���
	 */
	public static ResultSet executeQuery(String sql) throws SQLException {
		try {
			conn = getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			conn.close();
		} catch (SQLException ex) {
			conn.close();
			System.err.println(ex.getMessage());
		}
		return rs;
	}

	/*
	 * ����:ִ�и��²���
	 */
	public static int executeUpdate(String sql) throws SQLException {
		int result = 0;
		try {
			conn = getConnection(); // ����getConnection()��������Connection�����һ��ʵ��conn
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			result = stmt.executeUpdate(sql); // ִ�и��²���
			conn.close();
		} catch (SQLException ex) {
			conn.close();
			result = 0;
		}
		return result;
	}

	/*
	 * ����:�ر����ݿ������
	 */
	public static void close() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}
