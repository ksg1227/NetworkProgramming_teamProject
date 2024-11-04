package server.core;

import server.serverThread.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerCore {

    public static void main(String[] args) {


        ScheduleServer scheduleServer = new ScheduleServer();
        PlaceSuggestServer placeSuggestServer = new PlaceSuggestServer();
        VoteServer voteServer = new VoteServer();
        ChatServer chatServer = new ChatServer();
        StatisticServer statisticServer = new StatisticServer();

        scheduleServer.start();
        placeSuggestServer.start();
        voteServer.start();
        chatServer.start();
        statisticServer.start();
    }
}
