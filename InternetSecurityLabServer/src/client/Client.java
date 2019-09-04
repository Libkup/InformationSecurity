package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import usermanage.UserManage;
import usermanage.UserManageInterface;

/**
 * RMI 客户端
 *
 * @author LBK
 * @version 1.0
 *
 */
public class Client {

    private static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 为维护方便，所有操作结果信息写在此处
     */
    private static final String WRONG_PARAMETER = "wrong parameter";
    private static int k = 10;
    private static String key = "MYSECRETKEY";
    private static UserManageInterface rmi;
    private static Caesar caesar;
    private static RSA rsa;
    private static Playfair playfair;
    private static DES des;

    public static void main(String[] args) throws Exception {
    	if(args.length < 2){
    		System.err.println(WRONG_PARAMETER);
    		System.exit(0);
    	}
    	String username = args[0];
    	String password = args[1];
    	System.out.println(username + " online...");
    	rmi = (UserManageInterface) Naming.lookup("UserManage");
    	
    	String check = rmi.checkUser(username, password);
    	if(check.equals("login failed, wrong passord")){
    		System.out.println("login failed, wrong passord");
    		System.exit(0);
    	}
    		
    	
    	while(true){
    		rsa = new RSA();
    		caesar = new Caesar(k);
    		playfair = new Playfair(key);
            System.out.println("Input an operation: ");
            System.out.println("1. send message: ");
            System.out.println("2. check my message: ");
            System.out.print("your input:");
            String operation = bf.readLine();
            System.out.println();
            if(operation.equals("1")){
            	sendMessage(username);
            	System.out.println("send successfully...");
            	System.out.println();
            }else if(operation.equals("2")){
            	checkMessage(username);
            	System.out.println();
            }
        }
    	
    }
    
    public static void sendMessage(String username) throws Exception{
    		System.out.println("select a user you want to send message (input the number):");
    		ArrayList<String> allUser = rmi.findAllUser();
    		int i = 1;
    		for(String user : allUser){
    			System.out.println(i + ". " + user);
    			i++;
    		}
    		System.out.print("your input:");
    		int number = Integer.valueOf(bf.readLine());
    		System.out.println();
    		System.out.println("input your message to be sent:");
    		String message = bf.readLine();
    		message = "from " + username + " -> " + message;
    		System.out.println();
    		System.out.println("select a way to encryt:\n"
    						+ "1. Caesar\n"
    						+ "2. Playfair\n"
    						+ "3. RSA\n"
    						+ "4. RSA with digital signature\n"
    						+ "5. DES");
    		System.out.print("your input:");
    		int encrytion = Integer.valueOf(bf.readLine());
    		switch (encrytion){
    			case 1:
    				message = caesar.CaesarEncryption(message);
        			message = "1" + message;
        			break;
    			case 2:
    				message = playfair.PlayfairEncryption(message);
        			message = "2" + message;
        			break;
    			case 3:
    				message = rsa.RSAEncryptionWords(message);
        			message = "3" + message;
        			break;
    			case 4:
    				message += "@MD5:" + rsa.signE(rsa.MD5(message));
        			message = rsa.RSAEncryptionWords(message);
        			message = "4" + message;
        			break;
    			case 5:
    				des = new DES("DES",message);
    				byte[] c = des.deal(message.getBytes(),1);
    				message = new String(c,"ISO-8859-1");
//    				System.out.println(message.length());
//        			message = new String("5".getBytes(),"ISO-8859-1") + message;
//        			for(int j = 0;i < message.getBytes("ISO-8859-1").length-1; j ++){
//	    				System.out.println(message.getBytes("ISO-8859-1")[j]);
//	    			}
        			break;
    		}
    			
//    		if(encrytion == 1){
//    			message = caesar.CaesarEncryption(message);
//    			message = "1" + message;
//    		}else if(encrytion == 2){
//    			message = playfair.PlayfairEncryption(message);
//    			message = "2" + message;
//    		}else if(encrytion == 3){
//    			message = rsa.RSAEncryptionWords(message);
//    			message = "3" + message;
//    		}else if(encrytion == 4){
//    			message += "@MD5:" + rsa.signE(rsa.MD5(message));
//    			message = rsa.RSAEncryptionWords(message);
//    			message = "4" + message;
//    		}
    		rmi.sendMessage(username, allUser.get(number-1), message);
    	}
    
    public static void checkMessage(String username) throws Exception{
    	ArrayList<String> messages = rmi.findUserByName(username).getMessages();
    	int i = 0;
    	if(messages.size() == 0)
    		System.out.println("There are no messages... \n");
    		
    	else{
    		for(String m : messages){
    			if(m.startsWith("1")){
	    			m = m.substring(1, m.length());
	    			m = caesar.CaesarDecryption(m);
	    		}else if(m.startsWith("2")){
	    			m = m.substring(1, m.length());
	    			m = playfair.PlayfairDecryption(m);
	    		}else if(m.startsWith("3")){
	    			m = m.substring(1, m.length());
	    			m = rsa.RSADecryptionWords(m);
	    		}else if(m.startsWith("4")){
	    			m = m.substring(1, m.length());
	    			m = rsa.verification(m);
	    		}else {
	    			m = m.substring(1, m.length());
//	    			for(int j = 0;i < m.getBytes("ISO-8859-1").length; j ++){
//	    				System.out.println(m.getBytes("ISO-8859-1")[j]);
//	    			}
	    			des = new DES("DES",m);
	    			byte[] p = des.deal(m.getBytes("ISO-8859-1"), 0);
	    			m = new String(p);
	    		}
	    		System.out.println("message " + i +":-----------");
	    		System.out.println(m.toLowerCase());
	    		i++;
	    	}
    	}
    }
    
}
