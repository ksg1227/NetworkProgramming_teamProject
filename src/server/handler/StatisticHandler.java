package server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class StatisticHandler extends Thread {

    Socket socket;
    Map<String, PrintWriter> onStatisticClients;


    public StatisticHandler(Socket socket, Map<String, PrintWriter> onStatisticClients){
        this.socket = socket;
        this.onStatisticClients = onStatisticClients;
    }

    @Override
    public void run() {
        PrintWriter pw = null;
        BufferedReader br = null;

        try{
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            String userName = br.readLine();

            onStatisticClients.put(userName, pw);

            System.out.println(userName + "님이 일정 확인 기능을 사용하셨습니다.");
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
