package entity;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private boolean isHost;

    public User(boolean isHost) {
        this.isHost = isHost;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", isHost=" + isHost +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public boolean isHost() {
        return isHost;
    }
}
