package entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Chat implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String message;
    private Timestamp timestamp;

    public Chat(String userName, String message, Timestamp timestamp) {
        this.userName = userName;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return userName + ": " + message + " - " + timestamp + "\n";
    }
}
