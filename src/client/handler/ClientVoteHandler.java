package client.handler;

import dto.ClientState;
import dto.HostElectionAction;
import dto.Packet;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public class ClientVoteHandler extends ClientFeatureHandler {
    private User user;
    private HashSet<String> places;
    public ClientVoteHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User user) {
        super(serverInput, serverOutput);
        this.user = user;
    }

    @Override
    public void run() {
        if(user.isHost()) {
            initHostUi();
        } else {
            initVoteUi();
        }
    }

    private boolean isVoting() {
        Boolean isVoting = false;

        try {
            Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
            isVoting = packet.body();
        }catch (Exception e) {}

        return isVoting;
    }

    private void getPlaces() {
        try {
            Packet<HashSet<String>> packet = (Packet<HashSet<String>>)serverInput.readObject();
            places = packet.body();
        } catch (Exception e) {}
    }

    private boolean hasAlreadyVoted() {
        Boolean hasAlreadyVoted = false;

        try {
            Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
            hasAlreadyVoted = packet.body();
        } catch (Exception e) {}

        return hasAlreadyVoted;
    }

    private String votePlace(String place) {
        String response = "";

        try {
            serverOutput.writeObject(place);
        } catch (IOException e) {}

        try {
            Packet<String> responsePacket = (Packet<String>)serverInput.readObject();
            response = responsePacket.body();
        } catch (Exception e) {
        }

        return response;
    }

    private void initVoteUi() {
        JFrame frame = new JFrame("election");

        boolean isVoting = isVoting();
        if(!isVoting) {
            JOptionPane.showMessageDialog(frame, "투표가 진행중이지 않습니다.");
            return;
        }

        boolean hasVoted = hasAlreadyVoted();
        if(hasVoted) {
            JOptionPane.showMessageDialog(frame, "이미 투표했습니다.");
            return;
        }

        getPlaces();

        JPanel header = new JPanel();
        JPanel body = new JPanel();
        JPanel footer = new JPanel();

        frame.setLayout(new BorderLayout());
        header.setLayout(new FlowLayout(FlowLayout.CENTER));
        body.setLayout(new BorderLayout());
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel addPlaceLabel = new JLabel("투표할 장소를 입력하세요");
        JTextField voteInputField = new JTextField(15);
        JButton voteButton = new JButton("투표하기");

        voteButton.addActionListener(e -> {
            String vote = voteInputField.getText().trim();
            if (!vote.isEmpty()) {
                String response = votePlace(vote);
                voteInputField.setText("");
                JOptionPane.showMessageDialog(frame, response);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "장소를 입력해 주세요");
            }
        });

        header.add(addPlaceLabel);
        header.add(voteInputField);
        header.add(voteButton);

        JLabel placesLabel = new JLabel("장소 목록");

        DefaultListModel<String> rawPlaceList = new DefaultListModel<>();
        if(!places.isEmpty()) {
            for (String place : places) {
                rawPlaceList.addElement(place);
            }
        }
        JList placeList = new JList(rawPlaceList);
        body.add(placesLabel, BorderLayout.NORTH);
        body.add(placeList, BorderLayout.CENTER);

        JButton exitButton = new JButton("메인메뉴로");
        exitButton.addActionListener(e -> {
            String response = votePlace("exit");
            frame.dispose();
        });

        footer.add(exitButton);

        frame.add(header, BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.add(footer, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void initHostUi() {
        JFrame frame = new JFrame("host election");
        JPanel header = new JPanel();
        JPanel body = new JPanel();
        JPanel footer = new JPanel();

        frame.setLayout(new BorderLayout());
        header.setLayout(new FlowLayout(FlowLayout.CENTER));
        body.setLayout(new FlowLayout(FlowLayout.CENTER));
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton electionStartButton = new JButton("투표 시작하기");
        electionStartButton.addActionListener(e -> {
            sendHostCommand(HostElectionAction.START);
            JOptionPane.showMessageDialog(frame, "투표가 시작됐습니다.");
            frame.dispose();
        });
        JButton voteButton = new JButton("투표하기");
        voteButton.addActionListener(e -> {
            sendHostCommand(HostElectionAction.VOTE);
            initVoteUi();
            frame.dispose();
        });
        JButton electionEndButton = new JButton("투표 종료하기");
        electionEndButton.addActionListener(e -> {
            sendHostCommand(HostElectionAction.END);
            JOptionPane.showMessageDialog(frame, "투표가 종료됐습니다.");
            frame.dispose();
        });

        body.add(electionStartButton);
        body.add(voteButton);
        body.add(electionEndButton);

        JButton exitButton = new JButton("메인메뉴로");
        exitButton.addActionListener(e -> {
            sendHostCommand(HostElectionAction.END);
            frame.dispose();
        });

        footer.add(exitButton);

        frame.add(header, BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void sendHostCommand(HostElectionAction action) {
        try {
            serverOutput.writeObject(new Packet<>(ClientState.PLACE_VOTE, action));
        } catch (IOException e) {}
    }
}
