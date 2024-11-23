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
    public ServerChatHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients,User client) {
        super(clientInput, clientOutput, onFeatureClients);
        this.client = client;
    }
    public void broadcast(Chat chat) {
        saveChats(chat);
        Map<String, ObjectOutputStream> onChatClients = this.getOnFeatureClients();

        synchronized (onChatClients) {
            Collection<ObjectOutputStream> collection = onChatClients.values();
            for (ObjectOutputStream clientOutput : collection) {
                try {
                    // Chat 객체를 모든 클라이언트에게 전송
                    clientOutput.writeObject(chat);
                    clientOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

            if(!chat.getUserName().equals("Server")){ //클라이언트 입장 메시지는 저장 안 함
                recentChats.addLast(chat);
            }

        }
    }



    @Override
    public void run() {
          broadcast(new Chat("Server",
                  client.getUserName()+"님이 채팅방에 입장하셨습니다.",
                  Timestamp.valueOf(LocalDateTime.now())));
        try {
            showRecentChats();
            while (true) {
                Chat receivedChat = (Chat) clientInput.readObject();

                if (receivedChat.getMessage().equalsIgnoreCase("/q")) {
                    broadcast(new Chat("Server",
                            client.getUserName()+"님이 채팅방에서 나가셨습니다.",
                            Timestamp.valueOf(LocalDateTime.now())));
                    break;
                }


                broadcast(receivedChat);
            }
        } catch (Exception e) {
            System.out.println("Connection lost with: " + client.getUserName());
            e.printStackTrace();
        } finally {

            synchronized (getOnFeatureClients()) {
                getOnFeatureClients().remove(client.getUserName());
            }
            broadcast(new Chat("Server",
                    client.getUserName()+"님의 연결이 종료되었습니다.",
                    Timestamp.valueOf(LocalDateTime.now())));
        }
    }

}
