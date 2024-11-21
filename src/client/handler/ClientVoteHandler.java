package client.handler;

import dto.ClientState;
import dto.HostVoteAction;
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
                serverOutput.writeObject(new Packet<HostVoteAction>(ClientState.PLACE_VOTE, HostVoteAction.START));
                break;
            case "2":
                serverOutput.writeObject(new Packet<HostVoteAction>(ClientState.PLACE_VOTE, HostVoteAction.END));
                break;
            case "3":
                serverOutput.writeObject(new Packet<HostVoteAction>(ClientState.PLACE_VOTE, HostVoteAction.VOTE));
                vote();
            case null, default:
                break;
        }
    }

    private void vote()  throws IOException, ClassNotFoundException {
        // 1. 투표가 진행중인지 확인
        Boolean isVoting = false;
        Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
        isVoting = packet.body();

        if(!isVoting) {
            writer.println("Vote is currently unavailable");
            return;
        }

        // 2. 장소 목록 공유
        HashSet<String> places = (HashSet<String>) serverInput.readObject();

        writer.println("Vote the place you want to go");

        for(String place : places){
            writer.println(place);
        }

        // 3. 투표 결과 전송
        String input = scanner.nextLine();
        serverOutput.writeObject(input);

        // 4. 투표 결과 처리
        Packet<String> response = (Packet<String>)serverInput.readObject();
        writer.println(response.body());
    }
}
