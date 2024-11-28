package server.handler.normal;

import entity.Chat;
import entity.User;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class ServerChatHandler extends ServerFeatureHandler {
    protected final User client;
    private static final int MAX_RECENT_CHAT = 10;
    private static final Deque<Chat> recentChats = new LinkedList<>();

    public ServerChatHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput,
                             Map<String, ObjectOutputStream> onFeatureClients, User client) {
        super(clientInput, clientOutput, onFeatureClients);
        this.client = client;
    }

    private void showRecentChats() {
        try {
            synchronized (recentChats) {
                for (Chat chat : recentChats) {
                    clientOutput.writeObject(chat);
                }
                clientOutput.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveChats(Chat chat) {
        synchronized (recentChats) {
            if (recentChats.size() >= MAX_RECENT_CHAT) {
                recentChats.pollFirst(); // 가장 오래된 메시지 제거
            }
            if(chat.getUserName().equals("Server")) return;
            recentChats.addLast(chat);
        }
    }

    public void broadcast(Chat chat) {
        saveChats(chat);
        Map<String, ObjectOutputStream> onChatClients = this.getOnFeatureClients();

        synchronized (onChatClients) {
            Collection<ObjectOutputStream> collection = onChatClients.values();
            for (ObjectOutputStream clientOutput : collection) {
                try {
                    clientOutput.writeObject(chat);
                    clientOutput.reset();
                    clientOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        Map<String, ObjectOutputStream> onChatClients = this.getOnFeatureClients();
        synchronized (onChatClients){
            onChatClients.put(client.getUserName(), clientOutput);
        }

        broadcast(new Chat("Server",
                client.getUserName() + "님이 채팅방에 입장하셨습니다.",
                Timestamp.valueOf(LocalDateTime.now())));

        try {
            // 최근 채팅 내역 전송
            showRecentChats();

            while (true) {
                Object receivedObject = clientInput.readObject();

                if (receivedObject instanceof Chat receivedChat) {
                    if (receivedChat.getMessage().equals("/q")) {
                        synchronized (onChatClients){
                            onChatClients.remove(client.getUserName());
                        }
                        broadcast(new Chat("Server",
                                client.getUserName() + "님이 채팅방에서 나가셨습니다.",
                                Timestamp.valueOf(LocalDateTime.now())));
                        break;
                    }
                    broadcast(receivedChat);
                } else {
                    System.out.println("알 수 없는 데이터 수신: " + receivedObject);
                }
            }
        } catch (EOFException e) {
            System.out.println(client.getUserName() + " 연결이 종료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
