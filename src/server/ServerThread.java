package server;

import dto.Packet;
import entity.User;
import server.handler.normal.ServerChatHandler;
import server.handler.host.HostVoteHandler;
import server.handler.normal.ServerPlaceSuggestHandler;
import server.handler.normal.ServerVoteHandler;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread extends Thread {
    private static ConcurrentHashMap<String, PrintWriter> onChatClients;
    private static ConcurrentHashMap<String, ObjectOutputStream> onScheduleClients;
    private static ConcurrentHashMap<String, ObjectOutputStream> onStatisticClients;
    private static ConcurrentHashMap<String, ObjectOutputStream> onVoteClients;
    private static ConcurrentHashMap<String, ObjectOutputStream> onPlaceSuggestClients;

    private final ObjectInputStream clientInput;
    private final ObjectOutputStream clientOutput;
    private final BufferedReader chatReader;
    private final PrintWriter chatWriter;
    private final PrintWriter writer = new PrintWriter(System.out, true);

    private final User user;

    public ServerThread(
            Socket socket,
            ConcurrentHashMap<String, PrintWriter> onChatClients,
            ConcurrentHashMap<String, ObjectOutputStream> onScheduleClients,
            ConcurrentHashMap<String, ObjectOutputStream> onStatisticClients,
            ConcurrentHashMap<String, ObjectOutputStream> onVoteClients,
            ConcurrentHashMap<String, ObjectOutputStream> onPlaceSuggestClients
    ) {
        ServerThread.onChatClients = onChatClients;
        ServerThread.onScheduleClients = onScheduleClients;
        ServerThread.onStatisticClients = onStatisticClients;
        ServerThread.onVoteClients = onVoteClients;
        ServerThread.onPlaceSuggestClients = onPlaceSuggestClients;

        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            this.clientOutput = new ObjectOutputStream(out);
            clientOutput.flush();
            this.clientInput = new ObjectInputStream(in);
            this.chatWriter = new PrintWriter(out, true);
            this.chatReader = new BufferedReader(new InputStreamReader(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        user = new User(ClientOrderGenerator.getClientOrder() == 1);
    }

    // 채팅 기능을 관리하는 스레드
    @Override
    public void run() {
        setUserName();

        notifyUserInfoToClient();

        Integer EMPTY_BODY = 0;

        System.out.println(user.getUserName() + " joined");
        Packet<Integer> packet = null;

        while (!currentThread().isInterrupted()) {
            try {
                packet = (Packet<Integer>) clientInput.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            assert packet != null;
            assert packet.body().equals(EMPTY_BODY);

            switch (packet.clientState()) {
                case HOME -> {
                    System.out.println("home");
                }
                case CHATTING -> {
                    new ServerChatHandler(chatReader,chatWriter,onChatClients,user).run();
                }
                case SCHEDULE -> {
                    System.out.println("schedule");
                }
                case STATISTIC -> {
                    System.out.println("statistic");
                }
                case PLACE_VOTE -> {
                    onVoteClients.put(user.getUserName(), clientOutput);
                    if(user.isHost()) {
                        new HostVoteHandler(clientInput, clientOutput, onVoteClients, user).run();
                    } else {
                        new ServerVoteHandler(clientInput, clientOutput, onVoteClients, user).run();
                    }
                    onVoteClients.remove(user.getUserName());
                }
                case PLACE_SUGGESTION -> {
                    onPlaceSuggestClients.put(user.getUserName(), clientOutput);
                    new ServerPlaceSuggestHandler(clientInput, clientOutput, onPlaceSuggestClients).run();
                    onPlaceSuggestClients.remove(user.getUserName());
                }
                case null, default -> {
                    System.out.println("nothing");
                }
            }
        }
    }

    private void notifyUserInfoToClient() {
        try {
            clientOutput.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUserName() {
        try {
            String userName = (String) clientInput.readObject();

            user.setUserName(userName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
