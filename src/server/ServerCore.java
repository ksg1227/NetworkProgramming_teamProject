package server;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerCore extends Thread {
    private static final Map<String, ObjectOutputStream> onScheduleClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onStatisticClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onVoteClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onPlaceSuggestClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onChatClients = new HashMap<>();
    private static ServerSocket serverSocket = null;
    private final PrintWriter writer = new PrintWriter(System.out, true);

    public ServerCore() {
        try {
            serverSocket = new ServerSocket(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        writer.println("Server started");

        while(true) {
            Socket socket = null;

            try {
                writer.println("Waiting for connection");
                socket = serverSocket.accept();
            } catch (Exception e) {
                e.printStackTrace();
            }

            assert socket != null;

            ServerThread chatServerThread = new ServerThread(
                    socket,
                    onChatClients,
                    onScheduleClients,
                    onStatisticClients,
                    onVoteClients,
                    onPlaceSuggestClients
            );
            chatServerThread.start();

            writer.println("Add client");
        }
    }
}
