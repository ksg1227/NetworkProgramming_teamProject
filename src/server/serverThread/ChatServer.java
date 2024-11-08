package server.serverThread;

import server.handler.normal.ChatHandler;

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
        System.out.println("채팅 기능 연결 대기중");
        ServerSocket chatServerSocket = null;

        try{
            chatServerSocket = new ServerSocket(10001);

            while(true) {
                Socket socket = chatServerSocket.accept();
                System.out.println("채팅 기능 연결");

                /*
                    각각의 기능에 대한 처리를 ChatServer에서 수행하게되면 해당 로직을 수행하는 동안 ChatServer는
                    반복문 내에서 다음 반복으로 넘어가지 못하게되기 때문에, 다음 클라이언트의 요청을 받아줄 수 없게됩니다.
                    따라서 실제 서버 기능 로직은 chatServerThread에게 위임하였습니다.
                 */
                ChatServerThread chatServerThread = new ChatServerThread(socket, onChatClients);
                chatServerThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



