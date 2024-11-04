package server.serverThread;

import server.handler.normal.PlaceSuggestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PlaceSuggestServer extends Thread{

    Map<String, PrintWriter> onPlaceSuggestClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("장소 제시 기능 연결 대기중");
        ServerSocket chatServerSocket = null;

        try{
            chatServerSocket = new ServerSocket(10002);

            while(true) {
                Socket socket = chatServerSocket.accept();
                System.out.println("장소 제시 기능 연결");

                PlaceSuggestHandler placeSuggestHandler = new PlaceSuggestHandler(socket, onPlaceSuggestClients);
                placeSuggestHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
