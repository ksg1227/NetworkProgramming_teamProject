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
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class HostScheduleHandler extends ServerScheduleHandler {
    private final Schedule schedule;

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
        Packet<String> requestPacket = (Packet<String>) clientInput.readObject();
        String[] scheduleInfo = requestPacket.body().split(",");
        String name = scheduleInfo[0];
        LocalDate startDate = LocalDate.parse(scheduleInfo[1]);
        LocalDate endDate = LocalDate.parse(scheduleInfo[2]);

        synchronized (schedule) {
            schedule.setScheduleName(name);
            schedule.setStartDate(startDate);
            schedule.setEndDate(endDate);
        }

        // 사용자들에게 스케줄 조율 시작 알림
        String notification = String.format("Name: %s, Start Date: %s, End Date: %s",
                schedule.getScheduleName(),
                schedule.getStartDate(),
                schedule.getEndDate());
        synchronized (Objects.requireNonNull(onFeatureClients)) {
            for (ObjectOutputStream client : onFeatureClients.values()) {
                try {
                    client.writeObject(notification);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processResults() throws IOException {
        synchronized (schedule) {
            // 가장 높은 투표수를 가진 날짜 추출
            Map<LocalDate, Integer> maxValueAvailability = schedule.getMaxValueAvailability();

            if (maxValueAvailability.isEmpty()) {
                sendToAllClients("No votes received. Scheduling failed.");
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
            sendToAllClients(result);
        }
    }

    private void sendToAllClients(String message) {
        for (ObjectOutputStream client : onFeatureClients.values()) {
            try {
                client.writeObject(new Packet<>(ClientState.SCHEDULE, message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void voteDate() { super.run(); }
}
