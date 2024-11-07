package server.handler.normal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class PlaceSuggestHandler extends Thread {

    BufferedReader br;
    PrintWriter pw;
    Map<String, PrintWriter> onPlaceSuggestClients;


    public PlaceSuggestHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onPlaceSuggestClients) {
        this.br = br;
        this.pw = pw;
        this.onPlaceSuggestClients = onPlaceSuggestClients;
    }

    @Override
    public void run() {

        try {
            String userName = br.readLine();

            synchronized (onPlaceSuggestClients) {
                onPlaceSuggestClients.put(userName, pw);
            }

            System.out.println(userName + "님이 장소 제시 기능을 사용하셨습니다.");
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
