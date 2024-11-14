package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class ClientFeatureHandler {
    private final ObjectInputStream serverInput;
    private final ObjectOutputStream serverOutput;

    public ClientFeatureHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
    }

    public ObjectInputStream getServerInput() {
        return serverInput;
    }

    public abstract void run();
}
