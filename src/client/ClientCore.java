package client;

import client.handler.ClientChatHandler;
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
        writer.println("Connected to server\n");

        createClient();

        writer.println(client+ " connected");

        ClientState state = ClientState.HOME;
        while (true) {
            writer.println("Select feature");
            writer.println("[1]. Enter chat");
            writer.println("[2]. Enter schedule");
            writer.println("[3]. Suggest place");
            writer.println("[4]. Vote place");
            writer.println("[5]. Show statistic");

            state = setState();
            try{
                notifyState(state);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            switch (state) {
                case HOME -> {
                    writer.println("home");
                }
                case CHATTING -> {
                    new ClientChatHandler(serverInput,serverOutput,client).run();
                }
                case SCHEDULE -> {
                    writer.println("schedule");
                }
                case STATISTIC -> {
                    writer.println("statistic");
                }
                case PLACE_VOTE -> {
                    writer.println("place vote");
                }
                case PLACE_SUGGESTION -> {
                    writer.println("place suggestion");
                }
                case null, default -> {
                    writer.println("unknown");
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
    private void notifyState(ClientState state) throws IOException {
        Packet<Integer> packet = new Packet<>(state, 0);
        serverOutput.writeObject(packet);
        serverOutput.flush();
    }
    private ClientState setState() {
        String input = scanner.nextLine();

        switch (input) {
            case "chat", "1" -> {
                return ClientState.CHATTING;
            }
            case "schedule", "2" -> {
                return ClientState.SCHEDULE;
            }
            case "place-suggest", "3" -> {
                return ClientState.PLACE_SUGGESTION;
            }
            case "place-vote", "4" -> {
                return ClientState.PLACE_VOTE;
            }
            case "statistic", "5" -> {
                return ClientState.STATISTIC;
            }
            case null, default ->  {
                return ClientState.HOME;
            }
        }
    }
}
