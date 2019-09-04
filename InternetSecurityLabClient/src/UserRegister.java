/**
 * Created by xuhuan on 2017/11/19.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class UserRegister  {

    JFrame f=new JFrame("Register");
    JPanel p0=new JPanel();
    JPanel p1=new JPanel();
    JPanel p2=new JPanel();
    JPanel p3=new JPanel();
    JPanel p4=new JPanel();
    
    private static ObjectOutputStream objectOutputStream;
	private static ObjectInputStream objectInputStream;
    private int flag = 0;
    private JTextField nameTextField;
    private JPasswordField passwordField;
    private JPasswordField passwordField_ag;
    JLabel name_jLabel=new JLabel("Account");
    JLabel psw_jLabel=new JLabel("Password");
    JLabel psw_again_jLabel=new JLabel("Re-enter password");
    JButton registerButton=new JButton("Ok");
    JButton backButton=new JButton("Cancel");
//    public static void main(String[] args) {
//        UserRegister register=new UserRegister();
//
//    }

    public UserRegister() {
        nameTextField=new JTextField(15);
        passwordField=new JPasswordField(15);
        passwordField_ag=new JPasswordField(15);
//        registerButton.addActionListener(this);
//        backButton.addActionListener(this);
        Font font=new Font("Black body", 1, 15);
        registerButton.setFont(font);registerButton.setForeground(SystemColor.blue);
        registerButton.setContentAreaFilled(false);
        backButton.setFont(font);backButton.setForeground(SystemColor.blue);
        backButton.setContentAreaFilled(false);
        name_jLabel.setFont(font);name_jLabel.setForeground(Color.blue);
        psw_jLabel.setFont(font);psw_jLabel.setForeground(Color.blue);
        psw_again_jLabel.setFont(font);psw_again_jLabel.setForeground(Color.blue);


        ImageIcon image=new ImageIcon("sourse/注册.png");
        Image i=image.getImage();
        i=i.getScaledInstance(400,300,Image.SCALE_DEFAULT);
        image.setImage(i);
        JLabel jl=new JLabel(image);
        jl.setBounds(0,0,image.getIconWidth(),image.getIconHeight());

        f.getLayeredPane().add(jl,new Integer(Integer.MIN_VALUE));
        Container con=f.getContentPane();
        con.setLayout(new BorderLayout());
        p0.setLayout(new GridLayout(3,1));
        p0.setBorder(new TitledBorder("Please input"));
        p1.add(name_jLabel);p1.add(nameTextField);
        p2.add(psw_jLabel);p2.add(passwordField);
        p3.add(psw_again_jLabel);p3.add(passwordField_ag);
        p4.add(registerButton);p4.add(backButton);
        p0.add(p1);p0.add(p2);p0.add(p3);
        con.add(p0,BorderLayout.CENTER);
        con.add(p4,BorderLayout.SOUTH);
        p0.setOpaque(false);
        p1.setOpaque(false);
        p2.setOpaque(false);
        p3.setOpaque(false);
        p4.setOpaque(false);
        ((JPanel)con).setOpaque(false);

        f.setSize(400,300);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        //返回按钮事件监听
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backButton.setEnabled(false);
                //返回登陆界面
                Login frame = new Login();
                f.dispose();
            }
        });

        //注册按钮事件监听
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Properties userPro = new Properties();
                File file = new File("Users.properties");
                LoadUser.loadPro(userPro, file);

                String user_name = nameTextField.getText();
                String user_pwd = new String(passwordField.getPassword());
                String user_pwd_ag = new String(passwordField_ag.getPassword());

                // 判断用户名是否在普通用户中已存在
                if (user_name.length() != 0) {

                    if (userPro.containsKey(user_name)) {
                        JOptionPane.showMessageDialog(f,"User already exists");
                        nameTextField.setText("");
                        passwordField.setText("");
                        passwordField_ag.setText("");
                    } else {
                        isPassword(userPro, file, user_name, user_pwd, user_pwd_ag);
                    }
                } else {
                   JOptionPane.showMessageDialog(f,"Username can not be empty！");
                }
            }

            public void isPassword(Properties userPro,
                                    File file, String user_name, String user_pwd, String user_pwd_ag) {
                if (user_pwd.equals(user_pwd_ag)) {
                    if (user_pwd.length() != 0) {
                    	MD5 md5 = new MD5();
                        userPro.setProperty(md5.digest(user_name) , md5.digest(user_pwd_ag) + "," + user_name);
                        try {
                            userPro.store(new FileOutputStream(file),
                                    "The file manage user and passworld @Albert");
                            
                            Socket clientSocket = new Socket("localhost", 8520);
                            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                            // 记录上线客户的信息在Connection中，并发送给服务器
                            
                            Connection con = new Connection();
                            con.setType(10);
                            con.setName(md5.digest(user_name));
                            con.setInfo(md5.digest(user_pwd_ag) + "," + user_name);
                            con.setTimer(LoadUser.getTimer());
                            objectOutputStream.writeObject(con);
                            objectOutputStream.flush();
                            flag = 1;
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        registerButton.setEnabled(false);
                        //返回登陆界面
                        Login frame = new Login();
                        f.dispose();

                    } else {
                        JOptionPane.showMessageDialog(f,"Password is empty！");
                    }
                } else {
                    JOptionPane.showMessageDialog(f,"Inconsistent passwords!");
                }
            }
        });
    }
    
    public String getNameAndPassword(){
    	if(flag == 1)
    		return nameTextField.getText() + "," + new String(passwordField.getPassword());
    	else
    		return "false";
    }
}
