package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static client.ClientOrderGenerator.*;

public class Client {

    public static void main(String[] args) {

        int order = getClientOrderCounter();
        increaseClientOrderCounter();

        Socket sock = null;
        BufferedReader keyBoard = null;

        try {
            keyBoard = new BufferedReader(new InputStreamReader(System.in));
            boolean isRunning = true;

            System.out.println("\"이때 돼?\"에 접속하셨습니다.\n");
            if (order == 1) {
                System.out.println("호스트 클라이언트입니다.\n");
            } else {
                System.out.println("일반 클라이언트입니다.\n");
            }

            System.out.print("이름을 입력해주세요 : ");
            String name = keyBoard.readLine();

            while (isRunning) {

                System.out.println();
                showMenuScreen();

                System.out.print("어떤 기능을 사용하시겠습니까? (q 를 입력할 시 프로그램을 종료합니다.) : ");
                String functionNum = keyBoard.readLine();

                PrintWriter pw = null;
                BufferedReader br = null;

                switch (functionNum) {
                    case "1":
                        try{
                            sock = new Socket("localhost", 10001);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            sock.close();
                        }

                        break;
                    case "2":
                        try {
                            sock = new Socket("localhost", 10002);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            sock.close();
                        }

                        break;
                    case "3":
                        try{
                            sock = new Socket("localhost", 10003);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            sock.close();
                        }

                        break;
                    case "4":
                        try{
                            sock = new Socket("localhost", 10004);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            sock.close();
                        }

                        break;
                    case "5":
                        try{
                            sock = new Socket("localhost", 10005);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            sock.close();
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
