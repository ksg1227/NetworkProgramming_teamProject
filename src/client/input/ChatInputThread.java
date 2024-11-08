package client.input;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatInputThread extends Thread {
    private BufferedReader br;
    private volatile boolean running = true; // 종료 플래그

    public ChatInputThread(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // 입력이 준비된 경우에만 readLine 호출
                if (br.ready()) {
                    String line = br.readLine();
                    if (line != null) {
                        System.out.println(line);
                    }
                } else {
                    // 입력이 없으면 잠시 대기
                    Thread.sleep(100); // 100ms 정도 딜레이를 줌
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태를 다시 설정
        }
    }

    public void stopThread() {
        running = false;
    }
}
