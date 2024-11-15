package server.handler.normal;

import java.io.*;
import java.util.Map;

public class ServerPlaceSuggestHandler extends ServerFeatureHandler {
    public ServerPlaceSuggestHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    void run() {

    }
}
