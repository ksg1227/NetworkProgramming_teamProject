package server.serverThread;

import server.handler.ChatHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer extends Thread {

    Map<String, PrintWriter> onChatClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("일정 조율 기능 연결 대기중");
        ServerSocket chatServerSocket = null;

        try{

            chatServerSocket = new ServerSocket(10005);

            while(true) {
                Socket socket = chatServerSocket.accept();
                System.out.println("채팅 기능 연결");

                ChatHandler chatHandler = new ChatHandler(socket, onChatClients);
                chatHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
