package server.serverThread;

import server.handler.ChatHandler;
import server.handler.VoteHandler;

import java.io.IOException;
import java.io.PrintWriter;
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

        try{
            voteServerSocket = new ServerSocket(10003);

            while(true) {
                Socket socket = voteServerSocket.accept();
                System.out.println("투표 기능 연결");

                VoteHandler voteHandler = new VoteHandler(socket, onVoteClients);
                voteHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
