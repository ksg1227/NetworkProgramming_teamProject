package server.handler.normal;

import entity.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class ServerChatHandler extends ServerFeatureHandler {
    protected final User client;
    private static final int MAX_RECENT_CHAT = 10;
    private static final Deque<String> recentChats = new LinkedList<>();

    public ServerChatHandler(BufferedReader chatReader, PrintWriter chatWriter, Map<String, PrintWriter> onFeatureClients, User client)
    {
        super(chatReader, chatWriter, onFeatureClients);
        this.client = client;
    }

    private void showRecentChats() {
        synchronized (recentChats) {
            for (String chat : recentChats) {
                if (chat != null && !chat.trim().isEmpty()) {
                    chatWriter.println(chat);
                }
            }
            chatWriter.flush();
        }
    }

    private void saveChats(String chat) {
        synchronized (recentChats) {
            if (recentChats.size() >= MAX_RECENT_CHAT) {
                recentChats.pollFirst(); // 가장 오래된 메시지 제거
            }
            // 입장 및 퇴장 메시지는 저장하지 않음
            if (!chat.startsWith("Server")) {
                recentChats.addLast(chat); // 새로운 메시지 추가
            }
        }
    }

    public void broadcast(String chat) {
        saveChats(chat);
        Map<String, PrintWriter> onChatClients = getOnFeatureClients();

        synchronized (onChatClients) {
            for (PrintWriter clientWriter : onChatClients.values()) {
                if (chat != null && !chat.trim().isEmpty()) {
                    clientWriter.println(chat);
                    clientWriter.flush();
                }
            }
        }
    }

    @Override
    public void run() {
        Map<String, PrintWriter> onChatClients = getOnFeatureClients();
        broadcast(makeMessage("Server",client.getUserName() + "님이 입장하셨습니다."));
        synchronized (onChatClients) {
            onChatClients.put(client.getUserName(), chatWriter);
        }

        try {
            showRecentChats(); // 최근 채팅 내역 전송

            String message;
            while ((message = chatReader.readLine()) != null) {
                if (message.equalsIgnoreCase("/q")) {
                    synchronized (onChatClients) {
                        onChatClients.remove(client.getUserName());
                    }
                    broadcast(makeMessage("Server",client.getUserName() + "님이 퇴장하셨습니다."));
                    break;
                }
                broadcast(makeMessage(client.getUserName(), message)); // 일반 채팅 메시지
            }
        } catch (IOException e) {
            System.out.println("연결이 종료되었습니다.");
        }
    }

    private String makeMessage(String userName, String msg) {
        return userName + " : " + msg + " - " + Timestamp.valueOf(LocalDateTime.now());
    }




}
