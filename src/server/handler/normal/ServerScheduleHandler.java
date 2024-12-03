package server.handler.normal;

import dto.ClientState;
import dto.Packet;
import entity.Schedule;
import entity.User;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerScheduleHandler extends ServerFeatureHandler {
    private final User user;
    private final Schedule schedule;
    private static volatile boolean isScheduling = false;
    protected static final ConcurrentHashMap<User, Boolean> userParticipation = new ConcurrentHashMap<>(); // 사용자 참여 상태 관리

    public ServerScheduleHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients, User user, Schedule schedule) {
        super(clientInput, clientOutput, onFeatureClients);
        this.user = user;
        this.schedule = schedule;
    }

    @Override
    public void run() {
        try {
            // 투표가 진행중인지 확인
            sendResponse(isScheduling);
            if (!isScheduling) return;

            // 사용자 참여 여부 확인
            sendResponse(userParticipation.containsKey(user));
            if (userParticipation.containsKey(user)) return;

            // 클라이언트 요청 처리
            handleClientRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClientRequest() throws IOException, ClassNotFoundException {
        Packet<String> requestPacket = (Packet<String>) clientInput.readObject();
        System.out.println("Received packet: " + requestPacket.body());
        String request = requestPacket.body();

        // 1. 클라이언트로부터 스케줄 정보 요청 수신
        if ("REQUEST_SCHEDULE_INFO".equals(request)) {
            // 2. 클라이언트에 스케줄 정보 전달
            sendScheduleInfo();
        } else {
            return;
        }

        // 3. 클라이언트로부터 가능 날짜 정보 수신
        Packet<String> datesPacket = (Packet<String>) clientInput.readObject();
        updateAvailability(datesPacket.body());

        userParticipation.put(user, true); // 참여 표시
    }

    private void sendScheduleInfo() throws IOException {
        synchronized (schedule) {
            String scheduleInfo = String.format("Name: %s, Start Date: %s, End Date: %s",
                    schedule.getScheduleName(),
                    schedule.getStartDate(),
                    schedule.getEndDate());
            clientOutput.writeObject(new Packet<>(ClientState.SCHEDULE, scheduleInfo));
        }
    }

    private void updateAvailability(String availableDates) {
        if (!schedule.hasInitialDates()) {
            System.out.println("Schedule range is not initialized.");
            return;
        }

        String[] dates = availableDates.split(", ");
        LocalDate localDate;
        for (String date : dates) {
            localDate = parseDate(date);
            if (localDate == null) {
                continue;
            }

            System.out.println("local date: " + localDate);
            synchronized (schedule) {
                if (schedule.isWithinRange(localDate)) {
                    schedule.incrementAvailability(localDate);
                } else {
                    System.out.println("Date out of range: " + localDate);
                }
            }

        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + date);
            return null;
        }
    }

    private void sendResponse(Object message) throws IOException {
        clientOutput.writeObject(new Packet<>(ClientState.SCHEDULE, message));
    }

    public static synchronized void startScheduling() {
        isScheduling = true;
        System.out.println("[ServerScheduleHandler] Scheduling started.");
    }

    public static synchronized void stopScheduling() {
        isScheduling = false;
        System.out.println("[ServerScheduleHandler] Scheduling stopped.");
    }
}
