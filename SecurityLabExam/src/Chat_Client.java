

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




/**
 * 6.2:���û�֮��ķ����Լ����ܵ������Ѿ��������������Ҫ��Զ���û���ͬ���ߵĲ���������֤��ͬʱ׼��UI �Լ�DES�ȼ����㷨
 * 
 * @author dell
 *
 */
public class Chat_Client {
	/*
	 * setting the socket and Other global variables
	 */
	private static Socket clientSocket;

	private String name;

	private static final int port = 9997;
	private static final String serverIP = "127.0.0.1";

	private static String hashVal;

	private static String hashpassword;
	
	/*
	 * ��Կ�ֿ��洢�Ƿ����һЩ��
	 * 
	 * ���ڴ洢���ּ��ܷ�ʽ����Կ
	 */
	// private String secretKey;
	/*
	 * ���ڴ洢Caesar����Կ
	 */

	Chat_Client() throws Exception {

		Scanner sc = new Scanner(System.in);
		setName(sc);
	}

	public static void main(String[] args) throws Exception {

		clientSocket = new Socket(serverIP, port);
		Chat_Client client = new Chat_Client();
		client.start();
	}

	/**
	 * When a user sends a message,mainly implemented functions and methods
	 * ������Ϣ������Ҫ���߼��ṹ��ã�֮���������÷�����Ϣʵ����Ϣ�ô���
	 */
	public void start() {
		try {
			Scanner scanner = new Scanner(System.in);

			// The thread that receives the message sent from the server starts
			ExecutorService exec = Executors.newCachedThreadPool();
//			exec.execute(new ListenrS());

			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			
			
			System.out.println("\nPlease choose enter the system or quit: E.enter Q.quit");
			String ent=scanner.nextLine();
			
			
			/*
			 * ��Ҫ��������Ϣ������������޸�ʱӦע���װ
			 */
			while (true) {
				System.out.println("a.send message to chat(input a)  b.get the online usernames(input b) c.LOCK the system  d.quit");
				System.out.println("----------------------------------");
				String operator = scanner.nextLine();
				String choice;
				
				choice = operator;

				switch (choice) {
				case "a":
					/*
					 * ���choice ѡ�����������ж�
					 */
					System.out.println("\ninput the user name you want to chat: ");
					
					/**
					 * �ڴ˴������޸�֤��Ĳ�����
					 * ע�ⲻҪ����˼ά���ƣ�ֻ����Ҫ�޸�֤�����Ϣ����֮��Ľ�������û�й�ϵ����˲���Ҫ�漰̫�෶Χ
					 * �޸���ɺ���Ҫʵ�ּ��ܵķ�ʽֻ��ʾ��ǰ���������(���Բ���ʾ���ܷ�ʽֱ�ӽ��м���Ҳ����
					 *                                                 ������������û��Ľ������������һ���Ӱ�����ֵ��жϼ��ܷ�ʽ��ִ��)
					 * 
					 */
					
					String username = scanner.nextLine();
					pw.println("a" + ":" + username);
					String ciphertext = null;
					
					Thread.sleep(500);
					System.out.println("please input the encript info you want :M. Message F. file  L.LOCK the system");
					String infoType = scanner.nextLine();
					if (infoType.equals("M")) {

						/*
						 * ȷ��RSA˫����public
						 * key֮�󣬽����û�ѡ����м��ܵ÷�ʽ�����Ҷ��ڼ����е���Կ����RSA��ʽ�ļ��ܣ��Ӷ�ʵ��share
						 * the key
						 */
						Thread.sleep(500);
						
						// Thread.sleep(500);
						/*
						 * replace the enter
						 */
						// key=list.get(0);//��ȡ������Կ�����д���
						Thread.sleep(500);
						ciphertext = ciphertext.replace("\n", "/*1111*/");
						pw.println("@" + username + ":" + ciphertext);
						
						Thread.sleep(500);
						
					}
					break;
				case "b": // �����������֧ʵ�ֻ�ȡ��ǰ��������
					pw.println("b" + ":" + name);
					Thread.sleep(500);
					break;
				}

			}
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setName(Scanner scan) throws Exception {

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
		// Create input stream
		BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

		while (true) {
			System.out.println("client starts successfully...��");
			BufferedReader brs = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			/*
			 * register
			 */
			System.out.println("\nPlease input your username��");
			name = scan.nextLine();			
			
			if (name.trim().equals("")) {
				System.out.println("the name must not be empty ");
			} else {
				pw.println(name);
				Thread.sleep(500);
					
				}
				
					break;
			}
	}

	

}