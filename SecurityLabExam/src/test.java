import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import DB.ConnDB;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		MD5 md5 = new MD5();
		FileReader flie = new FileReader("user.txt");
		BufferedReader line = new BufferedReader(flie);
		System.out.println(md5.digest("00002"));
		System.out.println(md5.digest(line.readLine()));
		System.out.println(md5.digest(line.readLine()));
		System.out.println(md5.digest(line.readLine()));
		ConnDB.executeQuery("select * from user");
	}

}
