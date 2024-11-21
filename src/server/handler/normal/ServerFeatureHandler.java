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
        this.clientInput = clientInput;
        this.clientOutput = clientOutput;
        this.onFeatureClients = onFeatureClients;
    }

    abstract public void run();
}
