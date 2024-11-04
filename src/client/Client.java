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
        boolean isHost = false;
        increaseClientOrderCounter();

        Socket sock = null;
        BufferedReader keyBoard = null;

        try {
            keyBoard = new BufferedReader(new InputStreamReader(System.in));
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

                PrintWriter pw = null;
                BufferedReader br = null;

                switch (functionNum) {
                    case "1": // 날짜 조율 기능
                        try {
                            sock = new Socket("localhost", 10001);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);

                            if (isHost) { // 호스트 클라이언트가 수행할 코드 영역 : 호스트 클라이언트는 시작일, 종료일을 설정해야함

                            }

                            if (!isHost) { // 일반 클라이언트가 수행할 코드 영역 : 일반 클라이언트는 가능한 날짜를 표시해야함

                            }


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (br != null) {
                                br.close();
                            }

                            if (pw != null) {
                                pw.close();
                            }

                            if (sock != null) {
                                sock.close();
                            }
                        }

                        break;
                    case "2": // 장소 제시 기능
                        try {
                            sock = new Socket("localhost", 10002);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (br != null) {
                                br.close();
                            }

                            if (pw != null) {
                                pw.close();
                            }

                            if (sock != null) {
                                sock.close();
                            }
                        }

                        break;
                    case "3": // 투표 기능
                        try {
                            sock = new Socket("localhost", 10003);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                            if (isHost) {  // 호스트 클라이언트가 수행할 코드 영역 : 호스트 클라이언트는 투표를 시작하는 기능도 필요

                            }

                            if (!isHost) { // 일반 클라이언트가 수행할 코드 영역 : 일반 클라이언트는 오직 투표만 수행

                            }


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (br != null) {
                                br.close();
                            }

                            if (pw != null) {
                                pw.close();
                            }

                            if (sock != null) {
                                sock.close();
                            }
                        }

                        break;
                    case "4": // 일정 확인 기능
                        try {
                            sock = new Socket("localhost", 10004);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (br != null) {
                                br.close();
                            }

                            if (pw != null) {
                                pw.close();
                            }

                            if (sock != null) {
                                sock.close();
                            }
                        }

                        break;
                    case "5": // 채팅 기능
                        try {
                            sock = new Socket("localhost", 10005);

                            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                            pw = new PrintWriter(sock.getOutputStream(), true);

                            pw.println(name);


                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (br != null) {
                                br.close();
                            }

                            if (pw != null) {
                                pw.close();
                            }

                            if (sock != null) {
                                sock.close();
                            }
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
