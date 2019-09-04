import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Test {

	private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private static final String serverIP = "localhost";
    private static Socket clientSocket;
	private static final int Dport=8520;
	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
//		clientSocket = new Socket("localhost", 8520);
//		objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
////		objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
//		 Connection connection = new Connection();
		 MD5 md5 = new MD5();
//    	 connection.setType(0);
//    	 connection.setName(md5.digest("test"));
//    	 connection.setInfo(md5.digest("11111"));
//    	 
//         objectOutputStream.writeObject(connection);
//         objectOutputStream.flush();
//         objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
//         Connection  connectionFromServer = (Connection) objectInputStream.readObject();
//         if(connectionFromServer.getName().equals("FAIL")){
//        	 System.out.println("FAIL");
//         }else if(connectionFromServer.getName().equals("SUCCESS")){
//        	
//        	 System.out.println("SUCCESS");
//         }
		Test test = new Test();
		System.out.println(test.getString(md5.digest("test"), md5.digest("11111")));
	}
	
	public String getString(String name, String password) throws IOException, ClassNotFoundException{
		clientSocket = new Socket("localhost", 8520);
		objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//		objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		 Connection connection = new Connection();
		 MD5 md5 = new MD5();
    	 connection.setType(0);
    	 connection.setName(md5.digest("test"));
    	 connection.setInfo(md5.digest("11111"));
    	 
         objectOutputStream.writeObject(connection);
         objectOutputStream.flush();
         objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
         Connection  connectionFromServer = (Connection) objectInputStream.readObject();
         if(connectionFromServer.getName().equals("FAIL")){
        	 System.out.println("FAIL");
        	 return "false";
         }else if(connectionFromServer.getName().equals("SUCCESS")){
        	
        	 System.out.println("SUCCESS");
        	 return "success";
         }
		return password;
	}
	
}
