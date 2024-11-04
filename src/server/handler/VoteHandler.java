package server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Map;

public class VoteHandler extends Thread {

    BufferedReader br;
    PrintWriter pw;
    Map<String, PrintWriter> onVoteClients;


    public VoteHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onVoteClients) {
        this.br = br;
        this.pw = pw;
        this.onVoteClients = onVoteClients;
    }

    @Override
    public void run() {

        try{
            String userName = br.readLine();

            onVoteClients.put(userName, pw);

            System.out.println(userName + "님이 투표 기능을 사용하셨습니다.");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                if (br != null) br.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (pw != null) pw.close();
        }


    }
}
