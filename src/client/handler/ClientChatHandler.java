package client.handler;

import entity.Chat;
import entity.User;

import java.io.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientChatHandler extends ClientFeatureHandler{
    private BufferedReader br;
    private PrintWriter pw;
    private User client;
    public ClientChatHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User client) {
        super(serverInput, serverOutput);
        this.client = client;
        try{
            br = new BufferedReader(new InputStreamReader(serverInput));
            pw = new PrintWriter(serverOutput);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        writer.println("채팅방에 입장하였습니다.(/q를 입력하여 퇴장)");

        Thread receiverThread = new Thread(() -> {
            try{
                while(true){
                    Chat receivedChat = (Chat) serverInput.readObject();
                    writer.printf("%s : %s - %s%n",
                            receivedChat.getUserName(),
                            receivedChat.getMessage(),
                            receivedChat.getTimeStamp());
                }
            }catch (Exception e){
                writer.println("서버와 연결이 끊어졌습니다.");
            }
        });
        receiverThread.start();
        try {
            while (true) {
                // 사용자 입력
                String inputMsg = scanner.nextLine();

                // "/q" 입력 시 채팅 종료
                if (inputMsg.equalsIgnoreCase("/q")) {
                    writer.println("채팅방을 나갑니다.");
                    break;
                }
                Timestamp nowTimeStamp = Timestamp.valueOf(LocalDateTime.now());
                Chat chat = new Chat(client.getUserName(), inputMsg , nowTimeStamp);

                // 서버에 전송
                serverOutput.writeObject(chat);
                serverOutput.flush();
            }
        } catch (Exception e) {
            writer.println("채팅 도중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
