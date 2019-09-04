package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import usermanage.UserManage;

/**
 * RMI 服务器
 *
 * @author LBK
 * @version 1.0.0
 */
public class Server {
    /**
     * 启动 RMI 注册服务并进行对象注册
     */
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);

            UserManage usermanage = new UserManage();
            Naming.rebind("UserManage", usermanage);

            System.out.println("UserManage Server is ready.");
        } catch (Exception e) {
            System.out.println("UserManage Server failed: " + e);
        }
    }
}
