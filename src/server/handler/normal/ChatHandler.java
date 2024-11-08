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

    private PrintWriter pw;
    private BufferedReader br;
    Map<String, PrintWriter> onChatClients;

    String userName;

    public ChatHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onChatClients) {
        this.br = br;
        this.pw = pw;
        this.onChatClients = onChatClients;

        try {
            userName = br.readLine(); // 객체를 생성하면서 inputStream으로부터 사용자 이름을 받아옴

            synchronized (this.onChatClients) {
                this.onChatClients.put(userName, pw); // 현재 채팅 기능을 사용중인 사용자를 추적하는 맵에 추가
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
                if (line.equals("/나가기"))
                    break;
                else
                    broadcast(userName + " : " + line);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {

            synchronized (onChatClients) {
                onChatClients.remove(userName); // /나가기 를 입력할 경우 map에서 해당 사용자 삭제
            }

            broadcast(userName + "님이 채팅방을 나가셨습니다.");
            // 채팅방에서 나간 사용자를 제외한 나머지 사용자들 중 채팅 기능을 이용하는 사람들에게만 broadcast
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
