package server.handler.host;

import server.handler.normal.ServerScheduleHandler;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class HostScheduleHandler extends ServerScheduleHandler {
    public HostScheduleHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    public void run() {
        super.run();
    }
}
