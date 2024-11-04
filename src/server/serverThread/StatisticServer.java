package server.serverThread;

import server.handler.ChatHandler;
import server.handler.StatisticHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class StatisticServer extends Thread {

    Map<String, PrintWriter> onStatisticClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("일정 확인 기능 연결 대기중");
        ServerSocket statisticServerSocket = null;

        try{
            statisticServerSocket = new ServerSocket(10004);

            while(true) {
                Socket socket = statisticServerSocket.accept();
                System.out.println("일정 확인 기능 연결");

                StatisticHandler statisticHandler = new StatisticHandler(socket, onStatisticClients);
                statisticHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
