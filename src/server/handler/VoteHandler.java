package server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class VoteHandler extends Thread {

    Socket socket;
    Map<String, PrintWriter> onVoteClients;


    public VoteHandler(Socket socket, Map<String, PrintWriter> onVoteClients){
        this.socket = socket;
        this.onVoteClients = onVoteClients;
    }

    @Override
    public void run() {
        PrintWriter pw = null;
        BufferedReader br = null;

        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            String userName = br.readLine();

            onVoteClients.put(userName, pw);

            System.out.println(userName + "님이 일정 조율 기능을 사용하셨습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
