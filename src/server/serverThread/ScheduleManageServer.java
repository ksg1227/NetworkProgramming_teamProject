package server.serverThread;

import java.io.IOException;
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
        System.out.println("날짜 조율, 장소 제시, 투표 시작, 일정 확인용 서버 연결 대기중");
        ServerSocket scheduleManageServerSocket;

        try {
            scheduleManageServerSocket = new ServerSocket(10000);

            while (true) {
                Socket socket = scheduleManageServerSocket.accept(); // 각각의 클라이언트가 요청을 보낼 때 까지 대기
                System.out.println("날짜 조율, 장소 제시, 투표 시작, 일정 확인용 서버 연결");

                /*
                    각각의 기능에 대한 처리를 ScheduleManageServer에서 수행하게되면 해당 로직을 수행하는 동안 ScheduleManageServer는
                    반복문 내에서 다음 반복으로 넘어가지 못하게되기 때문에 다음 클라이언트의 요청을 받아줄 수 없게됩니다.
                    따라서 실제 서버 기능 로직은 scheduleManageThread에게 위임하였습니다.
                 */
                ScheduleManageServerThread scheduleManageThread = new ScheduleManageServerThread(socket, onScheduleClients, onStatisticClients, onVoteClients, onPlaceSuggestClients);
                scheduleManageThread.start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
