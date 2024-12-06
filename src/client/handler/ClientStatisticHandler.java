package client.handler;

import dto.Packet;
import dto.Statistic;
import dto.StatisticResponse;
import dto.VoteStatistic;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Map;

public class ClientStatisticHandler extends ClientFeatureHandler {
    private JFrame frame;
    private JLabel scheduleNameLabel;
    private JTextArea datesTextArea;
    private JLabel placeLabel;

    public ClientStatisticHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {
        try {
            // 서버에서 응답 수신
            Object response = serverInput.readObject();

            // 응답을 분기 처리
            if (response instanceof Statistic) {
                Statistic statistic = (Statistic) response;
                VoteStatistic voteStatistic = statistic.voteStatistic();

                String scheduleName = statistic.name();
                Map<LocalDate, Integer> votedDates = statistic.dates();
                String place = voteStatistic.place();

                // Swing UI 구성
                SwingUtilities.invokeLater(() -> createAndShowGUI(scheduleName, votedDates, place));
            } else if (response instanceof StatisticResponse) {
                handleStatisticResponse((StatisticResponse) response);
            } else {
                throw new IllegalArgumentException("Unrecognized response type from server: " + response.getClass());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * StatisticResponse 처리
     */
    private void handleStatisticResponse(StatisticResponse response) {
        switch (response) {
            case NO_SCHEDULE -> JOptionPane.showMessageDialog(null, "일정이 정해지지 않았습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            case NO_SUGGESTED_PLACE -> JOptionPane.showMessageDialog(null, "장소가 정해지지 않았습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            default -> JOptionPane.showMessageDialog(null, "알 수 없는 응답입니다.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * GUI를 생성하고 표시하는 메서드
     */
    private void createAndShowGUI(String scheduleName, Map<LocalDate, Integer> votedDates, String place) {
        frame = new JFrame("Statistic Viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1)); // 화면을 3등분
        frame.setSize(500, 700);

        // 상단 패널: Schedule Name
        JPanel topPanel = new JPanel(new BorderLayout());
        scheduleNameLabel = new JLabel(scheduleName, SwingConstants.CENTER);
        scheduleNameLabel.setFont(new Font("Arial", Font.BOLD, 24)); // 일정 이름 폰트 크기 조정
        topPanel.add(scheduleNameLabel, BorderLayout.CENTER);

        // 중앙 패널: Dates with Scroll
        JPanel centerPanel = new JPanel(new BorderLayout());
        JLabel dateTitleLabel = new JLabel("가능한 날짜 목록", SwingConstants.CENTER);
        dateTitleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // 제목 크기 조정

        datesTextArea = new JTextArea();
        datesTextArea.setFont(new Font("Arial", Font.PLAIN, 16)); // 날짜 목록의 글자 크기 조정
        datesTextArea.setEditable(false);
        appendDatesToTextArea(votedDates);

        JScrollPane scrollPane = new JScrollPane(datesTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 내부 여백 추가

        centerPanel.add(dateTitleLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // 하단 패널: Place Label with Spacer and Main Menu Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        placeLabel = new JLabel("장소: " + place, SwingConstants.CENTER);
        placeLabel.setFont(new Font("Arial", Font.BOLD, 18)); // 장소 텍스트 크기 조정

        // 날짜 영역과 장소 사이 여백 추가
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 20)); // 20픽셀 높이로 빈 공간 생성

        bottomPanel.add(spacerPanel, BorderLayout.NORTH); // 여백 추가
        bottomPanel.add(placeLabel, BorderLayout.CENTER);

        JButton mainMenuButton = new JButton("메인 메뉴로");
        mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 16));
        mainMenuButton.addActionListener(e -> frame.dispose()); // 버튼 클릭 시 창 닫기

        bottomPanel.add(mainMenuButton, BorderLayout.SOUTH);

        // 프레임에 패널 추가
        frame.add(topPanel);   // 상단 패널 추가
        frame.add(centerPanel); // 중앙 패널 추가
        frame.add(bottomPanel); // 하단 패널 추가

        // 프레임 표시
        frame.setVisible(true);
    }


    /**
     * 날짜 데이터를 TextArea에 추가하는 메서드
     */
    private void appendDatesToTextArea(Map<LocalDate, Integer> votedDates) {
        for (Map.Entry<LocalDate, Integer> entry : votedDates.entrySet()) {
            LocalDate date = entry.getKey();
            Integer votes = entry.getValue();
            datesTextArea.append(date + " :  " + votes + "표\n");
        }
    }
}
