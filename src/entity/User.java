package entity;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private boolean isHost;

    public User(String userName) {
        this.userName = userName;
        this.isHost = false;
    }

    public boolean getHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", isHost=" + isHost +
                '}';
    }
}
