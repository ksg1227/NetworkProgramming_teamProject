package server.handler.host;

import dto.ClientState;
import dto.HostElectionAction;
import dto.Packet;
import entity.User;
import server.handler.normal.ServerVoteHandler;

import java.io.*;
import java.util.Map;

public class HostVoteHandler extends ServerVoteHandler {
    public HostVoteHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients, User user) {
        super(clientInput, clientOutput, onFeatureClients, user);
    }

    @Override
    public void run() {
        HostElectionAction action = null;

        try {
            Packet<HostElectionAction> packet = (Packet<HostElectionAction>) clientInput.readObject();

            assert packet.clientState().equals(ClientState.PLACE_VOTE);
            action = packet.body();
        } catch (Exception e) {}

        switch (action) {
            case START:
                startElection();
                break;
            case END:
                endElection();
                break;
            case VOTE:
                votePlace();
                break;
            case null, default:
                break;
        }
    }

    private void startElection() {
        isVoting = true;
    }

    private void endElection() {
        isVoting = false;
    }

    private void votePlace() {
        super.run();
    }
}
