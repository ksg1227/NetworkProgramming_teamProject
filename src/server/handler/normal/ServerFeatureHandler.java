package server.handler.normal;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public abstract class ServerFeatureHandler {
    private final ObjectInputStream clientInput;
    private final ObjectOutputStream clientOutput;
    private final Map<String, ObjectOutputStream> onFeatureClients;

    public ServerFeatureHandler(
            ObjectInputStream clientInput,
            ObjectOutputStream clientOutput,
            Map<String, ObjectOutputStream> onFeatureClients
    ) {
        this.clientInput = clientInput;
        this.clientOutput = clientOutput;
        this.onFeatureClients = onFeatureClients;
    }

    public ObjectInputStream getClientInput() {
        return clientInput;
    }

    public ObjectOutputStream getClientOutput() {
        return clientOutput;
    }

    public Map<String, ObjectOutputStream> getOnFeatureClients() {
        return onFeatureClients;
    }

    abstract void run();
}
