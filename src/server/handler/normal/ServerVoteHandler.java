package server.handler.normal;

import java.io.*;
import java.util.Map;

public class ServerVoteHandler extends ServerFeatureHandler {
    @Override
    public void run() {

    }

    public ServerVoteHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }
}
