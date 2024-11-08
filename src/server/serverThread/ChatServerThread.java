package server.serverThread;

import server.handler.normal.ChatHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ChatServerThread extends Thread {

    private Socket socket;
    private Map<String, PrintWriter> onChatClients;

    public ChatServerThread(Socket socket, Map<String, PrintWriter> onChatClients) {
        this.socket = socket;
        this.onChatClients = onChatClients;
    }

    // 채팅 기능을 관리하는 스레드
    @Override
    public void run() {

        BufferedReader br = null;
        PrintWriter pw = null;

        String function = null;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                function = br.readLine(); // 채팅 기능을 요청할 때까지 버퍼링을 수행하기 위해서 사용

                if (function.equals("채팅")) {
                    // 버퍼링을 활용하기 위해 스트림을 한 번 열었다보니 ChatHandler의 인자로도 socket이 아닌 BufferedReader와 PrintWriter를 전달해주었습니다.
                    ChatHandler chatHandler = new ChatHandler(br, pw, onChatClients);
                    chatHandler.start();

                    chatHandler.join();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
