package client.handler;

import entity.User;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class ClientChatHandler extends ClientFeatureHandler {
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 500;
    private final String exitMessage = "UserQuitFromChattingRoom";
    private final User client;
    private volatile boolean running = true; // 스레드 실행 상태 플래그
    private BufferedReader chatReader;
    private PrintWriter chatWriter;
    private JFrame chatFrame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton exitButton;

    public ClientChatHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, BufferedReader chatReader, PrintWriter chatWriter, User client) {
        super(serverInput, serverOutput);
        this.chatReader = chatReader;
        this.chatWriter = chatWriter;
        this.client = client;
        SwingUtilities.invokeLater(() -> initializeGUI());
    }



    private void initializeGUI() {
        chatFrame = createChatFrame();
        chatArea = createChatArea();
        inputField = createInputField();
        sendButton = createSendButton();
        exitButton = createExitButton();

        // 구성 요소 배치
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(inputPanel, BorderLayout.CENTER);
        buttonPanel.add(exitButton, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatFrame.add(scrollPane, BorderLayout.CENTER);
        chatFrame.add(buttonPanel, BorderLayout.SOUTH);

        chatFrame.setVisible(true);
    }

    // 채팅 프레임 생성
    private JFrame createChatFrame() {
        JFrame frame = new JFrame("Chat Room: " + client.getUserName());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitChat();
            }
        });
        return frame;
    }

    // 채팅 영역 생성
    private JTextArea createChatArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        return area;
    }

    private JTextField createInputField() {
        JTextField field = new JTextField();
        field.addActionListener(e -> sendMessage()); // 엔터키 입력 시 메시지 전송
        return field;
    }

    private JButton createSendButton() {
        JButton button = new JButton("Send");
        button.addActionListener(e -> sendMessage());
        return button;
    }

    private JButton createExitButton() {
        JButton button = new JButton("Exit");
        button.addActionListener(e -> exitChat());
        return button;
    }

    private void exitChat() {
        if (running) {
            chatWriter.println(exitMessage);
            chatWriter.flush();
            running = false;
        }
        chatFrame.dispose();
    }

    private void sendMessage() {
        String inputMsg = inputField.getText().trim();
        if (!inputMsg.isEmpty()) {
            chatWriter.println(inputMsg);
            chatWriter.flush();
            inputField.setText("");
        }
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            chatArea.append("채팅방에 입장하였습니다.\n");
            Thread receiverThread = createReceiverThread();
        });
    }

    // 수신 스레드 생성
    private Thread createReceiverThread() {
        Thread receiverThread = new Thread(this::receiveMessage);
        receiverThread.start();
        return receiverThread;
    }

    private void receiveMessage() {
        try {
            while (running) {
                if (chatReader.ready()) {
                    String receivedMessage = chatReader.readLine();
                    if (receivedMessage != null) {
                        appendMessageToChatArea(receivedMessage);
                    }
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            if (running) {
                appendMessageToChatArea("메시지 수신 중 오류가 발생했습니다: " + e.getMessage());
            }
        }
    }

    private void appendMessageToChatArea(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }
}
