/**
 * Created by xuhuan on 2017/11/19.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Caesar;
import client.Des1;
import client.IDEA;
import client.Playfair;
import client.RSA;

/*
* 该类实现了好友列表*/
class CellRenderer extends JLabel implements ListCellRenderer {
    CellRenderer() {
        setOpaque(true);//设置是否透明
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// 加入宽度为5的空白边框

        if (value != null) {
            setText(value.toString());
            setIcon(new ImageIcon("sourse/头像.png"));
        }
        if (isSelected) {
            setBackground(new Color(255, 255, 153));// 设置背景色
            setForeground(Color.black);
        } else {
            // 设置选取与取消选取的前景与背景颜色.
            setBackground(Color.white); // 设置背景色
            setForeground(Color.black);
        }
        setEnabled(list.isEnabled());
        setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
        setOpaque(true);
        return this;
    }
}


class ListModel extends AbstractListModel{

    private Vector vs;

    public ListModel(Vector vs){
        this.vs = vs;
    }

    @Override
    public Object getElementAt(int index) {
        // TODO Auto-generated method stub
        return vs.get(index);
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return vs.size();
    }

}

/*
* 该类实现了会话框主要功能*/
public class ChatRoom extends JFrame implements ActionListener {

    private static final long serialVersionUID = 6129126482250125466L;

    private static JPanel main_jPanel;
    private static Socket clientSocket;
    private static ObjectOutputStream objectOutputStream;
    private static ObjectInputStream objectInputStream;
    private static String name;
    private static String password;
    private static JTextArea showMessageTextArea;
    private static AbstractListModel listmodel;
    private static JList list;
    private static Vector onlines;
    private static MD5 md5 = new MD5();
    JTextArea editMessageTextArea = new JTextArea();
    private String currentAlgorithm = "DES";
    private JMenuBar mb1=new JMenuBar();
    private JMenu m1=new JMenu("Algorithm");
//    private JMenu m2=new JMenu("color");
//    private JMenu m3=new JMenu("font");
//    private JMenu m4=new JMenu("state");
    private JMenuItem mi1=new JMenuItem("Playfair Cypher");
    private JMenuItem mi2=new JMenuItem("Cesare Cypher");
    private JMenuItem mi3=new JMenuItem("DES");
    private JMenuItem mi4=new JMenuItem("IDEA");
//    private JMenuItem mi5=new JMenuItem("Font1");
//    private JMenuItem mi6=new JMenuItem("Font2");
//    private JMenuItem mi7=new JMenuItem("Font3");
//    private JMenuItem mi8=new JMenuItem("Disconnect from the server");
//    private JMenuItem mi9=new JMenuItem("Start connecting to the server");
    JButton sendMessageButton = new JButton("S");
    HashMap<String,String> publicKey = new  HashMap<String,String>();
    private String currentPeople = "";
    private String currentPulbicKey = "";
    private String selfPublicKey = "";
    private String selfPrivateKey = "";
    private String symKey = "";
    public DatagramSocket dataSocket;
    public DatagramPacket dataPacket;
    public static byte[] receiveByte;
    DatagramSocket datagramSocket;
    /**
     * 创建会话界面.
     */

