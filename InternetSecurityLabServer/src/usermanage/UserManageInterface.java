package usermanage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import bean.User;

public interface UserManageInterface extends Remote {
	
	
	public String checkUser(String username, String password) throws RemoteException; 
   
	public void sendMessage(String sender,String receiver,String message) throws RemoteException;
	
	public ArrayList<String> findAllUser() throws RemoteException;

	public User findUserByName(String username) throws RemoteException;
}
