package usermanage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bean.User;

/**
 * @author LBK
 * @version 1.0.0
 * @see java.rmi.server.UnicastRemoteObject
 * @see ssd8.rmi.rface.MeetingInterface
 */
public class UserManage extends UnicastRemoteObject implements UserManageInterface{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<User> users = new ArrayList<>();

    private static int meetingID = 0;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");


    /**
     * 为维护方便，所有返回信息写在此处
     */
    private static final String REGISTER_USER_SUCCESS = "注册成功！";
    private static final String REGISTER_USER_FAILURE = "注册失败！";

    public UserManage() throws RemoteException {
        super();
    }
    
    @Override
    public String checkUser(String username, String password) throws RemoteException {
        User user = findUserByName(username);
        if(user != null){
        	if(user.getPassword().equals(password))
        		return "login success";
        	else
        		return "login failed, wrong passord";
        }else{
        	user = new User(username, password);
        	users.add(user);
        	return "register success";
        }
    }

    private boolean isUserExist(String username) {
        boolean isExist = false;
        for (User user : users) {
            if (user.getName().equals(username)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }
    
    @Override
    public User findUserByName(String name){
    	for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
    	return null;
    }
    
    @Override
    public void sendMessage(String sender,String receiver,String message){
    	User send = null,receive = null;
    	for(User user : users){
    		if(user.getName().equals(sender))
    			send = user;
    		if(user.getName().equals(receiver))
    			receive = user;
    	}
    	
    	receive.getMessages().add(message);
    	
    }

    @Override
    public ArrayList<String> findAllUser(){
    	ArrayList<String> allUser = new ArrayList<String>();
    	for(User user : users){
    		allUser.add(user.getName());
    	}
    	return allUser;
    }
    
    
    
}
