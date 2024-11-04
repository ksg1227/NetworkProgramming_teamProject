package server.serverThread;

import server.handler.ChatHandler;
import server.handler.ScheduleHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ScheduleServer extends Thread{

    Map<String, PrintWriter> onScheduleClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("날짜 조율 기능 연결 대기중");
        ServerSocket scheduleServerSocket = null;

        try{
            scheduleServerSocket = new ServerSocket(10001);

            while(true) {
                Socket socket = scheduleServerSocket.accept();
                System.out.println("날짜 조율 기능 연결");

                ScheduleHandler scheduleHandler = new ScheduleHandler(socket, onScheduleClients);
                scheduleHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
