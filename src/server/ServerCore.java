package server;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerCore {
    private static final Map<String, ObjectOutputStream> onScheduleClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onStatisticClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onVoteClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onPlaceSuggestClients = new HashMap<>();
    private static final Map<String, ObjectOutputStream> onChatClients = new HashMap<>();
    private static ServerSocket serverSocket = null;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while(true) {
            Socket socket = null;

            try {
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
        }
    }
}
