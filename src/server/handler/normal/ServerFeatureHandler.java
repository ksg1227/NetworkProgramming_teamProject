package server.handler.normal;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public abstract class ServerFeatureHandler {
    protected final ObjectInputStream clientInput;
    protected final ObjectOutputStream clientOutput;
    protected final Map<String, ObjectOutputStream> onFeatureClients;

    public ServerFeatureHandler(
            ObjectInputStream clientInput,
            ObjectOutputStream clientOutput,
            Map<String, ObjectOutputStream> onFeatureClients
    ) {
        this.clientOutput = clientOutput;
        this.clientInput = clientInput;
        this.onFeatureClients = onFeatureClients;
    }

    public ServerFeatureHandler(ObjectOutputStream clientOutput, ObjectInputStream clientInput) {
        this.clientOutput = clientOutput;
        this.clientInput = clientInput;
        this.onFeatureClients = null;
    }
    public Map<String, ObjectOutputStream> getOnFeatureClients() {
        return onFeatureClients;
    }
    abstract public void run();
}