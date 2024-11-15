package entity;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private boolean isHost;
    private boolean placeVoteCompleted;
    private boolean scheduleVoteCompleted;

    public User(String userName) {
        this.userName = userName;
        this.isHost = false;
        this.placeVoteCompleted = false;
        this.scheduleVoteCompleted = false;
    }

    public boolean getHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setPlaceVoteCompleted(boolean placeVoteCompleted) {
        this.placeVoteCompleted = placeVoteCompleted;
    }

    public void setScheduleVoteCompleted(boolean scheduleVoteCompleted) {
        this.scheduleVoteCompleted = scheduleVoteCompleted;
    }
}