    public ChatRoom(String user_name, String pass_word, Socket client) {
        // 赋值
        name = user_name;
        password = pass_word;
        clientSocket = client;
        onlines = new Vector();

        SwingUtilities.updateComponentTreeUI(this); //刷新UI界面

        setTitle(name);
        setResizable(false); //不可自由改变大小（更新版本的时候，这个地方可做改进）
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(200, 100, 688, 510);
        main_jPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("sourse/聊天背景.jpg").getImage(), 0, 0,
                        getWidth(), getHeight(), null);
            }

        };
        setContentPane(main_jPanel);
        main_jPanel.setLayout(null);

        // 聊天信息显示区域
        JScrollPane chatMessageScrollPane = new JScrollPane();
        chatMessageScrollPane.setBounds(260, 10, 410, 320);
        getContentPane().add(chatMessageScrollPane);

        showMessageTextArea = new JTextArea();
        showMessageTextArea.setEditable(false);
        showMessageTextArea.setLineWrap(true);//可以自动换行
        showMessageTextArea.setWrapStyleWord(true);//断行不断字
        showMessageTextArea.setFont(new Font("sdf", Font.BOLD, 13));
        chatMessageScrollPane.setViewportView(showMessageTextArea);   //将showMessageTextArea添加到滚动窗口内

        mb1.add(m1);
        m1.add(mi1);m1.add(mi2);m1.add(mi3);m1.add(mi4);
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi4.addActionListener(this);
        mb1.setBounds(260,330,410,30);
        getContentPane().add(mb1);
        // 消息编辑区域
        JScrollPane editMessageScrollPane = new JScrollPane();
        editMessageScrollPane.setBounds(260, 360, 410, 80);
        getContentPane().add(editMessageScrollPane);

        
        editMessageTextArea.setLineWrap(true);//可以自动换行
        editMessageTextArea.setWrapStyleWord(true);//设置断行不断字
        editMessageScrollPane.setViewportView(editMessageTextArea);

        // 关闭按钮
        final JButton closeWinButton = new JButton("Close");
        closeWinButton.setBounds(214, 400, 60, 30);
