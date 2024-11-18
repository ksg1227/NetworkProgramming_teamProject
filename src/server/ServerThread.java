package server;

import dto.ClientState;
import dto.Packet;
import entity.User;
import server.handler.normal.ServerPlaceSuggestHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static dto.ClientState.*;
import static java.lang.Boolean.*;

public class ServerThread extends Thread {
    private static Map<String, ObjectOutputStream> onChatClients;
    private static Map<String, ObjectOutputStream> onScheduleClients;
    private static Map<String, ObjectOutputStream> onStatisticClients;
    private static Map<String, ObjectOutputStream> onVoteClients;
    private static Map<String, ObjectOutputStream> onPlaceSuggestClients;

    private final ObjectInputStream clientInput;
    private final ObjectOutputStream clientOutput;

    private final PrintWriter writer = new PrintWriter(System.out, true);

    private final User client;

    public ServerThread(
            Socket socket,
            Map<String, ObjectOutputStream> onChatClients,
            Map<String, ObjectOutputStream> onScheduleClients,
            Map<String, ObjectOutputStream> onStatisticClients,
            Map<String, ObjectOutputStream> onVoteClients,
            Map<String, ObjectOutputStream> onPlaceSuggestClients
    ) {
        ServerThread.onChatClients = onChatClients;
        ServerThread.onScheduleClients = onScheduleClients;
        ServerThread.onStatisticClients = onStatisticClients;
        ServerThread.onVoteClients = onVoteClients;
        ServerThread.onPlaceSuggestClients = onPlaceSuggestClients;

        try {
            this.clientOutput = new ObjectOutputStream(socket.getOutputStream());
            clientOutput.flush();
            this.clientInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            String userName = (String) clientInput.readObject();

            client = new User(userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (ClientOrderGenerator.getClientOrder() == 1) {
            client.setHost(true);
        }

        try {
            clientOutput.writeObject(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 채팅 기능을 관리하는 스레드
    @Override
    public void run() {
        Packet<Object> packet = null;

        while (!currentThread().isInterrupted()) {
            try {
                packet = (Packet<Object>) clientInput.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO : 각각의 기능 구현 필요
            assert packet != null;
            switch (packet.clientState()) {
                case HOME -> {
                    writer.println("home");
                }
                case CHATTING -> {
                    writer.println("chat");
                }
                case SCHEDULE -> {
                    writer.println("schedule");
                }
                case STATISTIC -> {
                    writer.println("statistic");
                }
                case PLACE_VOTE -> {
                    writer.println("vote");
                }
                case PLACE_SUGGESTION -> {
                    writer.println("place-suggest");
                }
                case null, default -> {
                    writer.println("nothing");
                }
            }
        }
    }
}
