package server.handler.normal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class PlaceSuggestHandler extends Thread {

    Socket socket;
    Map<String, PrintWriter> onPlaceSuggestClients;


    public PlaceSuggestHandler(Socket socket, Map<String, PrintWriter> onPlaceSuggestClients) {
        this.socket = socket;
        this.onPlaceSuggestClients = onPlaceSuggestClients;
    }

    @Override
    public void run() {
        PrintWriter pw = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

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
