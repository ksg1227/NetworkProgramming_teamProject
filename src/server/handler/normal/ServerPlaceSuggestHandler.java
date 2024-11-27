package server.handler.normal;

import dto.ClientState;
import dto.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;

public class ServerPlaceSuggestHandler extends ServerFeatureHandler {
    private static final HashSet<String> places = new HashSet<>();

    public ServerPlaceSuggestHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    public void run() {
        String place = null;
        Packet<String> packet = null;

        try {
            clientOutput.writeObject(new Packet<>(ClientState.PLACE_SUGGESTION, new HashSet(places)));
        } catch (Exception e) {}

        try {
            place = getRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(place.equals("exit")) {
            packet = createPacket("exit");
        } else if(doesPlaceExist(place)) {
            packet = createPacket("place already exist");
        } else {
            if(addPlace(place)) {
                packet = createPacket("Added " + place);
            }
        }

        try {
            sendResponse(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addPlace(String place) {
        synchronized (places) {
            return places.add(place);
        }
    }

    private String getRequest() throws IOException, ClassNotFoundException {
        Packet<String> request = (Packet<String>) clientInput.readObject();
        return request.body();
    }

    private boolean doesPlaceExist(String place) {
        synchronized (places) {
            return places.contains(place);
        }
    }

    private Packet<String> createPacket(String place) {
        return new Packet<>(ClientState.PLACE_SUGGESTION, place);
    }

    private void sendResponse(Packet<String> packet) throws IOException {
        clientOutput.writeObject(packet);
        clientOutput.flush();
    }

    public static HashSet<String> getPlaces() {
        return new HashSet<>(places);
    }
}
