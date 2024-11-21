package client;

import dto.ClientState;
import dto.Packet;
import entity.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientCore extends Thread {
    private Socket socket;
    private ObjectOutputStream serverOutput;
    private ObjectInputStream serverInput;
    private final Scanner scanner = new Scanner(System.in);
    private final PrintWriter writer = new PrintWriter(System.out, true);
    private User client;

    public ClientCore() {
        try {
            socket = new Socket("localhost", 10000);
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
            serverInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        writer.println("Connected to server");

        createClient();

        writer.println(client+ " connected");

        ClientState state = ClientState.HOME;

        while (true) {
            showMenuScreen();

            writer.print("어떤 기능을 사용하시겠습니까? : ");
            writer.flush();

            String functionNum = scanner.nextLine();

            switch (functionNum) {
                // body 데이터 타입은 Integer로 임시 설정
                case "1"-> {
                    Packet<Integer> packet = new Packet<>(SCHEDULE, 1);

                    sendPacketToServer(packet);
                }
                case "2" -> {
                    Packet<Integer> packet = new Packet<>(PLACE_SUGGESTION, 2);

                    sendPacketToServer(packet);
                }
                case "3" -> {
                    Packet<Integer> packet = new Packet<>(PLACE_VOTE, 3);

                    sendPacketToServer(packet);
                }
                case "4" -> {
                    Packet<Integer> packet = new Packet<>(STATISTIC, 4);

                    sendPacketToServer(packet);
                }
                case "5" -> {
                    Packet<Integer> packet = new Packet<>(CHATTING, 5);

                    sendPacketToServer(packet);
                }
                default -> {
                    writer.println("유효하지 않은 입력입니다. 다시 선택해주세요.");
                }
            }
        }

    }

    private void createClient() {
        writer.println("Enter name");

        String userName;
        try {
            userName = scanner.nextLine();
            serverOutput.writeObject(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client = (User) serverInput.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendPacketToServer(Packet<?> packet) {
        try {
            serverOutput.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMenuScreen() {
        writer.println("========================================");
        writer.println("(1) : 날짜 조율 기능");
        writer.println("(2) : 장소 제시 기능");
        writer.println("(3) : 장소 투표 기능");
        writer.println("(4) : 일정 확인 기능");
        writer.println("(5) : 채팅 기능");
        writer.println("========================================");
    }
}
