package client.handler;

import dto.ClientState;
import dto.HostElectionAction;
import dto.Packet;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class ClientVoteHandler extends ClientFeatureHandler {
    private User user;
    public ClientVoteHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User user) {
        super(serverInput, serverOutput);
        this.user = user;
    }

    @Override
    public void run() {
        try {
            if(user.isHost()) {
                handleHost();
            } else {
                vote();
            }
        } catch (Exception e) {}
    }

    private void handleHost() throws IOException, ClassNotFoundException {
        writer.println("Select the menu you want to enter");
        writer.println("[1]. Start election");
        writer.println("[2]. End election");
        writer.println("[3]. Vote");
        writer.println("type [exit] to exit");

        String input = scanner.nextLine();
        switch (input) {
            case "exit":
                return;
            case "1":
                serverOutput.writeObject(new Packet<HostElectionAction>(ClientState.PLACE_VOTE, HostElectionAction.START));
                break;
            case "2":
                serverOutput.writeObject(new Packet<HostElectionAction>(ClientState.PLACE_VOTE, HostElectionAction.END));
                break;
            case "3":
                serverOutput.writeObject(new Packet<HostElectionAction>(ClientState.PLACE_VOTE, HostElectionAction.VOTE));
                vote();
            case null, default:
                break;
        }
    }

    private void vote()  throws IOException, ClassNotFoundException {
        // 1. 투표가 진행중인지 확인
        if(!isVoting()) {
            writer.println("Vote had not started");
            return;
        }

        // 2. 장소 목록 출력
        showPlaces();

        // 3. 투표 결과 전송
        String input = scanner.nextLine();
        serverOutput.writeObject(input);

        // 4. 투표 결과 처리
        Packet<String> response = (Packet<String>)serverInput.readObject();
        writer.println(response.body());
    }

    private boolean isVoting() throws IOException, ClassNotFoundException {
        Boolean isVoting = false;

        Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
        isVoting = packet.body();

        return isVoting;
    }

    private void showPlaces() throws IOException, ClassNotFoundException {
        Packet<HashSet<String>> packet = (Packet<HashSet<String>>)serverInput.readObject();
        HashSet<String> places = packet.body();

        writer.println("Vote the place you want to go");

        for(String place : places){
            writer.println(place);
        }
    }
}
