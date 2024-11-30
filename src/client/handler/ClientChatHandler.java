package client.handler;

import entity.User;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientChatHandler extends ClientFeatureHandler {
    private final User client;

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
        }catch (Exception e){
            writer.println(e.getMessage());
        } finally {
            receiverThread.interrupt();
        }
    }

    private Thread createReceiverThread() {
        Thread receiverThread = new Thread(() -> {
            try {
                String receivedMessage;
                while (!Thread.currentThread().isInterrupted() && (receivedMessage = chatReader.readLine()) != null) {
                    writer.println(receivedMessage);
                }
            } catch (Exception e) {
                writer.println("메시지 수신 중 오류가 발생했습니다: " + e.getMessage());
            }
        });

        receiverThread.start();
        return receiverThread;
    }

}
