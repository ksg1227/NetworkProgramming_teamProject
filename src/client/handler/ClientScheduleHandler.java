package client.handler;

import dto.ClientState;
import dto.HostSchedulingAction;
import dto.Packet;
import entity.Schedule;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientScheduleHandler extends ClientFeatureHandler {
    private final User user;
    private String scheduleName;
    private LocalDate startDate;
    private LocalDate endDate;

    public ClientScheduleHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User user) {
        super(serverInput, serverOutput);
        this.user = user;
    }

    @Override
    public void run() {
        try {
            if (user.isHost()) {
                handleHostGUI();
            } else {
                handleEnterAvailableDates();
            }
        } catch (Exception e) {}
    }

    private void handleHostGUI() throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("[Host] 일정 조율 메뉴");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton setDateRangeButton = new JButton("일정 설정");
        JButton endElectionButton = new JButton("조율 종료");
        JButton enterDatesButton = new JButton("가능 날짜 입력");

        setDateRangeButton.addActionListener(e -> {
            try {
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.START));
                frame.setVisible(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            setDateRangeGUI(frame);
        });

        endElectionButton.addActionListener(e -> {
            try {
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.END));
                frame.setVisible(false);
                getScheduleResult();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        enterDatesButton.addActionListener(e -> {
            try {
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.ADD_AVAILABLE_DATE));
                frame.setVisible(false);
                handleEnterAvailableDates();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(setDateRangeButton);
        panel.add(endElectionButton);
        panel.add(enterDatesButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void setDateRangeGUI(JFrame parentFrame) {
        if (scheduleName != null && startDate != null && endDate != null) {
            JOptionPane.showMessageDialog(parentFrame, scheduleName + " - Schedule coordination is not over yet.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JDialog dialog = new JDialog(parentFrame, "일정 설정", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(4, 1, 10, 10)); // 4개의 행으로 레이아웃 조정

        // Schedule Name 입력창
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Schedule Name:"), BorderLayout.WEST);
        JTextField scheduleNameField = new JTextField();
        namePanel.add(scheduleNameField, BorderLayout.CENTER);

        // Start Date 설정
        JPanel startPanel = new JPanel(new BorderLayout());
        startPanel.add(new JLabel("Start Date:"), BorderLayout.WEST);
        JButton startDateButton = new JButton("Select Start Date");
        startPanel.add(startDateButton, BorderLayout.CENTER);

        // End Date 설정
        JPanel endPanel = new JPanel(new BorderLayout());
        endPanel.add(new JLabel("End Date:"), BorderLayout.WEST);
        JButton endDateButton = new JButton("Select End Date");
        endPanel.add(endDateButton, BorderLayout.CENTER);

        // Confirm 버튼
        JButton confirmButton = new JButton("Confirm");

        // Date 선택 동작
        startDateButton.addActionListener(e -> {
            LocalDate selectedDate = selectDate("Select Start Date");
            if (selectedDate != null) {
                startDate = selectedDate;
                startDateButton.setText(startDate.toString());
            }
        });

        endDateButton.addActionListener(e -> {
            if (startDate != null) {
                LocalDate selectedDate = selectDate("Select End Date", startDate);
                if (selectedDate != null) {
                    endDate = selectedDate;
                    endDateButton.setText(endDate.toString());
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select the start date first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        Runnable handleConfirm = () -> {
            String scheduleName = scheduleNameField.getText().trim();

            Schedule schedule = null;
            if (scheduleName.isEmpty() || startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                schedule = new Schedule(scheduleName, startDate, endDate);
            }
            try {
                serverOutput.writeObject(new Packet<>(ClientState.SCHEDULE, schedule));
                serverOutput.flush();
                dialog.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

        // Confirm 버튼 동작
        confirmButton.addActionListener(e -> handleConfirm.run());

        // 구성 요소 추가
        dialog.add(namePanel);      // Schedule Name 입력창
        dialog.add(startPanel);     // Start Date 선택
        dialog.add(endPanel);       // End Date 선택
        dialog.add(confirmButton);  // Confirm 버튼

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleConfirm.run();
            }
        });

        dialog.setVisible(true);
    }

    private void handleEnterAvailableDates() throws IOException, ClassNotFoundException {
        if (isDateInputAvailable()) {
            getScheduleInfo();
        }
        if (scheduleName == null || startDate == null || endDate == null) {
            return;
        }
        enterAvailableDatesGUI();
    }

    private boolean isDateInputAvailable() throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("조율 실패");
        // 투표가 진행중인지 확인
        if(!isScheduling()) {
            JOptionPane.showMessageDialog(frame, "Coordination had not started.", "Error", JOptionPane.ERROR_MESSAGE);
            writer.println("Schedule coordination had not started");
            return false;
        }

        // 사용자 참여 여부 확인
        if(hasAlreadyVoted()) {
            JOptionPane.showMessageDialog(frame, "You can't enter again.", "Error", JOptionPane.ERROR_MESSAGE);
            writer.println("You can't enter again");
            return false;
        }

        return true;
    }

    private void getScheduleInfo() throws IOException, ClassNotFoundException {
        scheduleName = null;
        startDate = null;
        endDate = null;

        // 1. 서버로부터 스케줄 정보 요청
        writer.println("Retrieving schedule details from host...");
        Packet<String> requestScheduleInfo = new Packet<>(ClientState.SCHEDULE, "REQUEST_SCHEDULE_INFO");
        serverOutput.writeObject(requestScheduleInfo);

        // 2. 서버로부터 스케줄 정보 수신
        Packet<String> responsePacket = (Packet<String>) serverInput.readObject();
        String scheduleInfo = responsePacket.body();

        // 정규식 패턴 정의
        String regex = "Schedule Name: (.+?)\\nStart Date: (\\d{4}-\\d{2}-\\d{2})\\nEnd Date: (\\d{4}-\\d{2}-\\d{2})\\n";

        // Pattern과 Matcher 사용
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(scheduleInfo);

        // 값 추출
        if (matcher.find()) {
            scheduleName = matcher.group(1); // Schedule Name
            startDate = LocalDate.parse(matcher.group(2));   // Start Date
            endDate = LocalDate.parse(matcher.group(3));     // End Date

            // 출력
            System.out.println("Schedule Name: " + scheduleName);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
        } else {
            System.out.println("No match found!");
        }
    }

    private void enterAvailableDatesGUI() {
        JFrame frame = new JFrame("[" + scheduleName + "] 가능 일정 입력");
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Select Available Dates", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 연도와 월 표시
        JLabel monthLabel = new JLabel(
                String.format("%s: %s %d", scheduleName, startDate.getMonth(), startDate.getYear()),
                JLabel.CENTER
        );
        monthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(monthLabel, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 요일 헤더 추가
        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : weekdays) {
            JLabel dayLabel = new JLabel(day, JLabel.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 14));
            calendarPanel.add(dayLabel);
        }

        // 달력에 표시할 날짜 계산
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = startDate; date != null && !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date);
        }

        // 첫 번째 날짜 이전의 빈 칸 추가
        DayOfWeek firstDate = startDate.getDayOfWeek();
        int startOffset = (firstDate.getValue() % 7);
        for (int i = 0; i < startOffset; i++) {
            calendarPanel.add(new JLabel()); // 빈 셀 추가
        }

        // 날짜 버튼 추가
        List<JButton> dateButtons = new ArrayList<>();
        for (LocalDate date : dates) {
            JButton dateButton = new JButton(String.valueOf(date.getDayOfMonth()));
            dateButton.setBackground(Color.WHITE);
            dateButton.setOpaque(true);
            dateButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // 클릭으로 선택 토글
            dateButton.addActionListener(e -> {
                if (dateButton.getBackground() == Color.GREEN) {
                    dateButton.setBackground(Color.WHITE);
                } else {
                    dateButton.setBackground(Color.GREEN);
                }
            });

            dateButtons.add(dateButton);
            calendarPanel.add(dateButton);
        }

        mainPanel.add(calendarPanel, BorderLayout.CENTER);

        Runnable handleSubmit = () -> {
            List<String> selectedDates = new ArrayList<>();
            for (int i = 0; i < dateButtons.size(); i++) {
                JButton button = dateButtons.get(i);
                if (button.getBackground() == Color.GREEN) {
                    selectedDates.add(dates.get(i).toString());
                }
            }

            String availableDates = null;
            if (selectedDates.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No dates selected.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                availableDates = String.join(", ", selectedDates);
            }
            try {
                // 3. 서버에 가능 날짜 정보 전송
                serverOutput.writeObject(new Packet<String>(ClientState.SCHEDULE, availableDates));
                serverOutput.flush();
                frame.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

        // 확인 버튼
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.addActionListener(e -> handleSubmit.run());

        mainPanel.add(submitButton, BorderLayout.SOUTH);
        frame.add(mainPanel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleSubmit.run();
            }
        });

        frame.setVisible(true);
    }

    // 날짜 선택을 위한 헬퍼 메서드
    private LocalDate selectDate(String title) {
        return selectDate(title, null);
    }

    private LocalDate selectDate(String title, LocalDate minDate) {
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        int result = JOptionPane.showConfirmDialog(null, dateSpinner, title, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            LocalDate selectedDate = LocalDate.parse(editor.getFormat().format(dateSpinner.getValue()));
            if (minDate != null && selectedDate.isBefore(minDate)) {
                JOptionPane.showMessageDialog(null, "End date must be after start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (minDate != null && ChronoUnit.DAYS.between(minDate, selectedDate) > 30) {
                JOptionPane.showMessageDialog(null, "The selected date must not exceed 30 days from the start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return selectedDate;
        }
        return null;
    }

    private void getScheduleResult() throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("조율 결과");
        Packet<String> responsePacket = (Packet<String>) serverInput.readObject();
        String scheduleResult = responsePacket.body();

        JOptionPane.showMessageDialog(frame, scheduleResult, "End Coordination", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isScheduling() throws IOException, ClassNotFoundException {
        Boolean isScheduling = false;

        Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
        isScheduling = packet.body();

        return isScheduling;
    }

    private boolean hasAlreadyVoted() throws IOException, ClassNotFoundException {
        Boolean hasAlreadyVoted = false;

        Packet<Boolean> packet = (Packet<Boolean>) serverInput.readObject();
        hasAlreadyVoted = packet.body();

        return hasAlreadyVoted;
    }
}
