package server.core;

import server.serverThread.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerCore {

    public static void main(String[] args) {

        ScheduleManageServer scheduleManageServer = new ScheduleManageServer();
        ChatServer chatServer = new ChatServer();

        scheduleManageServer.start();
        chatServer.start();
    }
}
