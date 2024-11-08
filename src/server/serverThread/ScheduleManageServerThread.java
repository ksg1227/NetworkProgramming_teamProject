package server.serverThread;

import server.handler.host.HostScheduleHandler;
import server.handler.host.HostVoteHandler;
import server.handler.normal.PlaceSuggestHandler;
import server.handler.normal.ScheduleHandler;
import server.handler.normal.StatisticHandler;
import server.handler.normal.VoteHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ScheduleManageServerThread extends Thread {

    Socket socket;
    Map<String, PrintWriter> onScheduleClients;
    Map<String, PrintWriter> onVoteClients;
    Map<String, PrintWriter> onStatisticClients;
    Map<String, PrintWriter> onPlaceSuggestClients;

    public ScheduleManageServerThread(Socket socket, Map<String, PrintWriter> onScheduleClients, Map<String, PrintWriter> onStatisticClients, Map<String, PrintWriter> onVoteClients, Map<String, PrintWriter> onPlaceSuggestClients) {
        this.socket = socket;
        this.onScheduleClients = onScheduleClients;
        this.onVoteClients = onVoteClients;
        this.onStatisticClients = onStatisticClients;
        this.onPlaceSuggestClients = onPlaceSuggestClients;
    }

    // 날짜 조율, 장소 제시, 투표, 일정 확인 기능을 관리하는 스레드
    @Override
    public void run() {

        BufferedReader br;
        PrintWriter pw;

        String function;

        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());

            while (true) {

                function = br.readLine();

                if (function.equals("날짜 조율")) { // 날짜 조율 기능을 실행한 경우 수행되는 동작
                    boolean isHost = Boolean.parseBoolean(br.readLine());

                    if (isHost) { // 호스트인 경우
                        HostScheduleHandler hostScheduleHandler = new HostScheduleHandler(br, pw, onScheduleClients);
                        hostScheduleHandler.start();

                        hostScheduleHandler.join();
                    }

                    // 호스트가 아닌 경우
                    ScheduleHandler scheduleHandler = new ScheduleHandler(br, pw, onScheduleClients);
                    scheduleHandler.start();
                    scheduleHandler.join();

                } else if (function.equals("장소 제시")) { // 장소 제시 기능을 실행한 경우 수행되는 동작

                    PlaceSuggestHandler placeSuggestHandler = new PlaceSuggestHandler(br, pw, onPlaceSuggestClients);
                    placeSuggestHandler.start();
                    placeSuggestHandler.join();

                } else if (function.equals("투표")) { // 투표 기능을 실행한 경우 수행되는 동작

                    boolean isHost = Boolean.parseBoolean(br.readLine());

                    if (isHost) {  // 호스트인 경우
                        HostVoteHandler hostVoteHandler = new HostVoteHandler(br, pw, onVoteClients);
                        hostVoteHandler.start();

                        hostVoteHandler.join();
                        continue;
                    }

                    // 호스트가 아닌 경우
                    VoteHandler voteHandler = new VoteHandler(br, pw, onVoteClients);
                    voteHandler.start();
                    voteHandler.join();

                } else if (function.equals("일정 확인")) {

                    StatisticHandler statisticHandler = new StatisticHandler(br, pw, onStatisticClients);
                    statisticHandler.start();
                    statisticHandler.join();

                } else {
                    System.err.println("올바르지 않은 기능입니다.");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
