package server.handler.host;

import server.handler.normal.VoteHandler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;

public class HostVoteHandler extends VoteHandler {

    BufferedReader br;
    PrintWriter pw;
    Map<String, PrintWriter> onVoteClients;

    public HostVoteHandler(BufferedReader br, PrintWriter pw, Map<String, PrintWriter> onVoteClients) {
        super(br, pw, onVoteClients);
    }

    @Override
    public void run() {
        System.out.println("호스트입니다.");
        super.run();
    }
}
