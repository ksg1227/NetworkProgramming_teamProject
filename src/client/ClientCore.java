package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCore {

    public static void main(String[] args) {

        int order = getClientOrderCounter();
        boolean isHost = false;
        increaseClientOrderCounter();
        BufferedReader keyBoard = null;

        Socket scheduleManageSocket = null;
        Socket chatSocket = null;

        BufferedReader forChatBr = null;
        PrintWriter forChatPw = null;

        BufferedReader forScheduleBr = null;
        PrintWriter forSchedulePw = null;


        try {
            scheduleManageSocket = new Socket("localhost", 10000);
            chatSocket = new Socket("localhost", 10001);


            try {
                forChatBr = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
                forChatPw = new PrintWriter(chatSocket.getOutputStream(), true);
                forScheduleBr = new BufferedReader(new InputStreamReader(scheduleManageSocket.getInputStream()));
                forSchedulePw = new PrintWriter(scheduleManageSocket.getOutputStream(), true);
                keyBoard = new BufferedReader(new InputStreamReader(System.in));
            } catch (IOException e) {
                e.printStackTrace();
            }

            
            boolean isRunning = true;

            System.out.println("\"이때 돼?\"에 접속하셨습니다.\n");
            if (order == 1) {
                System.out.println("호스트 클라이언트입니다.\n");
                isHost = true;
            } else {
                System.out.println("일반 클라이언트입니다.\n");
            }

            System.out.print("이름을 입력해주세요 : ");
            String name = keyBoard.readLine();

            String scheduleName = null;

            if (isHost) {
                System.out.print("\n약속 일정의 이름을 입력해주세요 ex) 번개 모임, 건국대 컴공 종강파티  : ");
                scheduleName = keyBoard.readLine();  // 어떤 식으로 사용할 지 고려 대상
            }

            while (isRunning) {

                System.out.println();
                showMenuScreen();

                System.out.print("어떤 기능을 사용하시겠습니까? (q 를 입력할 시 프로그램을 종료합니다.) : ");
                String functionNum = keyBoard.readLine();


                switch (functionNum) {
                    case "1": // 날짜 조율 기능
                        try {
                            // ScheduleServer로 호스트 여부와 클라이언트의 이름을 전송
                            // 반드시 순서가 지켜져야합니다!
                            forSchedulePw.println("날짜 조율"); // 어떤 기능을 사용하는지에 대한 정보를 적어주어야함.
                            forSchedulePw.println(isHost);
                            forSchedulePw.println(name);

                            if (isHost) { // 호스트 클라이언트가 수행할 코드 영역 : 호스트 클라이언트는 시작일, 종료일을 설정해야함

                            }

                            if (!isHost) { // 일반 클라이언트가 수행할 코드 영역 : 일반 클라이언트는 가능한 날짜를 표시해야함

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "2": // 장소 제시 기능
                        try {

                            forSchedulePw.println("장소 제시"); // 어떤 기능을 사용하는지에 대한 정보를 적어주어야함
                            forSchedulePw.println(name);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "3": // 투표 기능
                        try {

                            // VoteServer로 호스트 여부와 클라이언트의 이름을 전송
                            // 반드시 순서가 지켜져야합니다!
                            forSchedulePw.println("투표"); // 어떤 기능을 사용하는지에 대한 정보를 적어주어야함
                            forSchedulePw.println(isHost);
                            forSchedulePw.println(name);


                            if (isHost) {  // 호스트 클라이언트가 수행할 코드 영역 : 호스트 클라이언트는 투표를 시작하는 기능도 필요

                            }

                            if (!isHost) { // 일반 클라이언트가 수행할 코드 영역 : 일반 클라이언트는 오직 투표만 수행
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case "4": // 일정 확인 기능
                        try {

                            forSchedulePw.println("일정 확인"); // 어떤 기능을 사용하는지에 대한 정보를 적어주어야함
                            forSchedulePw.println(name);


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        break;
                    case "5": // 채팅 기능
                        try {
                            System.out.println("채팅방에 입장하셨습니다.\n");

                            forChatPw.println("채팅");
                            forChatPw.println(name);

                            ChatInputThread inputThread = new ChatInputThread(forChatBr);
                            inputThread.start();

                            String input;

                            while (true) {
                                input = keyBoard.readLine();
                                forChatPw.println(input);

                                if (input.equals("/나가기")) break;
                            }

                            System.out.println("채팅방을 나갑니다.");


                            inputThread.stopThread();

                            // inputThread가 종료되면 다음 동작 실행하도록 join 걸어줌
                            inputThread.join();


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        break;
                    case "q":
                        isRunning = false;
                        break;
                    default:
                        System.out.println("잘못된 입력입니다. 입력을 다시 확인해주세요");
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (forChatPw != null) forChatPw.close();
                if (forChatBr != null) forChatBr.close();
                if (forSchedulePw != null) forSchedulePw.close();
                if (forScheduleBr != null) forScheduleBr.close();
                if (scheduleManageSocket != null) scheduleManageSocket.close();
                if (chatSocket != null) chatSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    static void showMenuScreen() {
        System.out.println("====================기능 메뉴====================");
        System.out.println("(1) : 날짜 조율 기능");
        System.out.println("(2) : 장소 제시 기능");
        System.out.println("(3) : 장소 투표 기능");
        System.out.println("(4) : 일정 확인 기능");
        System.out.println("(5) : 채팅 기능");
        System.out.println("===============================================");
    }
}
