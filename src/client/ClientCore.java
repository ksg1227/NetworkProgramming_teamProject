package client;

import dto.Packet;
import entity.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static dto.ClientState.*;

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
        System.out.println("Connected to server\n");

        createClient();

        while (true) {
            showMenuScreen();

            System.out.print("어떤 기능을 사용하시겠습니까? : ");
            String functionNum = scanner.nextLine();

            switch (functionNum) {
                // body 데이터 타입은 Integer로 임시 설정
                case "1"-> {
                    Packet<Integer> packet = new Packet<>(SCHEDULE, null);

                    sendPacketToServer(packet);

                    // 첫번째 접속이 아니라면
                    if (!isFirstAccess()) {
                        System.out.println("이미 가능한 날짜에 표시하셨습니다.");
                        continue;
                    }

                    // 처음으로 기능을 사용하는 경우
                    // TODO : 처음 기능에 접속한 경우 실행할 로직


                }
                case "2" -> {
                    Packet<Integer> packet = new Packet<>(PLACE_SUGGESTION, 1);

                    sendPacketToServer(packet);
                }
                case "3" -> {
                    Packet<Integer> packet = new Packet<>(PLACE_VOTE, null);

                    sendPacketToServer(packet);

                    // 이미 기능을 사용한 경우
                    if (!isFirstAccess()) {
                        System.out.println("이미 장소 투표에 참여하셨습니다.");
                        continue;
                    }

                    // 처음으로 기능을 사용하는 경우
                    // TODO : 처음으로 기능을 사용하는 경우 실행할 로직


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
                    System.out.println("유효하지 않은 입력입니다. 다시 선택해주세요.");
                }
            }
        }

    }

    private void createClient() {
        System.out.print("이름 : ");

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

    private boolean isFirstAccess() {
        try {
            return (Boolean) serverInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("서버 응답 처리 중 오류 발생", e);
        }
    }

    static void showMenuScreen() {
        System.out.println("========================================");
        System.out.println("(1) : 날짜 조율 기능");
        System.out.println("(2) : 장소 제시 기능");
        System.out.println("(3) : 장소 투표 기능");
        System.out.println("(4) : 일정 확인 기능");
        System.out.println("(5) : 채팅 기능");
        System.out.println("========================================");
    }
}
