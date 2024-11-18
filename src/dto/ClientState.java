package dto;

public enum ClientState {
    CHATTING("chatting"),
    PLACE_SUGGESTION("place suggestion"),
    SCHEDULE("schedule"),
    STATISTIC("statistic"),
    PLACE_VOTE("place vote"),
    HOME("home");

    ClientState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final String name;
}
