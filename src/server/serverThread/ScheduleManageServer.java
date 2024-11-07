package server.serverThread;

import server.handler.host.HostScheduleHandler;
import server.handler.host.HostVoteHandler;
import server.handler.normal.PlaceSuggestHandler;
import server.handler.normal.ScheduleHandler;
import server.handler.normal.StatisticHandler;
import server.handler.normal.VoteHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ScheduleManageServer extends Thread {

    // 굳이 Map 안써도 되긴 합니다.
    Map<String, PrintWriter> onScheduleClients = new HashMap<>();
    Map<String, PrintWriter> onStatisticClients = new HashMap<>();
    Map<String, PrintWriter> onVoteClients = new HashMap<>();
    Map<String, PrintWriter> onPlaceSuggestClients = new HashMap<>();

    @Override
    public void run() {
        System.out.println("날짜 조율, 장소 제시, 투표 시작, 일정 확인용 서버 연결");
        ServerSocket scheduleManageServerSocket;

        try {
            scheduleManageServerSocket = new ServerSocket(10000);

            while (true) {
                Socket socket = scheduleManageServerSocket.accept(); // 각각의 클라이언트가 요청을 보낼 때 까지 대기

                // 각 클라이언트는 자신이 어떤 기능을 사용할지 소켓을 통해 전달하므로
                // 그 정보를 읽어올 BufferedReader가 필요
                BufferedReader br = null;
                PrintWriter pw = null;

                String function = null;

                try {
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    pw = new PrintWriter(socket.getOutputStream());

                    function = br.readLine(); // 어떤 기능인지를 읽어옴
                } catch (IOException e) {
                    e.printStackTrace();
                }


                /*
                    어떤 기능인지 확인하기 위해 inputStream, OutputStream을 미리 열다보니
                    각각의 handler로 소켓을 넘겨서 추후에 handler 내부에서 소켓으로부터 스트림을 다시 열게되면
                    어떤 기능인지에 대한 정보를 제외한, 사용자 이름과 같은 데이터가 handler 남아있지 않고 소멸하는 문제가 존재했습니다.
                    따라서 각각의 handler의 인자로 socket이 아닌 스트림 자체를 전달해주었으니 이 점 유의바랍니다.
                 */

                if (function.equals("날짜 조율")) { // 날짜 조율 기능을 실행한 경우 수행되는 동작
                    boolean isHost = Boolean.parseBoolean(br.readLine());

                    if(isHost) { // 호스트인 경우
                        HostScheduleHandler hostScheduleHandler = new HostScheduleHandler(br, pw, onScheduleClients);
                        hostScheduleHandler.start();
                        continue;
                    }

                    // 호스트가 아닌 경우
                    ScheduleHandler scheduleHandler = new ScheduleHandler(br, pw, onScheduleClients);
                    scheduleHandler.start();

                } else if(function.equals("장소 제시")){ // 장소 제시 기능을 실행한 경우 수행되는 동작

                    PlaceSuggestHandler placeSuggestHandler = new PlaceSuggestHandler(br, pw, onPlaceSuggestClients);
                    placeSuggestHandler.start();

                } else if(function.equals("투표")){ // 투표 기능을 실행한 경우 수행되는 동작

                    boolean isHost = Boolean.parseBoolean(br.readLine());

                    if (isHost) {  // 호스트인 경우
                        HostVoteHandler hostVoteHandler = new HostVoteHandler(br, pw, onVoteClients);
                        hostVoteHandler.start();
                        continue;
                    }

                    // 호스트가 아닌 경우
                    VoteHandler voteHandler = new VoteHandler(br, pw, onVoteClients);
                    voteHandler.start();

                } else if(function.equals("일정 확인")){

                    StatisticHandler statisticHandler = new StatisticHandler(br, pw, onStatisticClients);
                    statisticHandler.start();

                } else{
                    System.err.println("올바르지 않은 기능입니다.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
