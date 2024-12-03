package client;

import client.handler.ClientPlaceSuggestHandler;
import client.handler.ClientScheduleHandler;
import client.handler.ClientVoteHandler;
import dto.ClientState;
import dto.Packet;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientCore extends Thread {
    private Socket socket;
    private ObjectOutputStream serverOutput;
    private ObjectInputStream serverInput;
    private User client;

    private static JFrame mainFrame;
    private JLabel welcomeLabel;
    private JLabel logoLabel;
    private JButton enterChatButton;
    private JButton enterScheduleButton;
    private JButton suggestPlaceButton;
    private JButton votePlaceButton;
    private JButton showStatsButton;
    private JButton exitButton;

    public ClientCore() {
        initializeSocketConnection();
        initializeGUI();
    }

    // GUI 초기화
    private void initializeGUI() {
        mainFrame = new JFrame("Client Application");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));

        setupLogo(mainPanel);
        setupButtonPanel(mainPanel);

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }

    // 로고 설정
    private void setupLogo(JPanel mainPanel) {
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        // 로고 이미지 로드
        try {
            ImageIcon logoIcon = new ImageIcon("logo.png"); // 로고 파일 경로
            Image logoImage = logoIcon.getImage().getScaledInstance(450, 450, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImage));
        } catch (Exception e) {
            logoLabel.setText("Logo Here");
            logoLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        }

        mainPanel.add(logoLabel, BorderLayout.CENTER);
    }

    // 버튼 패널 설정
    private void setupButtonPanel(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20)); // 간격 키움
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        enterChatButton = createButton("Enter Chat", ClientState.CHATTING);
        enterScheduleButton = createButton("Enter Schedule", ClientState.SCHEDULE);
        suggestPlaceButton = createButton("Suggest Place", ClientState.PLACE_SUGGESTION);
        votePlaceButton = createButton("Vote Place", ClientState.PLACE_VOTE);
        showStatsButton = createButton("Show Statistics", ClientState.STATISTIC);
        exitButton = createButton("Exit", null);

        buttonPanel.add(enterChatButton);
        buttonPanel.add(enterScheduleButton);
        buttonPanel.add(suggestPlaceButton);
        buttonPanel.add(votePlaceButton);
        buttonPanel.add(showStatsButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // 버튼 생성 (크기와 폰트 설정)
    private JButton createButton(String text, ClientState state) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18)); // 폰트 크기 키움
        button.setPreferredSize(new Dimension(200, 80)); // 버튼 크기 설정
        if (state != null) {
            button.addActionListener(e -> handleButtonClick(state));
        } else {
            button.addActionListener(e -> exitApplication());
        }
        return button;
    }

    // 버튼 클릭 처리
    private void handleButtonClick(ClientState state) {
        try {
            notifyState(state);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (state) {
            case CHATTING -> startChatHandler();
            case SCHEDULE -> startScheduleHandler();
            case STATISTIC -> startStatisticsHandler();
            case PLACE_VOTE -> startVoteHandler();
            case PLACE_SUGGESTION -> startPlaceSuggestionHandler();
            default -> showMainFrame();
        }
    }

    // 상태 알림
    private void notifyState(ClientState state) throws IOException {
        Packet<Integer> packet = new Packet<>(state, 0);
        serverOutput.writeObject(packet);
        serverOutput.flush();
    }

    // 각 상태 처리 핸들러
    private void startChatHandler() {
        System.out.println("Chat");
        // 여기서 new ClientChatHandler().run() 하시면 됩니다.
    }

    private void startScheduleHandler() {
        new ClientScheduleHandler(serverInput, serverOutput, client).run();
    }

    private void startStatisticsHandler() {
        System.out.println("Statistics");
        // 여기서 new ClientStatisticHandler().run()
    }

    private void startVoteHandler() {
//        mainFrame.setVisible(false); 각 핸들러의 동작이 끝나면 showMainFrame()을 통해 화면을 다시 띄워야함
        new ClientVoteHandler(serverInput, serverOutput, client).run();
    }

    private void startPlaceSuggestionHandler() {
//        mainFrame.setVisible(false); 각 핸들러의 동작이 끝나면 showMainFrame()을 통해 화면을 다시 띄워야함
        new ClientPlaceSuggestHandler(serverInput, serverOutput).run();
    }

    // 소켓 초기화
    public void initializeSocketConnection() {
        try {
            socket = new Socket("localhost", 10000);
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
            serverOutput.flush();
            serverInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        createClient();
    }

    // 클라이언트 생성
    private void createClient() {
        String userName;
        try {
            userName = JOptionPane.showInputDialog(mainFrame, "Enter your name: ");
            serverOutput.writeObject(userName);
            client = (User) serverInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 애플리케이션 종료
    private void exitApplication() {
        mainFrame.dispose();
        try {
            if (serverOutput != null) serverOutput.close();
            if (serverInput != null) serverInput.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    // 메인 화면 표시
    public static void showMainFrame() {
        mainFrame.setVisible(true);
    }
}
