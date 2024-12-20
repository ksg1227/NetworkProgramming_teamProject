package server.handler.normal;

import dto.ClientState;
import dto.Packet;
import dto.VoteStatistic;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVoteHandler extends ServerFeatureHandler {
    protected final User user;
    protected static final ConcurrentHashMap<User, String> votes = new ConcurrentHashMap<>();
    protected static Boolean isVoting = false;
    private final HashSet<String> places = ServerPlaceSuggestHandler.getPlaces();
    private static final HashMap<String, Integer> voteStatistic = new HashMap<>();

    public ServerVoteHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients, User user) {
        super(clientInput, clientOutput, onFeatureClients);
        this.user = user;
    }

    @Override
    public void run() {
        try {
            vote();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vote() throws IOException, ClassNotFoundException {
        // 1. 투표가 진행중인지 확인
        sendResponse(isVoting);

        if(!isVoting) {
            return;
        }

        // 2. 이미 투표했는지 확인
        sendResponse(votes.containsKey(user));
        if(votes.containsKey(user)) {
            return;
        }

        // 3. 장소 목록 공유
        sendResponse(places);
        System.out.println("Send " + places + " to " + user.getUserName());

        // 4. 투표 결과 전송
        String place = (String) clientInput.readObject();

        synchronized (places) {
            if(!places.contains(place)) {
                sendResponse("You voted to the place that doesn't exist");
                return;
            }
        }
        votes.put(user, place);
        if(voteStatistic.containsKey(place)) {
            voteStatistic.put(place, voteStatistic.get(place) + 1);
        } else {
            voteStatistic.put(place, 1);
        }

        // 5. 투표 결과 처리
        sendResponse("You've voted to " + place);
    }

    private void sendResponse(Object body) throws IOException {
        clientOutput.writeObject(new Packet<>(ClientState.PLACE_VOTE, body));
    }

    public static VoteStatistic getResult() {
        String maxPlace = "";
        Integer maxCount = 0;

        for (Map.Entry<String, Integer> entry : voteStatistic.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxPlace = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return new VoteStatistic(maxPlace, maxCount);
    }
}
