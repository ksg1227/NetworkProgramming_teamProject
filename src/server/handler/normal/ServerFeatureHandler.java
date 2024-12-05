package server.handler.normal;

import java.io.*;
import java.util.Map;

public abstract class ServerFeatureHandler {
    protected final ObjectInputStream clientInput;
    protected final ObjectOutputStream clientOutput;
    protected final BufferedReader chatReader;
    protected final PrintWriter chatWriter;
    protected final Map<String, ?> onFeatureClients;

    public ServerFeatureHandler(
            ObjectInputStream clientInput,
            ObjectOutputStream clientOutput,
            Map<String, ObjectOutputStream> onFeatureClients
    ) {
        this.clientOutput = clientOutput;
        this.clientInput = clientInput;
        this.chatReader = null;
        this.chatWriter = null;
        this.onFeatureClients = onFeatureClients;
    }

    public ServerFeatureHandler(
            BufferedReader chatReader,
            PrintWriter chatWriter,
            Map<String, PrintWriter> onFeatureClients
    ) {
        this.clientInput = null;
        this.clientOutput = null;
        this.chatReader = chatReader;
        this.chatWriter = chatWriter;
        this.onFeatureClients = onFeatureClients;
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getOnFeatureClients() {
        return (Map<String, T>) onFeatureClients;
    }

    abstract public void run();
}
