package server.handler.normal;

import java.awt.image.DataBufferDouble;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

public class ChatHandler extends Thread {

    Socket socket;
    private PrintWriter pw;
    private BufferedReader br;
    Map<String, PrintWriter> onChatClients;

    String userName;

    public ChatHandler(Socket socket, Map<String, PrintWriter> onChatClients) {
        this.socket = socket;
        this.onChatClients = onChatClients;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);

            userName = br.readLine();

            synchronized (onChatClients) {
                onChatClients.put(userName, pw);
            }

            broadcast(userName + "님이 채팅 기능에 접속하셨습니다.");
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("/quit"))
                    break;
                else
                    broadcast(userName + " : " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {

            synchronized (onChatClients) {
                onChatClients.remove(userName);
            }

            broadcast(userName + "님이 채팅방을 나가셨습니다.");

            try {
                if (socket != null) socket.close();
            } catch (Exception e) {

            }
        }
    }


    public void broadcast(String message) {

        synchronized (onChatClients) {
            Collection<PrintWriter> collection = onChatClients.values();
            for (PrintWriter pw : collection) {
                pw.println(message);
                pw.flush();
            }
        }
    }

}
