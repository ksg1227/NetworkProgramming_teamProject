package client.handler;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public abstract class ClientFeatureHandler {
    protected final Scanner scanner = new Scanner(System.in);
    protected final PrintWriter writer = new PrintWriter(System.out, true);
    protected final ObjectInputStream serverInput;
    protected final ObjectOutputStream serverOutput;
    protected BufferedReader chatReader;
    protected PrintWriter chatWriter;
    public ClientFeatureHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
    }
    public ClientFeatureHandler(BufferedReader chatReader, PrintWriter chatWriter) {
        this.serverInput = null;
        this.serverOutput = null;
        this.chatReader = chatReader;
        this.chatWriter = chatWriter;
    }
    public void setChatReader(BufferedReader chatReader) {
        this.chatReader = chatReader;
    }
    public void setChatWriter(PrintWriter chatWriter) {
        this.chatWriter = chatWriter;
    }

    public abstract void run();
}
