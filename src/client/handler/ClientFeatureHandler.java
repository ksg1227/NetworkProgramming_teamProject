package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public abstract class ClientFeatureHandler {
    protected final Scanner scanner = new Scanner(System.in);
    protected final PrintWriter writer = new PrintWriter(System.out, true);
    protected final ObjectInputStream serverInput;
    protected final ObjectOutputStream serverOutput;

    public ClientFeatureHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
    }

    public abstract void run();
}
