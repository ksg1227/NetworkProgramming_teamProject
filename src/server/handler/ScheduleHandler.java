package server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ScheduleHandler extends Thread {

    BufferedReader br;
    PrintWriter pw;
    Map<String, PrintWriter> onScheduleClients;


    public ScheduleHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onScheduleClients){
        this.br = br;
        this.pw = pw;
        this.onScheduleClients = onScheduleClients;
    }

    @Override
    public void run() {

        try{
            String userName = br.readLine();

            onScheduleClients.put(userName, pw);

            System.out.println(userName + "님이 날짜 조율 기능을 사용하셨습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(br != null) br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if(pw != null) pw.close();
        }
    }
}
