

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat_Server {

	private ServerSocket serverSocket;

	private ExecutorService exec;

	// The output stream of information that holds private conversations between users
	private static final int port = 9997;
	private Map<String, PrintWriter> storeInfo;
	/*
	 * �˴�Ϊ�ֽ��������룬Ӧ�û���ͼƬ�Ĵ����н���ʹ��(ͼƬ�Ĵ������Ӧ�ò���֮ǰʵ�ֵ�UDP/TCP�Ĵ������)
	 */
	private static Map<String, OutputStream> storeOutInfo;
	private static Map<String, String> storeCerKey;
	private static Map<String, String> storeUser;
	
	public Chat_Server() {
		try {
			/*
			 * �мǴ�������ʱҪ���г�ʼ��
			 */
			serverSocket = new ServerSocket(port);
			storeInfo = new HashMap<String, PrintWriter>();
			storeCerKey=new HashMap<String,String>();
			storeOutInfo=new HashMap<String, OutputStream>();
			exec = Executors.newCachedThreadPool();
			storeUser=new HashMap<String,String>(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send information to all clients
	 * 
	 * @param message
	 */
	private synchronized void sendToAll(String message) {
		for (PrintWriter out : storeInfo.values()) {
			out.println(message);
		}
	}
	/// Storing the client information in the collection as a Map
	private void putIn(String key, PrintWriter value) {
		synchronized (this) {
			storeInfo.put(key, value);
		}
	}
	private void putOutIn(String key, OutputStream value) {
		synchronized (this) {
			storeOutInfo.put(key, value);
		}
	}
	/**
	 * put key in
	 * @param key
	 * @param value
	 */
	private void putCerKeyIn(String key, String value) {
		synchronized (this) {
			storeCerKey.put(key, value);
		}
	}
	
	
	
	
	/**
	 * put username password in
	 * @param name   hash 
	 * @param passwoord  hash
	 */
	private boolean putRegIn(String name, String password) {
		if(storeUser.get(name)!=null){
			return false;
		}
		else {
		synchronized (this) {
			storeUser.put(name, password);
		}
		return true;
		}
	}
	/**
	 * verify the username and password
	 * @param name
	 * @return
	 */
	private boolean login(String name,String password){
		String pas=storeUser.get(name);
		if(pas==null)return false;
		if(pas.equals(password)){
			return true;
		}
		else{ 
		return false;
		}
	}
	
	
	
	
	
	// Deletes a given output stream from a Shared collection
	private synchronized void remove(String key) {
		storeInfo.remove(key);
		System.out.println(key + " ->quit��");
	}
	/**
	 * 
	 * @param name
	 * @param form
	 * @param sender
	 */
	private synchronized void sendCerKeyToSomeone(String name, String form, String sender) {
		PrintWriter pw1 = storeInfo.get(name);
		PrintWriter pw2=storeInfo.get(sender);
		String key1=storeCerKey.get(name);
		String key2=storeCerKey.get(sender);
//		System.out.println(name+" "+key1);
//		System.out.println(sender+" "+key2);
		if (pw1 != null)
			pw1.println("[Certificate]"  +sender+" "+ key2);
		if (pw2 != null)
			pw2.println("[Certificate]"  +name+" "+ key1);
	}
	/**
	 * ����Կ������û�
	 * @param name
	 * @param form
	 * @param sender
	 */
	private synchronized void sendSecretKeyToSomeone(String form, String secretKey, String sender) {
		PrintWriter pw1 = storeInfo.get(sender);
//		System.out.println(name+" "+key1);
//		System.out.println(sender+" "+key2);
		if (pw1 != null)
			pw1.println( form +" "+"[SecretKey]"+" "+ secretKey);
	}
	/**
	 * 
	 * @param form
	 * @param secretKey
	 * @param sender
	 */
	private synchronized void sendOnlineToSomeone(String receiver) {
		PrintWriter pw1 = storeInfo.get(receiver);
//		storeInfo
		String names=new String();
		for (String name : storeInfo.keySet()) {
//			out.println(message);
			names +=" "+name;
		}
//		System.out.println(names);
		if (pw1 != null)
			pw1.println( "[OnlineUsers:]"+" "+ names);
	}
	/**
	 * Send information to the specific client
	 * 
	 * @param name
	 * @param message
	 */
	private synchronized void sendToSomeone(String form,String name, String message) {
//		System.out.println(form+" "+name);
		PrintWriter pw = storeInfo.get(name); // Take out the corresponding
												// client chat information and
												// send it as private chat
												// content
		if (pw != null){
			if(form.equals("1")){
				pw.println("MessageCaesar"+" "+message);
			}
			
		}
	}

	/**
	 * �����������Ĳ���
	 */
	public void start() {
		System.out.println("Server is starting successfully ,Waiting for connection .");
		try {
			while (true) {

				Socket socket = serverSocket.accept();

				// Gets the client's IP address
				InetAddress address = socket.getInetAddress();
				/*
				 * Start a thread that handles client requests so that the next
				 * client connection can be monitored again
				 */
				exec.execute(new ListenrC(socket)); // Threads are
															// allocated through
															// a thread pool
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The line is used to handle a given client message, loop each string sent
	 * by the client, and output to the console
	 */
	class ListenrC implements Runnable {

		private Socket socket;
		private String name;

		public ListenrC(Socket socket) {
			this.socket = socket;
		}

		// Create an inner class to get a nickname
		private String getName() throws Exception {
			try {
				// The input stream on the server reads the name output stream
				// sent by the client
				BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				// The server sends the result of name validation to the client
				// through its own output stream
				PrintWriter ipw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

				// Read the username from the client
				while (true) {
					
					String nameString = bReader.readLine();
					if ((nameString.trim().length() == 0) || storeInfo.containsKey(nameString)) {
						ipw.println("FAIL");
					} else {
						ipw.println("OK");

						return nameString;
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}

		@Override
		public void run() {
			try {
				/*
				 * Gets the client's output stream through the client's Socket
				 * to send the message to the client
				 */
//				
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				OutputStream out=socket.getOutputStream();
				BufferedReader bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				boolean temp=false;
				String info=bReader.readLine();
				FileReader fr = new FileReader("out.txt");

				BufferedReader bf = new BufferedReader(fr);
				String ow;
				while((ow=bf.readLine())!=null){
					String[] in=ow.split(" ");
					String username=in[0];
					String password=in[1];
					storeUser.put(username, password);
				}
				
//				File fout = new File("User.txt");
				/*
				 * register
				 */
				
				if(info.startsWith("Register")){
					while(temp==false){
//					System.out.println(info);
					String[] in=info.split(" ");
					String name=in[1];
					String password=in[2];
					boolean tem=putRegIn(name, password);
					if(tem==false){
						pw.println("Same");
						info=bReader.readLine();
					}
					else{
					temp=true;
					String ins=name+" "+password+'\n';
					
					FileWriter fw = new FileWriter("out.txt",true);
			        fw.write(ins);
			        fw.close();				        
				        
					pw.println("Register success");
					}
					}
				}
				
				/*
				 * login
				 */
				else if(info.startsWith("Login")){
//					System.out.println(info);
					boolean tem=false;
					while(tem==false){
					String[] in=info.split(" ");
					String name=in[1];
					String password =in[2];
					tem=login(name, password);
					//��֤����
					if(tem==false){
						System.out.println("Login false");
						pw.println("Login false");
						info=bReader.readLine();
					}
					else {
						System.out.println("Login success");
						pw.println("Login success");
					}
					}
				}
				if(temp==true){
				/*
				 * Store the customer nickname and what it says in a HashMap of
				 * the Shared collection
				 */
				name = getName();
				System.out.println("\n" + name + " ->connection succeeded�� ");
				
				putOutIn(name, out);//��ָ�뱨��
				putIn(name, pw);
				
				Thread.sleep(100);

				// The server notifies all clients that a user is online
				sendToAll("[SystemMessage] ��" + name + "��is online");

				/*
				 * Gets the input stream through the client Socket to read the
				 * information sent by the client
				 */
				
				String msgString = null;
				String st=bReader.readLine();//public key+" "+alg form+" "+hash form+" "+time
				if(st.equals("Q")){
					remove(name);
					sendToAll("[SystemMessage] " + name + " is offline");

					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				else {
				String k = null;
				while ((msgString = bReader.readLine()) != null) {
					

				     if (msgString.startsWith("a")) {
//						System.out.println(msgString);
						String[] str = msgString.split(":");
						String theName = str[1];
						k = "1";
						System.out.println(name + " is chatting with " + theName);
						sendCerKeyToSomeone(name, "1", theName);
						/*
						 * ������Կ�ǳ��ִ�����Ϊ��ǰ�趨�����ڴ�
						 */
//						sendFormToSomeone(theName, k, name);
						
					}
					/*
					 * ʵ�ֽ�����������Ա����Ϣ���д����ָ������Ա
					 */
					else if (msgString.startsWith("b")) {
//						System.out.println(msgString);
						sendOnlineToSomeone(name);
						
					}
					
					
					/*
					 * �˴���˼����������һ�����룺��һ�����û�ѡ������ܵķ�ʽʱ���Ѿ�ȷ���������Ĵ��䷽ʽ��֮�����Կ����ֻ����Ҫ���з��ͼ���
					 * 
					 */
					/*
					 * Caesar key share:" 1 secret key "
					 */
					else if (msgString.startsWith("1")) {
						k = "1";
						String[] str = msgString.split(" ");
						String theName = str[2];
						String secretKey=str[1];
//						System.out.println(name + " is chatting with " + theName);
						System.out.println(name+" is sending the Caesar key to "+theName);
//						sendFormToSomeone(theName, k, name);
						sendSecretKeyToSomeone("Caesar",secretKey,theName);
					}
			
					/*
					 * �˴�������Ϣ�Ĵ���˼·�������б���ܷ�ʽ ��ν����ܷ�ʽ�Լ���Ϣ���ݹ�ȥ ����ڽ��յ��û���ȷ�ϼ��ܷ�ʽ�����н���
					 * 
					 * �˴��������ʣ�����secret key�������Ƿ���Ҫ�����û������б��滹��ֻ�豣��һ�����ɣ���
					 */
					else if (msgString.startsWith("@")) {
						int index = msgString.indexOf(":");
						if (index >= 0) {
							
							String theName = msgString.substring(1, index);
							System.out.println(name+" is sending the message to "+theName);
							String inf = msgString.substring(index + ":".length(), msgString.length());

							sendToSomeone(k,theName, inf);
							continue;
						}
					}
			
					
					/*
					 * ��ȡ��ǰ���ߵ��˵���Ϣ
					 */
					else if(msgString.startsWith("b")){
						
					}

//					}
				}
				}
				}
			} catch (Exception e) {
				 e.printStackTrace();
			} finally {
				remove(name);
				// Notifies all clients that client x is offline
				sendToAll("[SystemMessage] " + name + " is offline");

				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		
	}

	public static void main(String[] args) {
		Chat_Server server = new Chat_Server();
		server.start();
	}
}