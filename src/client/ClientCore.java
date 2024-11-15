package client;

import dto.ClientState;
import dto.Packet;
import entity.User;

import java.io.*;
import java.net.Socket;

import static dto.ClientState.*;

public class ClientCore extends Thread {
    private Socket socket;
    private ObjectOutputStream serverOutput;
    private ObjectInputStream serverInput;
    private final BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
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
        clientHostValidate();

        while (true) {

        }

    }


    private void createClient() {
        System.out.print("이름 : ");

        String userName = null;
        try {
            userName = keyBoard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client = new User(userName);
    }

    private void clientHostValidate() {
        try {
            int clientOrder = (int) serverInput.readObject();

            if (clientOrder == 1) {
                client.setHost(true);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
