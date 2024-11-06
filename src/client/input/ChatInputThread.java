package client.input;

import java.io.BufferedReader;
import java.net.Socket;

public class ChatInputThread extends Thread {
    private BufferedReader br;

    public ChatInputThread(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        try {
            String line;
            while (!Thread.currentThread().isInterrupted() && (line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception e) {
            }
        }
    }
}