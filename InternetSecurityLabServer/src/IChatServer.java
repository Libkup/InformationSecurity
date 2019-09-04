/**
 * Created by xuhuan on 2017/11/24.
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class IChatServer implements ActionListener{
    private static ServerSocket serverSocket;
    public static HashMap<String, ConnectionInfo> onlines;
    private static AbstractListModel listmodel;
    private static JList list;
    private static Vector on;
    static {
        try {
            serverSocket = new ServerSocket(8520);
            onlines = new HashMap<String, ConnectionInfo>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private HashMap<String,String> publickey = new HashMap<String,String>();
    private JFrame f=new JFrame("Server");
    private JPanel p;
    private JButton closeWinButton = new JButton("C");
    private JButton startButton = new JButton("S");
    private JTextArea showMessage=new JTextArea();
    private JLabel l=new JLabel("The port number:");
    private JTextField port=new JTextField("8520");
    public IChatServer(){

        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(200,100,688,510);
        p=new JPanel(){
            protected void paintComponent(java.awt.Graphics g){
                super.paintComponent(g);
                g.drawImage(new ImageIcon("1.png").getImage(),0,0,getWidth(), getHeight(), null);
            }
        };
        f.setContentPane(p);
        p.setLayout(null);

        JScrollPane chatMessageScrollPane = new JScrollPane();
        chatMessageScrollPane.setBounds(10, 50, 340, 410);
        f.getContentPane().add(chatMessageScrollPane);
        showMessage.setEditable(false);
        chatMessageScrollPane.setOpaque(false);
        chatMessageScrollPane.getViewport().setOpaque(false);
        chatMessageScrollPane.setBorder(new TitledBorder("chat record:"));
        showMessage.setOpaque(false);
        showMessage.setFont(new Font("sdf", Font.BOLD, 13));
        chatMessageScrollPane.setViewportView(showMessage);


        closeWinButton.setBounds(10, 10, 60, 30);
        f.getContentPane().add(closeWinButton);
        closeWinButton.addActionListener(this);


        startButton.setBounds(100, 10, 60, 30);
        f.getContentPane().add(startButton);
        startButton.addActionListener(this);

        port.setEditable(false);
        l.setBounds(190,10,60,30);
        port.setBounds(250,10,60,30);
        f.getContentPane().add(l);
        f.getContentPane().add(port);

        on=new Vector();
        listmodel = new ListModel(on) ;
        list = new JList(listmodel);
        list.setCellRenderer(new CellRenderer());  //调用set方法为列表绘制组件（该组件为自定义的CellRenderer）
        list.setOpaque(false);
        Border etchedBorder = BorderFactory.createEtchedBorder();
        list.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Online buddy list", TitledBorder.LEADING, TitledBorder.TOP, new Font(
                "sdf", Font.BOLD, 20), Color.CYAN));
        JScrollPane onlineUserScrollPane = new JScrollPane(list);
        onlineUserScrollPane.setBounds(360, 10, 310, 450);
        onlineUserScrollPane.setOpaque(false);
        onlineUserScrollPane.getViewport().setOpaque(false);
        f.getContentPane().add(onlineUserScrollPane);

        f.setVisible(true);
        JOptionPane.showMessageDialog(f,"The server has started");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==closeWinButton){
            int flag=JOptionPane.showConfirmDialog(f,"Is the server turned off?","Warning",JOptionPane.YES_NO_CANCEL_OPTION);
            if(flag==0){
                System.exit(0);
            }
//            closeWinButton.setEnabled(false);
//            startButton.setEnabled(true);
        }
        if(e.getSource()==startButton){
            start();
//            startButton.setEnabled(false);
//            closeWinButton.setEnabled(true);
        }
    }

    class IChatThread extends Thread {
        private Socket client;
        private Connection connection;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public IChatThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // 不断的从客户端接收信息
                while (true) {
                    // 读取从客户端接收到的Connnection信息
                    ois = new ObjectInputStream(client.getInputStream());
//                    System.out.println(ois.toString());
                    connection = (Connection) ois.readObject();
//                    System.out.println(connection.getName());

                    switch (connection.getType()) {

                        case 0: { // 上线
                            // 记录上线客户的用户名和端口在clientConnectionInfo中
                        	Properties userPro = new Properties();
                            File file = new File("Users.properties");
                            LoadUser.loadPro(userPro, file);
                            String user_name = connection.getName();
                        	String user_pwd = connection.getInfo();
//                        	System.out.println(user_pwd);
//                            ConnectionInfo clientConnectionInfo = new ConnectionInfo();
//                            clientConnectionInfo.setName(userPro.getProperty(user_name).split(",")[1]);
//                            clientConnectionInfo.setSocket(client);
//                            // 添加在线用户
//                            onlines.put(userPro.getProperty(user_name).split(",")[1], clientConnectionInfo);
                            // 创建服务器的Connection，并发送给客户端
                            Connection serverConnection = new Connection();
							if (userPro.containsKey(user_name)) {
								if (user_pwd.equals(userPro.getProperty(user_name).split(",")[0])) {
									System.out.println("success");
									
									publickey.put(user_name, connection.getPublicKey().get(user_name));
									Set<String> get = publickey.keySet(); 
							        for (String test:get) {
							        	System.out.println(test+","+publickey.get(test));
							        }
									ConnectionInfo clientConnectionInfo = new ConnectionInfo();
		                            clientConnectionInfo.setName(userPro.getProperty(user_name).split(",")[1]);
		                            clientConnectionInfo.setSocket(client);
		                            // 添加在线用户
		                            onlines.put(userPro.getProperty(user_name).split(",")[1], clientConnectionInfo);
									serverConnection.setName("SUCCESS");
									serverConnection.setType(0);
		                            serverConnection.setInfo(connection.getTimer() + "  "
		                                    + userPro.getProperty(user_name).split(",")[1] + " on line");
		                            serverConnection.setPublicKey(publickey);
		                            // 通知所有客户有人上线
		                            HashSet<String> set = new HashSet<String>();
		                            set.addAll(onlines.keySet());
		                            serverConnection.setClients(set);
		                            showMessage.append(connection.getTimer() + "  "
		                                    + userPro.getProperty(user_name).split(",")[1] + " on line"+"\n");
		                            String ele = userPro.getProperty(user_name).split(",")[1];
		                            on.add(ele);
		                            listmodel = new ListModel(on);
		                            list.setModel(listmodel);
								} else {
									Connection serverConnection1 = new Connection();
		                            serverConnection1.setType(0);
		                            serverConnection1.setName("FAIL");

		                            try {
		                                oos = new ObjectOutputStream(
		                                        client.getOutputStream());
		                                oos.writeObject(serverConnection1);
		                                oos.flush();
		                            } catch (IOException e) {
		                                // TODO Auto-generated catch block
		                                e.printStackTrace();
		                            }
								}
							}else{
								Connection serverConnection1 = new Connection();
	                            serverConnection1.setType(0);
	                            serverConnection1.setName("FAIL");

	                            try {
	                                oos = new ObjectOutputStream(
	                                        client.getOutputStream());
	                                oos.writeObject(serverConnection1);
	                                oos.flush();
	                            } catch (IOException e) {
	                                // TODO Auto-generated catch block
	                                e.printStackTrace();
	                            }
							}
                            sendAll(serverConnection);
                            break;
                        }
                        case 10:{
                        	Properties userPro = new Properties();
                            File file = new File("Users.properties");
                            LoadUser.loadPro(userPro, file);
                        	String user_name = connection.getName();
                        	String user_pwd = connection.getInfo();
                        	System.out.println(user_pwd);
                        	userPro.setProperty(user_name, user_pwd);
	                        try {
	                            userPro.store(new FileOutputStream(file),
	                                    "The file manage user and passworld @Albert");
	                        }catch(Exception e){
	                        	
	                        }
                        	break;
                        }
                        case -1: { // 下线

                            Connection serverConnection = new Connection();
                            serverConnection.setType(-1);

                            try {
                                oos = new ObjectOutputStream(
                                        client.getOutputStream());
                                oos.writeObject(serverConnection);
                                oos.flush();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            onlines.remove(connection.getName());

                            // 向剩下的在线用户发送有人离开的通知
                            Connection serverConnection2 = new Connection();
                            serverConnection2.setInfo(connection.getTimer() + "  "
                                    + connection.getName() + " " + " offline");
                            serverConnection2.setType(0);
                            HashSet<String> set = new HashSet<String>();
                            set.addAll(onlines.keySet());
                            serverConnection2.setClients(set);
                            sendAll(serverConnection2);
                            showMessage.append(connection.getTimer() + "  "
                                    + connection.getName() + " offline"+"\n");
                            on.remove(connection.getName());
                            listmodel = new ListModel(on);
                            list.setModel(listmodel);
                            return;
                        }
                        case 1: { // 聊天

//						 创建服务器的serverConnection，并发送给客户端
                            Connection serverConnection = new Connection();

                            serverConnection.setType(1);
                            serverConnection.setClients(connection.getClients());
                            serverConnection.setInfo(connection.getInfo());
                            serverConnection.setName(connection.getName());
                            serverConnection.setTimer(connection.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverConnection);
                            showMessage.append(connection.getTimer()+" "+connection.getName()+" "
                            +"to"+connection.getClients()+" say"+connection.getInfo()+"\n");
                            break;
                        }
                        case 2: {//群发
                            Connection serverConnection1 = new Connection();

                            serverConnection1.setType(2);
                            serverConnection1.setInfo(connection.getInfo());
                            serverConnection1.setName(connection.getName());
                            serverConnection1.setTimer(connection.getTimer());
                            HashSet<String> set = new HashSet<String>();
                            
                            set.addAll(onlines.keySet());
                            serverConnection1.setClients(set);
                            sendAll(serverConnection1);
                            showMessage.append(connection.getTimer()+" "+connection.getName()+
                                    " say"+connection.getInfo()+"\n");
                            break;
                        }
                        case 3: {
                        	Connection serverConnection = new Connection();

                            serverConnection.setType(3);
                            serverConnection.setClients(connection.getClients());
                            serverConnection.setInfo(connection.getInfo());
                            serverConnection.setName(connection.getName());
                            serverConnection.setTimer(connection.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverConnection);
                            System.out.println("info " + connection.getInfo());
                            System.out.println("info " + connection.getName());
                            showMessage.append(connection.getTimer()+" "+connection.getName()+" "
                            +"to"+connection.getClients()+" say"+connection.getInfo()+"\n");
                            break;
                        }
                        case 4: {
                        	Connection serverConnection = new Connection();

                            serverConnection.setType(4);
                            serverConnection.setClients(connection.getClients());
                            serverConnection.setInfo(connection.getInfo());
                            serverConnection.setName(connection.getName());
                            serverConnection.setTimer(connection.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverConnection);
                        }
                        default: {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                close();
            }
        }

        // 向选中的用户发送数据
        private void sendMessage(Connection serverConnection) {
            // 首先取得所有的values
            Set<String> cbs = onlines.keySet();
            Iterator<String> it = cbs.iterator();
            // 选中客户
            HashSet<String> clients = serverConnection.getClients();
            while (it.hasNext()) {
                // 在线客户
                String client = it.next();
                // 选中的客户中若是在线的，就发送serverConnection
                if (clients.contains(client)) {
                    Socket c = onlines.get(client).getSocket();
                    ObjectOutputStream oos;
                    try {
                        oos = new ObjectOutputStream(c.getOutputStream());
                        oos.writeObject(serverConnection);
                        oos.flush();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }

        // 向所有的用户发送数据
        public void sendAll(Connection serverBean) {
            Collection<ConnectionInfo> clients = onlines.values();
            Iterator<ConnectionInfo> it = clients.iterator();
            ObjectOutputStream oos;
            while (it.hasNext()) {
                Socket c = it.next().getSocket();
                try {
                    oos = new ObjectOutputStream(c.getOutputStream());
                    oos.writeObject(serverBean);
                    oos.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void close() {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void start() {
        try {
            while (true) {
                Socket client = serverSocket.accept();
                new IChatThread(client).start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void close(){
        Socket client = null;
        try {
            client = serverSocket.accept();
            new IChatThread(client).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        IChatServer server=new IChatServer();
        server.start();
    }

}
