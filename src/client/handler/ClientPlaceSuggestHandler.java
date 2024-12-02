package client.handler;

import dto.ClientState;
import dto.Packet;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientPlaceSuggestHandler extends ClientFeatureHandler {
    private HashSet<String> places = null;

    public ClientPlaceSuggestHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {
        try {
            Packet<HashSet<String>> packet = (Packet<HashSet<String>>) serverInput.readObject();
            places = packet.body();
            for (String place : places) {
                writer.println(place);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        initUi();
    }

    private String addPlace(String place) {
        Packet<String> packet = createPacket(place);
        String response = null;

        try {
            sendRequest(packet);
            response = getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Packet<String> createPacket(String place) {
        return new Packet<String>(ClientState.PLACE_SUGGESTION, place);
    }

    private void sendRequest(Packet<String> packet) throws IOException {
        serverOutput.writeObject(packet);
        serverOutput.flush();
    }

    private String getResponse() throws IOException, ClassNotFoundException {
        Packet<String> response = (Packet<String>)serverInput.readObject();
        return response.body();
    }

    private void initUi() {
        JFrame frame = new JFrame();
        JPanel header = new JPanel();
        JPanel body = new JPanel();
        JPanel footer = new JPanel();

        frame.setLayout(new BorderLayout());
        header.setLayout(new FlowLayout(FlowLayout.CENTER));
        body.setLayout(new BorderLayout());
        footer.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel addPlaceLabel = new JLabel("추가할 장소를 입력하세요");
        JTextField placeInputField = new JTextField(15);
        JButton addPlaceButton = new JButton("추가하기");

        addPlaceButton.addActionListener(e -> {
            String newPlace = placeInputField.getText().trim();
            if (!newPlace.isEmpty()) {
                String response = addPlace(newPlace);
                placeInputField.setText("");
                JOptionPane.showMessageDialog(frame, response);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "장소를 입력해 주세요");
            }
        });

        header.add(addPlaceLabel);
        header.add(placeInputField);
        header.add(addPlaceButton);

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
            addPlace("exit");
            frame.dispose();
        });

        footer.add(exitButton);

        frame.add(header, BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.add(footer, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
