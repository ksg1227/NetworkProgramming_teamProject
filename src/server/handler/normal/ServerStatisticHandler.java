package server.handler.normal;

import java.io.*;
import java.util.Map;

public class ServerStatisticHandler extends ServerFeatureHandler {
    public ServerStatisticHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    void run() {

    }
}
