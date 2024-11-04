package server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ChatHandler extends Thread {

    Socket socket;
    Map<String, PrintWriter> onChatClients;


    public ChatHandler(Socket socket, Map<String, PrintWriter> onChatClients){
        this.socket = socket;
        this.onChatClients = onChatClients;
    }

    @Override
    public void run() {
        PrintWriter pw = null;
        BufferedReader br = null;

        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            String userName = br.readLine();

            onChatClients.put(userName, pw);

            System.out.println(userName + "님이 일정 조율 기능을 사용하셨습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
