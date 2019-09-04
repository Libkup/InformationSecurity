package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LBK
 * @version 1.0.1
 * @see java.io.Serializable
 */
public class User implements Serializable {
    private String name;
    private String password;
    private List<String> messages;
    public User(String name, String password) {
        super();
        this.name = name;
        this.password = password;
        this.messages = new ArrayList<String>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!name.equals(user.name)) return false;
        return password != null ? password.equals(user.password) : user.password == null;
    }

    @Override
    public String toString() {
        return "[name: " + name + ",password:" + password + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public ArrayList<String> getMessages() {
		return (ArrayList<String>) messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
