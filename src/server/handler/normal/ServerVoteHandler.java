package server.handler.normal;

import dto.ClientState;
import dto.Packet;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVoteHandler extends ServerFeatureHandler {
    protected final User user;
    protected static final ConcurrentHashMap<User, String> votes = new ConcurrentHashMap<>();
    protected static Boolean isVoting = false;
    private final HashSet<String> places = ServerPlaceSuggestHandler.getPlaces();;

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
        clientOutput.writeObject(new Packet<Boolean>(ClientState.PLACE_VOTE, isVoting));

        if(!isVoting) {
            return;
        }

        // 2. 장소 목록 공유
        clientOutput.writeObject(places);

        // 3. 투표 결과 전송
        String place = (String) clientInput.readObject();

        if(votes.containsKey(user)) {
            clientOutput.writeObject(new Packet<String>(ClientState.PLACE_VOTE, "You can't vote again"));
            return;
        }

        synchronized (places) {
            if(!places.contains(place)) {
                clientOutput.writeObject(new Packet<String>(ClientState.PLACE_VOTE, "You voted to the place that doesn't exist"));
                return;
            }
        }
        votes.put(user, place);

        // 4. 투표 결과 처리
        clientOutput.writeObject(new Packet<String>(ClientState.PLACE_VOTE, "You've voted to " + place));
    }
}