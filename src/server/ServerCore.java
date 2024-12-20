package server;

import entity.Schedule;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCore extends Thread {
    private static final ConcurrentHashMap<String, ObjectOutputStream> onScheduleClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ObjectOutputStream> onStatisticClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ObjectOutputStream> onVoteClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ObjectOutputStream> onPlaceSuggestClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, PrintWriter> onChatClients = new ConcurrentHashMap<>();
    private static ServerSocket serverSocket = null;
    private final PrintWriter writer = new PrintWriter(System.out, true);

    private static final Schedule globalSchedule = new Schedule();

    public ServerCore() {
        try {
            serverSocket = new ServerSocket(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Schedule getGlobalSchedule() {
        return globalSchedule; // 공유된 globalSchedule 반환
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

            ServerThread serverThread = new ServerThread(
                    socket,
                    onChatClients,
                    onScheduleClients,
                    onStatisticClients,
                    onVoteClients,
                    onPlaceSuggestClients
            );
            serverThread.start();
        }
    }
}
