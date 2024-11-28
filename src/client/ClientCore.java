package client;

import client.handler.ClientChatHandler;
import client.handler.ClientPlaceSuggestHandler;
import client.handler.ClientVoteHandler;
import dto.ClientState;
import dto.Packet;
import entity.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static dto.ClientState.*;

public class ClientCore extends Thread {
    private ObjectOutputStream serverOutput;
    private ObjectInputStream serverInput;
    private final Scanner scanner = new Scanner(System.in);
    private final PrintWriter writer = new PrintWriter(System.out, true);
    private User client;

    public ClientCore() {
        try {
            Socket socket = new Socket("localhost", 10000);
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
            serverOutput.flush();
            serverInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        writer.println("Connected to server");
        createClient();

        writer.println(client+ " connected");
        while (true) {
            ClientState state = HOME;
            writer.println("Select feature");
            writer.println("[1]. Enter chat");
            writer.println("[2]. Enter schedule");
            writer.println("[3]. Suggest place");
            writer.println("[4]. Vote place");
            writer.println("[5]. Show statistic");

            state = setState();

            try {
                notifyState(state);
            } catch (IOException e) {
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
                    new ClientVoteHandler(serverInput, serverOutput, client).run();
                }
                case PLACE_SUGGESTION -> {
                    new ClientPlaceSuggestHandler(serverInput, serverOutput).run();
                }
                case null, default -> {
                    writer.println("unknown");
                }
            }
        }
    }

    private void notifyState(ClientState state) throws IOException {
        Packet<Integer> packet = new Packet<>(state, 0);
        serverOutput.writeObject(packet);
        serverOutput.flush();
    }

    private void createClient() {
        writer.println("Enter name");

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

    private ClientState setState() {
        String input = scanner.nextLine();

        switch (input) {
            case "chat", "1" -> {
                return CHATTING;
            }
            case "schedule", "2" -> {
                return SCHEDULE;
            }
            case "place-suggest", "3" -> {
                return PLACE_SUGGESTION;
            }
            case "place-vote", "4" -> {
                return PLACE_VOTE;
            }
            case "statistic", "5" -> {
                return STATISTIC;
            }
            case null, default ->  {
                return HOME;
            }
        }
    }

}
