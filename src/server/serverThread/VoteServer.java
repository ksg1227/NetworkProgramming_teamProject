package server.serverThread;

import server.handler.host.HostVoteHandler;
import server.handler.normal.VoteHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class VoteServer extends Thread {

    Map<String, PrintWriter> onVoteClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("투표 기능 연결 대기중");
        ServerSocket voteServerSocket = null;

        try {
            voteServerSocket = new ServerSocket(10003);

            while (true) {
                Socket socket = voteServerSocket.accept();
                System.out.println("투표 기능 연결");

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                boolean isHost = Boolean.parseBoolean(br.readLine());

                if (isHost) {
//                    String hostName = br.readLine();
//                    System.out.println("호스트 : " + hostName + "가 투표 기능에...?");
                    HostVoteHandler hostVoteHandler = new HostVoteHandler(br, pw, onVoteClients);
                    hostVoteHandler.start();
                    continue;
                }

                /*
                    VoteServer에서 스트림을 연 후에 호스트 여부를 확인하고, VoteHandler의 인자로 socket을 전달한 후,
                    Vote Handler에서 socket으로부터 스트림을 열어 사용자의 이름을 읽어오려고 했습니다.
                    근데 VoteHandler 내부에서 socket을 다시 열게 되니 스트림 안에 써준 내용들이 날라가서
                    이름이 계속 null 값을 읽어오더라구요. 그래서 인자로 br, pw 를 직접 넘겨줬습니다.

                    VoteHandler(socket, onVoteClients) -> X

                    VoteHandler(br, pw, onVoteClients) -> O
                */
                VoteHandler voteHandler = new VoteHandler(br, pw, onVoteClients);
                voteHandler.start();

            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }
}
