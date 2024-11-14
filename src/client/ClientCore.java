package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientCore extends Thread {
    private Socket socket;
    private ObjectOutputStream serverOutput;
    private ObjectInputStream serverInput;
    private final Scanner scanner = new Scanner(System.in);
    private final PrintWriter writer = new PrintWriter(System.out, true);

    public ClientCore() {
        try {
            socket = new Socket("localhost", 10000);
            serverInput = new ObjectInputStream(socket.getInputStream());
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        System.out.println("\"이때 돼?\"에 접속하셨습니다.\n");

        System.out.print("이름을 입력해주세요 : ");

        String name = scanner.nextLine();

        String scheduleName = null;

        while (true) {
            // Loop
        }
    }
}
