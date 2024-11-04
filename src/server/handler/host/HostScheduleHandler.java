package server.handler.host;

import server.handler.normal.ScheduleHandler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;

public class HostScheduleHandler extends ScheduleHandler {

    BufferedReader br;
    PrintWriter pw;
    Map<String, PrintWriter> onVoteClients;

    public HostScheduleHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onScheduleClients) {
        super(br, pw, onScheduleClients);
    }

    @Override
    public void run() {
        System.out.println("호스트입니다.");
        super.run();
    }
}