//        getContentPane().add(closeWinButton);

        // 发送按钮

        sendMessageButton.setBounds(610, 448, 60, 30);
        getRootPane().setDefaultButton(sendMessageButton);
        getContentPane().add(sendMessageButton);

        // 在线用户列表
        listmodel = new ListModel(onlines) ;
        list = new JList(listmodel);
        list.setCellRenderer(new CellRenderer());  //调用set方法为列表绘制组件（该组件为自定义的CellRenderer）
        list.setOpaque(false);
        Border etchedBorder = BorderFactory.createEtchedBorder();  //调用BorderFactory中的方法创建一个四周有凹痕的边界
        //建立一个设置了标题名称，位置，字体等参数的标题边界
        list.setBorder(BorderFactory.createTitledBorder(etchedBorder, "Online buddy list", TitledBorder.LEADING, TitledBorder.TOP, new Font(
                "sdf", Font.BOLD, 20), Color.CYAN));
        
        JScrollPane onlineUserScrollPane = new JScrollPane(list);
        onlineUserScrollPane.setBounds(10, 10, 240, 400);
        onlineUserScrollPane.setOpaque(false);
        onlineUserScrollPane.getViewport().setOpaque(false);
        getContentPane().add(onlineUserScrollPane);
        

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            // 记录上线客户的信息在Connection中，并发送给服务器
            
            Connection con = new Connection();
            con.setType(0);
            con.setName(md5.digest(name));
            con.setInfo(md5.digest(password));
            con.setTimer(LoadUser.getTimer());
            HashMap<String,String> key = new  HashMap<String,String>();
            RSA rsa = new RSA();
           
            selfPublicKey = rsa.getPublicKey();
            selfPrivateKey = rsa.getPrivateKey();
            System.out.println(selfPublicKey);
            System.out.println(selfPrivateKey);
            key.put(md5.digest(name),selfPublicKey);
            con.setPublicKey(key);
            objectOutputStream.writeObject(con);
            objectOutputStream.flush();

            // 启动客户接收线程
            new ClientInputThread().start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Failed to put message to server");
            e.printStackTrace();
        }

        // 发送按钮事件监听
        sendMessageButton.addActionListener(this);

        // 关闭按钮
        closeWinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    closeWinButton.setEnabled(false);
                    Connection clientBean = new Connection();
                    clientBean.setType(-1);
                    clientBean.setName(name);
                    clientBean.setTimer(LoadUser.getTimer());
                    sendMessage(clientBean);
            }
        });

        // 离开
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                    int result = JOptionPane.showConfirmDialog(getContentPane(),
                            "Are you sure you want to leave the chat room?");
                    if (result == 0) {
                        Connection clientConnection = new Connection();
                        clientConnection.setType(-1);
                        clientConnection.setName(name);
                        clientConnection.setTimer(LoadUser.getTimer());
                        sendMessage(clientConnection);
                    }

            }
        });

        // 列表监听
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                List SelectedTo = list.getSelectedValuesList();
                if (e.getClickCount() == 2) {

                    if (SelectedTo.toString().contains(name+"(yourself)")) {
                        JOptionPane.showMessageDialog(getContentPane(), "Can't send files to yourself");
                        return;
                    }else{
                    	
                    }
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				List<String> to = list.getSelectedValuesList();
				if (e.getClickCount() == 2) {//双击
					
					if (to.toString().contains(name+"(myself)")) {
						JOptionPane.showMessageDialog(getContentPane(), "Cannot send files to yourself");
						return;
					}
					
					// 双击打开文件文件选择框
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Select the file"); // 窗口标题
					chooser.showDialog(getContentPane(), "Select"); // 按钮的名字

					// 判定是否选择了文件
					if (chooser.getSelectedFile() != null) {
						// 获取路径
						String filePath = chooser.getSelectedFile().getPath();
						File file = new File(filePath);
						System.out.println(file.getPath());
						file.getAbsolutePath();
						//// 文件为空
						//if (file.length() == 0) {
						//	JOptionPane.showMessageDialog(getContentPane(),
						//			filePath + "文件为空,不允许发送.");
						//	return;
						//}
						String filedigit="";
						try {
							BufferedReader br;
							br = new BufferedReader(new FileReader(file));
							MD5 md5 = new MD5();
							String line = "";
							
							while((line = br.readLine()) != null){
								line += filedigit;
								filedigit = md5.digest(line);
							}
							br.close();
						} catch (FileNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						Connection clientCon = new Connection();
						clientCon.setType(4); 
						clientCon.setName(name);//发送消息的socket的客户端昵称
						clientCon.setTimer(LoadUser.getTimer());
						clientCon.setInfo(file.getPath()+" "+file.length()+" " + filedigit);

						// 判断要发送给谁
						HashSet set = new HashSet();
	                    set.addAll(to);
	                    clientCon.setClients(set);
						sendMessage(clientCon);
						try {
							datagramSocket = new DatagramSocket(2020);
							new Thread(new UDPHandler(datagramSocket)).start();
						} catch (SocketException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						showMessageTextArea.append("file send successfully" + "\r\n");
					}
				}
			}
		});

        
        list.addListSelectionListener(new ListSelectionListener() { 
        	
        	@Override 
        	public void valueChanged(ListSelectionEvent e) { 
        		if(e.getValueIsAdjusting()){
        			List selectedTo = list.getSelectedValuesList();

                    Connection clientCon = new Connection();
                    clientCon.setType(3);
                    clientCon.setName(list.getSelectedValue().toString());
                    System.out.println(list.getSelectedValue().toString());
                    String time = LoadUser.getTimer();
                    clientCon.setTimer(time);
//                    clientCon.setInfo("info test");
                    RSA rsa = new RSA();
	                  System.out.println("qqqq" + currentAlgorithm);
	                  System.out.println(rsa.RSAEncryptionWords("Playfair MYSECRETKEY", publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0], publicKey.get(md5.digest(name)).split("-")[1]));
	                  if(currentAlgorithm.equals("Playfair")){
	                	symKey = "MYSECRETKEY";
//	                  	System.out.println("tsefsfdsdfds");
//	                  	System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
	                  	clientCon.setInfo(rsa.RSAEncryptionWords("Playfair MYSECRETKEY", publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0], publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
	                  }else if(currentAlgorithm.equals("Cesare")){
	                	symKey = "10";
	                  	System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
	                  	clientCon.setInfo(rsa.RSAEncryptionWords("Cesare " + symKey, publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0], publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
	                  }else if(currentAlgorithm.equals("DES")){
		                	symKey = "DESkeyaa";
		                  	System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
		                  	clientCon.setInfo(rsa.RSAEncryptionWords("DES " + symKey, publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0], publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
		              }
                    HashSet set = new HashSet();
                    set.addAll(selectedTo);
                    clientCon.setClients(set);

                    // 自己发的内容也要现实在自己的屏幕上面
//                    showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");

                    sendMessage(clientCon);
                    
        		}
        	}
        	
        });
        
        
    }
    //事件监听
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==mi1){
        	currentAlgorithm = "Playfair";
        	if(list.getSelectedValue() != null){
	        	List selectedTo = list.getSelectedValuesList();
	
	            Connection clientCon = new Connection();
	            clientCon.setType(3);
	            clientCon.setName(list.getSelectedValue().toString());
	            System.out.println(list.getSelectedValue().toString());
	            String time = LoadUser.getTimer();
	            clientCon.setTimer(time);
	//            clientCon.setInfo("info test");
				RSA rsa = new RSA();
				System.out.println("qqqq" + currentAlgorithm);
				System.out.println(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
						publicKey.get(md5.digest(name)).split("-")[1]));
				if (currentAlgorithm.equals("Playfair")) {
					symKey = "MYSECRETKEY";
					// System.out.println("tsefsfdsdfds");
					// System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("Cesare")) {
					symKey = "10";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Cesare " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("DES")) {
					symKey = "DESkeyaa";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("DES " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("IDEA")) {
					symKey = "sdfsdg";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("IDEA " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				}
				HashSet set = new HashSet();
				set.addAll(selectedTo);
				clientCon.setClients(set);
	
	            // 自己发的内容也要现实在自己的屏幕上面
	//            showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");
	
	            sendMessage(clientCon);
        	}
        }
        if(e.getSource()==mi2){
        	currentAlgorithm = "Cesare";
        	if(list.getSelectedValue() != null){
	        	List selectedTo = list.getSelectedValuesList();
	
	            Connection clientCon = new Connection();
	            clientCon.setType(3);
	            clientCon.setName(list.getSelectedValue().toString());
	            System.out.println(list.getSelectedValue().toString());
	            String time = LoadUser.getTimer();
	            clientCon.setTimer(time);
	//            clientCon.setInfo("info test");
				RSA rsa = new RSA();
				System.out.println("qqqq" + currentAlgorithm);
				System.out.println(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
						publicKey.get(md5.digest(name)).split("-")[1]));
				if (currentAlgorithm.equals("Playfair")) {
					symKey = "MYSECRETKEY";
					// System.out.println("tsefsfdsdfds");
					// System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("Cesare")) {
					symKey = "10";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Cesare " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("DES")) {
					symKey = "DESkeyaa";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("DES " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("IDEA")) {
					symKey = "sdfsdg";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("IDEA " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				}
	            HashSet set = new HashSet();
	            set.addAll(selectedTo);
	            clientCon.setClients(set);
	
	            // 自己发的内容也要现实在自己的屏幕上面
	//            showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");
	
	            sendMessage(clientCon);
        	}
        }
        if(e.getSource()==mi3){
        	currentAlgorithm = "DES";
        	if(list.getSelectedValue() != null){
	        	List selectedTo = list.getSelectedValuesList();
	
	            Connection clientCon = new Connection();
	            clientCon.setType(3);
	            clientCon.setName(list.getSelectedValue().toString());
	            System.out.println(list.getSelectedValue().toString());
	            String time = LoadUser.getTimer();
	            clientCon.setTimer(time);
	//            clientCon.setInfo("info test");
				RSA rsa = new RSA();
				System.out.println("qqqq" + currentAlgorithm);
				System.out.println(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
						publicKey.get(md5.digest(name)).split("-")[1]));
				if (currentAlgorithm.equals("Playfair")) {
					symKey = "MYSECRETKEY";
					// System.out.println("tsefsfdsdfds");
					// System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("Cesare")) {
					symKey = "10";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("Cesare " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("DES")) {
					symKey = "DESkeyaa";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("DES " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				} else if (currentAlgorithm.equals("IDEA")) {
					symKey = "sdfsdg";
					System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
					clientCon.setInfo(rsa.RSAEncryptionWords("IDEA " + symKey,
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
							publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
				}
	            HashSet set = new HashSet();
	            set.addAll(selectedTo);
	            clientCon.setClients(set);
	
	            // 自己发的内容也要现实在自己的屏幕上面
	//            showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");
	
	            sendMessage(clientCon);
        	}
        }
        if(e.getSource()==mi4){
        	currentAlgorithm = "IDEA";
        	if(list.getSelectedValue() != null){
        		List selectedTo = list.getSelectedValuesList();

                Connection clientCon = new Connection();
                clientCon.setType(3);
                clientCon.setName(list.getSelectedValue().toString());
                String time = LoadUser.getTimer();
                clientCon.setTimer(time);
//                clientCon.setInfo("info test");
    			RSA rsa = new RSA();
    			System.out.println("qqqq" + currentAlgorithm);
    			System.out.println(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
    					publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
    					publicKey.get(md5.digest(name)).split("-")[1]));
    			if (currentAlgorithm.equals("Playfair")) {
    				symKey = "MYSECRETKEY";
    				// System.out.println("tsefsfdsdfds");
    				// System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
    				clientCon.setInfo(rsa.RSAEncryptionWords("Playfair MYSECRETKEY",
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
    			} else if (currentAlgorithm.equals("Cesare")) {
    				symKey = "10";
    				System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
    				clientCon.setInfo(rsa.RSAEncryptionWords("Cesare " + symKey,
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
    			} else if (currentAlgorithm.equals("DES")) {
    				symKey = "DESkeyaa";
    				System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
    				clientCon.setInfo(rsa.RSAEncryptionWords("DES " + symKey,
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
    			} else if (currentAlgorithm.equals("IDEA")) {
    				symKey = "sdfsdg";
    				System.out.println(publicKey.get(md5.digest(list.getSelectedValue().toString())));
    				clientCon.setInfo(rsa.RSAEncryptionWords("IDEA " + symKey,
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[0],
    						publicKey.get(md5.digest(list.getSelectedValue().toString())).split("-")[1]));
    			}
                HashSet set = new HashSet();
                set.addAll(selectedTo);
                clientCon.setClients(set);

                // 自己发的内容也要现实在自己的屏幕上面
//                showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");

                sendMessage(clientCon);
        	}
        }
        if(e.getSource()==sendMessageButton){
            String info = editMessageTextArea.getText();
            try {
            	showMessageTextArea.append(LoadUser.getTimer() + " you send to " + name + ": " + info + "\r\n");
            	if(currentAlgorithm.equals("Playfair")){
            		Playfair p = new Playfair(symKey);
                	info = p.PlayfairEncryption(info);
            	}else if(currentAlgorithm.equals("Cesare")){
            		Caesar c = new Caesar(Integer.valueOf(symKey));
            		info = c.CaesarEncryption(info);
                }else if(currentAlgorithm.equals("DES")){
                	info = Des1.encrypt(info, symKey, "encrypt");
                }else if(currentAlgorithm.equals("IDEA")){
                	IDEA idea = new IDEA();
            		byte[] encryptdata = idea.IdeaEncrypt(symKey.getBytes("ISO-8859-1"), info.getBytes("ISO-8859-1"), true);
                	info = info.length() + " " + new String(encryptdata,"ISO-8859-1");
                }
				System.out.println(info);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            
            List selectedTo = list.getSelectedValuesList();

            if (selectedTo.size() < 1) {

                Connection clients=new Connection();
                clients.setType(2);
                clients.setName(name);
                String time = LoadUser.getTimer();
                clients.setTimer(time);
                clients.setInfo(info);



                //showMessageTextArea.append(time + " 我说:\r" + info + "\r\n");



                sendMessage(clients);
                editMessageTextArea.setText(null);
                editMessageTextArea.requestFocus();
                return;
            }
            if (selectedTo.toString().contains(name+"(yourself)")) {
                JOptionPane.showMessageDialog(getContentPane(), "Can't send messages to yourself");
                return;
            }
            if (info.equals("")) {
                JOptionPane.showMessageDialog(getContentPane(), "Cannot send empty message");
                return;
            }

            Connection clientCon = new Connection();
            clientCon.setType(1);
            clientCon.setName(name);
            String time = LoadUser.getTimer();
            clientCon.setTimer(time);
            clientCon.setInfo(info);
            HashSet set = new HashSet();
            set.addAll(selectedTo);
            clientCon.setClients(set);

            // 自己发的内容也要现实在自己的屏幕上面
//            showMessageTextArea.append(time + " 我对"+clientCon.getName()+"说:\r" + info + "\r\n");

            sendMessage(clientCon);
            editMessageTextArea.setText(null);
            editMessageTextArea.requestFocus();
        }



    }


    //接收消息线程
    class ClientInputThread extends Thread {

        @Override
        public void run() {
            try {
                // 不停的从服务器接收信息
                while (true) {
                    objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    final Connection  connection = (Connection) objectInputStream.readObject();
                    System.out.println(connection.getType());
                    switch (connection.getType()) {
                        case -1: {
                            return;
                        }
                        case 0: {
                            // 更新列表
                            onlines.clear();
                            System.out.println(connection.getName());
                            if(connection.getName().equals("FAIL")){
                            	Connection clientBean = new Connection();
                                clientBean.setType(-1);
                                clientBean.setName("temp");
                                clientBean.setTimer(LoadUser.getTimer());
                                sendMessage(clientBean);
                            }
                            publicKey = connection.getPublicKey();
//                            Set<String> get = publicKey.keySet(); 
//					        for (String test:get) {
//					        	System.out.println(test+","+publicKey.get(test));
//					        }
                            HashSet<String> clients = connection.getClients();
                            Iterator<String> it = clients.iterator();
                            while (it.hasNext()) {
                                String ele = it.next();
                                if (name.equals(ele)) {
                                    onlines.add(ele + "(yourself)");
                                } else {
                                    onlines.add(ele);
                                }
                            }

                            listmodel = new ListModel(onlines);
                            list.setModel(listmodel);
                            //byte[] c = connection.getInfo().getBytes();
                            //byte[] p = des.deal(c,0);
                            //byte[] p_d = new byte[des.p_origin_length];
                            //System.arraycopy(p,0,p_d,0,c.length);
                            
                            showMessageTextArea.append(connection.getInfo() + "\r\n");
                            //System.out.println(connection.getInfo());
                            showMessageTextArea.selectAll();
                            break;
                        }
                        case 1: {

                            String info = connection.getTimer() + "  " + connection.getName()
                                    + " say to me: \r";
                            if (info.contains(name) ) {
                                info = info.replace(name, "Me");
                            }
//                            byte[] c = connection.getInfo().getBytes("ISO-8859-1");
//                            byte[] p = des.deal(connection.getInfo().getBytes(),0);
//                            byte[] p_d = new byte[des.getP_origin_length()];
//                                    System.arraycopy(p,0,p_d,0,des.getP_origin_length());
//                            for(int i = 0;i < p.length; i++){
//                            	System.out.println(p[i]);
//                            }
                            if(currentAlgorithm.equals("Playfair")){
                        		Playfair p = new Playfair(symKey);
                            	info = info + p.PlayfairDecryption(connection.getInfo());
                        	}else if(currentAlgorithm.equals("Cesare")){
                        		Caesar c = new Caesar(Integer.valueOf(symKey));
                            	info = info + c.CaesarDecryption(connection.getInfo());
                        	}else if(currentAlgorithm.equals("DES")){
    		                	info = info + Des1.encrypt(connection.getInfo(), symKey, "decrypt");
    		                	info = info.replace("*", " ");
                        	}else if(currentAlgorithm.equals("IDEA")){
                        		IDEA idea = new IDEA();
                        		int length = Integer.valueOf(connection.getInfo().split(" ")[0]);
                        		byte[] dncryptdata = idea.IdeaEncrypt(symKey.getBytes("ISO-8859-1"), connection.getInfo().split(" ")[1].getBytes("ISO-8859-1"), false);
                        		char[] temp = new char[length];
                        		char[] data = new String(dncryptdata,"ISO-8859-1").toCharArray();
                        		System.out.println(new String(dncryptdata));
                        		String result = "";
                        		for(int i = 0; i < temp.length ; i++){
                        			result += data[i];
                        		}
                            	info += result;
                        	}
                            showMessageTextArea.append(info + "\r\n");
                            System.out.println("test" + connection.getInfo());
                            showMessageTextArea.selectAll();
                            break;
                        }
                        //
                        case 2: {
                            String info = connection.getTimer() + "  " + connection.getName()
                                    + "say:\r";
                            if (info.contains(name) ) {
                                info = info.replace(name, "Me");
                            }
                          
                            showMessageTextArea.append(info+connection.getInfo() + "\r\n");
                           //System.out.println(connection.getInfo());
                            showMessageTextArea.selectAll();
                            break;
                        }
                        case 4:{
                        	String receiveRoot = "F:\\";
        					String filePath = connection.getInfo().split(" ")[0];
        					long fileLength = Long.parseLong(connection.getInfo().split(" ")[1]);
        					System.out.println("File Path: " + filePath + "  File Length: " + fileLength);
        					dataSocket = new DatagramSocket();
        					System.out.println("dataSocket");
        					byte[] info = filePath.getBytes();
        					DatagramPacket send = new DatagramPacket(info, info.length, new InetSocketAddress("127.0.0.1", 2020));
        					String fileName = filePath.substring(filePath.lastIndexOf("\\"), filePath.length());
//        					String fileName = filePath;
        					dataSocket.send(send);

        					String receiveAddress = receiveRoot+fileName;
        					System.out.println("Downloading File to: " + receiveAddress);
        					
        					DataOutputStream fileOut = new DataOutputStream(     // Transfer File to "D:\\receive\\"
        							new BufferedOutputStream(new FileOutputStream(receiveAddress)));
        					receiveByte = new byte[1024];
        					dataPacket = new DatagramPacket(receiveByte, receiveByte.length, new InetSocketAddress("127.0.0.1", 2020));
        					while (true) {
//        						System.out.println("File Receiving");
        						
        						dataSocket.receive(dataPacket);
        						
        						if (new String(dataPacket.getData(), 0, dataPacket.getLength()).equals("end.")) {
        							System.out.println("File Transfer Finish.");
        							fileOut.close();
        							break;
        						}
        						int length = dataPacket.getLength();
        						if (length > 0) {
        							int count = 0;
        							for(int i = 0;i <receiveByte.length;i++){
        								if(receiveByte[i] == 0)
        									break;
        								count ++;
        							}
        							byte[] receiveByteModfied = new byte[count];
        							for(int i = 0;i <count;i++){
        								receiveByteModfied[i] = receiveByte[i];
        							}
        							fileOut.write(receiveByteModfied, 0, count);
        							fileOut.flush();
        						}
        					}
        					File file = new File(receiveAddress);
        					BufferedReader br = new BufferedReader(new FileReader(file));
        					MD5 md5 = new MD5();
        					String line = "";
        					String curr="";
        					while((line = br.readLine()) != null){
        						line += curr;
        						curr = md5.digest(line);
        					}
        					br.close();
        					if(connection.getInfo().split(" ")[2].equals(curr))
        						showMessageTextArea.append("received file in " + receiveAddress + " from "+ connection.getName() + ", and the hash value has matched.\r\n");
        					else	
        						showMessageTextArea.append("received file in " + receiveAddress + ", and the hash value don't match, please check and send again.\r\n");
                        	break;
                        }
                        case 10: {
                        	System.out.println("chatroom");
                        	break;
                        }

                        case 3: {
                        	RSA rsa = new RSA();
                        	System.out.println("info " + connection.getInfo());
                        	System.out.println(selfPrivateKey.split("-")[0]);
                        	System.out.println(selfPrivateKey.split("-")[1]);
                        	String temp = rsa.RSADecryptionWords(connection.getInfo(), selfPrivateKey.split("-")[0], selfPrivateKey.split("-")[1]);
                        	currentAlgorithm = temp.split(" ")[0];
                        	symKey = temp.split(" ")[1];
//                        	System.out.println(temp);
                            break;
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
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
    }
    //发送消息
    private void sendMessage(Connection clientBean) {
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(clientBean);
            objectOutputStream.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Sending message failed");
//            System.exit(0);
            e.printStackTrace();
        }
    }

}
