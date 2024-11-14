package server.handler.normal;

import java.io.*;
import java.util.Map;

public class ServerScheduleHandler extends ServerFeatureHandler {
    public ServerScheduleHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    void run() {

    }
}
