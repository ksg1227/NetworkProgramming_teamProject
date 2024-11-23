package server.handler.normal;

import dto.ClientState;
import dto.Packet;
import entity.Chat;
import entity.User;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class ServerChatHandler extends ServerFeatureHandler {
    protected final User client;
    private PrintWriter pw;
    private BufferedReader br;
    public ServerChatHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients,User client) {
        super(clientInput, clientOutput, onFeatureClients);
        this.client = client;
        try{
            br = new BufferedReader(new InputStreamReader(clientInput));
            pw = new PrintWriter(new OutputStreamWriter(clientOutput));

        } catch (Exception e){}
    }

    public void broadcast(Chat chat) {
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



    @Override
    public void run() {
        try {
            while (true) {
                // 클라이언트로부터 Chat 객체 수신
                Chat receivedChat = (Chat) clientInput.readObject();

                if (receivedChat.getMessage().equalsIgnoreCase("/q")) {
                    // 클라이언트가 채팅방을 나갔을 경우 처리
                    System.out.println(client.getUserName() + " has left the chat.");
                    break;
                }


                broadcast(receivedChat);
            }
        } catch (Exception e) {
            System.out.println("Connection lost with: " + client.getUserName());
            e.printStackTrace();
        } finally {
            // 연결 종료 시 클라이언트 제거
            synchronized (getOnFeatureClients()) {
                getOnFeatureClients().remove(client.getUserName());
            }
            System.out.println(client.getUserName() + " has been removed from the chat.");
        }
    }

}
