package server;

import dto.ClientState;
import dto.Packet;
import entity.User;

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

    private static final Map<ClientState, Boolean> checkIsFirstAccess = new HashMap<>();

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

        checkIsFirstAccess.put(PLACE_VOTE, TRUE);
        checkIsFirstAccess.put(SCHEDULE, TRUE);

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

                    if(!isFirstAccess(SCHEDULE)) { // 이미 기능을 사용한 경우
                        continue;
                    }

                    // TODO : 처음으로 기능을 사용하는 경우 실행할 로직

                }
                case STATISTIC -> {
                    writer.println("statistic");
                }
                case PLACE_VOTE -> {
                    writer.println("vote");

                    if(!isFirstAccess(PLACE_VOTE)) { // 이미 기능을 사용한 경우
                        continue;
                    }

                    // TODO : 처음으로 기능을 사용하는 경우 실행할 로직

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


    private boolean isFirstAccess(ClientState state) {
        // 처음 접속한 것이 아니라면
        if (!checkIsFirstAccess.get(state)) {
            try {
                clientOutput.writeObject(false); // 첫번째 접속이 아니라는 것을 client에게 알려줌
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        // 처음 접속한 것이라면
        try {
            clientOutput.writeObject(true); // 첫번째 접속이 맞다는 것을 client에게 알려줌
            checkIsFirstAccess.replace(state, FALSE); // Map의 값도 변경
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
