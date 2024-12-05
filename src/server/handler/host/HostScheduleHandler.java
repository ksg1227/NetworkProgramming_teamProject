package server.handler.host;

import dto.ClientState;
import dto.HostSchedulingAction;
import dto.Packet;
import entity.Schedule;
import entity.User;
import server.handler.normal.ServerScheduleHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class HostScheduleHandler extends ServerScheduleHandler implements Serializable {
    private final Schedule schedule;
    private static volatile boolean isScheduling = false;

    public HostScheduleHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients, User user, Schedule schedule) {
        super(clientInput, clientOutput, onFeatureClients, user, schedule);
        this.schedule = schedule;
    }

    @Override
    public void run() {
        try {
            HostSchedulingAction action = null;

            try {
                Packet<HostSchedulingAction> packet = (Packet<HostSchedulingAction>) clientInput.readObject();

                assert packet.clientState().equals(ClientState.SCHEDULE);
                action = packet.body();
            } catch (Exception e) {}

            switch (action) {
                case START:
                    makeDateRange();
                    startScheduling();
                    break;
                case END:
                    stopScheduling();
                    processResults();
                    break;
                case ADD_AVAILABLE_DATE:
                    voteDate();
                    break;
                case null, default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeDateRange() throws IOException, ClassNotFoundException {
        // 일정 이름과 기간 설정
        Packet<Schedule> requestPacket = (Packet<Schedule>) clientInput.readObject();
        Schedule newSchedule = requestPacket.body();
        if (newSchedule.getScheduleName() == null) {
            return;
        }

        synchronized (schedule) {
            schedule.setScheduleName(newSchedule.getScheduleName());
            schedule.setStartDate(newSchedule.getStartDate());
            schedule.setEndDate(newSchedule.getEndDate());
        }

        // 사용자들에게 스케줄 조율 시작 알림
        String notification = schedule.toString();
        sendToAllClients(notification);
    }

    private void processResults() throws IOException {
        synchronized (schedule) {
            if (!schedule.hasInitialDates()) {
                String result = "Scheduling has not started yet.";
                clientOutput.writeObject(new Packet<>(ClientState.SCHEDULE, result));
                return;
            }

            // 가장 높은 투표수를 가진 날짜 추출
            Map<LocalDate, Integer> maxValueAvailability = schedule.getMaxValueAvailability();

            if (maxValueAvailability.isEmpty()) {
                String result = "No dates received. Scheduling failed.";
                clientOutput.writeObject(new Packet<>(ClientState.SCHEDULE, result));
                return;
            }

            String datesWithMaxVotes = maxValueAvailability.keySet().stream()
                    .map(LocalDate::toString)
                    .reduce((d1, d2) -> d1 + ", " + d2)
                    .orElse("No dates");

            // 최다 투표 수
            int topCount = maxValueAvailability.values().stream().findFirst().orElse(0);

            String result = String.format("Most voted date: %s with %d votes.", datesWithMaxVotes, topCount);

            System.out.println("[HostScheduleHandler] Results sent to all clients: " + result);

            // 모든 클라이언트에게 결과 전송
            // TODO: 결과 전송 수신할 위치 지정
            clientOutput.writeObject(new Packet<>(ClientState.SCHEDULE, result));
        }
    }

    private void sendToAllClients(String message) {
        synchronized (Objects.requireNonNull(onFeatureClients)) {
            for (Object client : onFeatureClients.values()) {
                if (client instanceof ObjectOutputStream) {
                    ObjectOutputStream outputStream = (ObjectOutputStream) client;
                    try {
                        System.out.println("[HostScheduleHandler] Sending to client: " + message);
                        outputStream.writeObject(new Packet<>(ClientState.SCHEDULE, message));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void voteDate() { super.run(); }

    public static synchronized boolean isScheduling() {
        return isScheduling;
    }

    private static synchronized void startScheduling() {
        isScheduling = true;
        System.out.println("[HostScheduleHandler] Scheduling started.");
    }

    private static synchronized void stopScheduling() {
        isScheduling = false;
        userParticipation.clear();
        System.out.println("[HostScheduleHandler] Scheduling stopped.");
    }
}
