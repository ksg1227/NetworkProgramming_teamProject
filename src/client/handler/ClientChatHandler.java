package client.handler;

import entity.User;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientChatHandler extends ClientFeatureHandler {
    private final User client;
    private volatile boolean running = true; // 스레드 실행 상태 플래그

    public ClientChatHandler(BufferedReader chatReader, PrintWriter chatWriter, User client) {
        super(chatReader, chatWriter);
        this.client = client;
    }

    @Override
    public void run() {
        writer.println("채팅방에 입장하였습니다. (/q를 입력하여 퇴장)");

        Thread receiverThread = createReceiverThread();

        try {
            while (true) {
                String inputMsg = scanner.nextLine();
                if (inputMsg.equalsIgnoreCase("/q")) {
                    writer.println("채팅방을 나갑니다.");
                    chatWriter.println("/q");
                    chatWriter.flush();
                    break;
                }
                chatWriter.println(inputMsg);
                chatWriter.flush();
            }
        } catch (Exception e) {
            writer.println("오류 발생: " + e.getMessage());
        } finally {
            running = false; // 실행 중단 신호
            try {
                receiverThread.join(); // receiverThread가 종료될 때까지 대기
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private Thread createReceiverThread() {
        Thread receiverThread = new Thread(() -> {
            try {
                while (running) {
                    if (chatReader.ready()) { // 데이터가 있을 때만 처리
                        String receivedMessage = chatReader.readLine();
                        if (receivedMessage != null) {
                            writer.println(receivedMessage);
                        }
                    }
                    // 잠시 대기하여 CPU 낭비 방지
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                if (running) {
                    writer.println("메시지 수신 중 오류가 발생했습니다: " + e.getMessage());
                }
            }
        });

        receiverThread.start();
        return receiverThread;
    }
}
