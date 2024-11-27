package client.handler;

import dto.ClientState;
import dto.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class ClientPlaceSuggestHandler extends ClientFeatureHandler {
    public ClientPlaceSuggestHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {
        HashSet<String> places = null;
        try {
            Packet<HashSet<String>> packet = (Packet<HashSet<String>>) serverInput.readObject();
            places = packet.body();
            for (String place : places) {
                writer.println(place);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        writer.println("Enter the place you want to add.");
        writer.println("[exit] to go to home");

        String input = scanner.nextLine();
        String response = null;

        response = addPlace(input);

        if(response.equals("exit")) {
            return;
        }

        writer.println(response);
    }

    private String addPlace(String place) {
        Packet<String> packet = createPacket(place);
        String response = null;

        try {
            sendRequest(packet);
            response = getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Packet<String> createPacket(String place) {
        return new Packet<String>(ClientState.PLACE_SUGGESTION, place);
    }

    private void sendRequest(Packet<String> packet) throws IOException {
        serverOutput.writeObject(packet);
        serverOutput.flush();
    }

    private String getResponse() throws IOException, ClassNotFoundException {
        Packet<String> response = (Packet<String>)serverInput.readObject();
        return response.body();
    }
}
