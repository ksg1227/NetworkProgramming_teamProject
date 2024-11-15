package server.handler.normal;

import dto.ClientState;
import dto.Packet;

import java.io.*;
import java.util.Collection;
import java.util.Map;

public class ServerChatHandler extends ServerFeatureHandler {
    public ServerChatHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    public void broadcast(String message) {
        Map<String, ObjectOutputStream> onChatClients = this.getOnFeatureClients();

        synchronized (onChatClients) {
            Collection<ObjectOutputStream> collection = onChatClients.values();
            for (ObjectOutputStream clientOutput : collection) {
                try {
                    clientOutput.writeObject(new Packet<String>(ClientState.CHATTING, "hello"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    void run() {

    }
}
