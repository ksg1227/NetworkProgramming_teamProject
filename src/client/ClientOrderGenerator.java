package client;

import java.io.*;

public class ClientOrderGenerator {

    private static final String COUNTER_FILE = "clientOrderCounter.txt";

    // 클라이언트 코드가 하나의 프로세스를 굴리는 거다보니 static 변수를 활용하는 클래스를 별도로 만들어도 공유가 안되더라구요
    // 그래서 외부 파일에 클라이언트의 순서를 저장해줄 수 있게 했습니다.
    // 일단 구현만 가능하게 야매 방식을 사용한 느낌이라 추후에 더 나은 방향이 있다면 바꾸는 게 좋을 것 같다는 생각은 드네요

    public static int getClientOrderCounter() {
        int counter = 1;
        File file = new File(COUNTER_FILE);

        // 파일에서 카운터 값을 읽어옴
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                counter = Integer.parseInt(reader.readLine());
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return counter;
    }

    public static void increaseClientOrderCounter() {
        int counter = getClientOrderCounter();
        counter++;

        // 증가된 카운터 값을 파일에 저장
        try (PrintWriter writer = new PrintWriter(new FileWriter(COUNTER_FILE))) {
            writer.println(counter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
