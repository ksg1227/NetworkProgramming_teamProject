package client.handler;

import entity.Chat;
import entity.User;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ClientChatHandler extends ClientFeatureHandler {
    private final User client;

    public ClientChatHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User client) {
        super(serverInput, serverOutput);
        this.client = client;
    }

    @Override
    public void run() {
        writer.println("채팅방에 입장하였습니다. (/q를 입력하여 퇴장)");

        Thread receiverThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Object receivedObject = serverInput.readObject();

                    if (receivedObject instanceof Chat receivedChat) {
                        writer.printf("%s : %s - %s%n",
                                receivedChat.getUserName(),
                                receivedChat.getMessage(),
                                receivedChat.getTimeStamp());
                    } else {
                        writer.println("알 수 없는 데이터 수신: " + receivedObject);
                    }
                }
            } catch (EOFException e) {
                writer.println("서버와의 연결이 종료되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                writer.println("서버와 연결이 끊어졌습니다.");
            }
        });

        receiverThread.start();

        try {
            while (true) {
                String inputMsg = scanner.nextLine();
                if (inputMsg.equalsIgnoreCase("/q")) {
                    writer.println("채팅방을 나갑니다.");
                    Chat disconnectMessage = new Chat(client.getUserName(), "/q", Timestamp.valueOf(LocalDateTime.now()));
                    serverOutput.writeObject(disconnectMessage);
                    serverOutput.flush();
                    break;
                }
                Timestamp nowTimeStamp = Timestamp.valueOf(LocalDateTime.now());
                Chat chat = new Chat(client.getUserName(), inputMsg, nowTimeStamp);

                serverOutput.writeObject(chat);
                serverOutput.flush();
            }
        } catch (IOException e) {
            writer.println("채팅 도중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            receiverThread.interrupt();
        }
    }
}
