package server;

import dto.Packet;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ServerThread extends Thread {
    private static Map<String, ObjectOutputStream> onChatClients;
    private static Map<String, ObjectOutputStream> onScheduleClients;
    private static Map<String, ObjectOutputStream> onStatisticClients;
    private static Map<String, ObjectOutputStream> onVoteClients;
    private static Map<String, ObjectOutputStream> onPlaceSuggestClients;

    private final ObjectInputStream clientInput;
    private final ObjectOutputStream clientOutput;

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

            assert packet != null;
            switch (packet.clientState()) {
                case HOME -> {
                    System.out.println("home");
                }
                case CHATTING -> {
                    System.out.println("chat");
                }
                case SCHEDULE -> {
                    System.out.println("schedule");
                }
                case STATISTIC -> {
                    System.out.println("statistic");
                }
                case PLACE_VOTE -> {
                    System.out.println("vote");
                }
                case PLACE_SUGGESTION -> {
                    System.out.println("place-suggest");
                }
                case null, default -> {
                    System.out.println("nothing");
                }
            }
        }
    }
}
