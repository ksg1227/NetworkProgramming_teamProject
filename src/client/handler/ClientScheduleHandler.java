package client.handler;

import dto.ClientState;
import dto.HostSchedulingAction;
import dto.Packet;
import entity.Schedule;
import entity.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ClientScheduleHandler extends ClientFeatureHandler {
    private final User user;

    public ClientScheduleHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput, User user) {
        super(serverInput, serverOutput);
        this.user = user;
    }

    @Override
    public void run() {
        try {
            if (user.isHost()) {
                handleHost();
            } else {
                enterAvailableDates();
            }
        } catch (Exception e) {}
    }

    private void handleHost() throws IOException, ClassNotFoundException {
        writer.println("Select the menu you want to enter");
        writer.println("[1]. Set date range");
        writer.println("[2]. End election");
        writer.println("[3]. Enter available dates");
        writer.println("type [exit] to exit");

        String input = scanner.nextLine();
        switch (input) {
            case "exit":
                return;
            case "1":
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.START));
                Schedule newSchedule = null;
                while (newSchedule == null) {
                    newSchedule = setDateRange();
                }
                writer.println("Start date coordination");
                break;
            case "2":
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.END));
                writer.println("End date coordination");
                break;
            case "3":
                serverOutput.writeObject(new Packet<HostSchedulingAction>(ClientState.SCHEDULE, HostSchedulingAction.ADD_AVAILABLE_DATE));
                enterAvailableDates();
            case null, default:
                break;
        }
    }

    private Schedule setDateRange() throws IOException {
        writer.println("Enter schedule name: ");
        String name = scanner.nextLine();

        writer.println("Enter start date (YYYY-MM-DD): ");
        String start = scanner.nextLine();
        LocalDate startDate = parseDate(start);

        writer.println("Enter end date (YYYY-MM-DD): ");
        String end = scanner.nextLine();
        LocalDate endDate = parseDate(end);

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return null;
        }

        Schedule newSchedule = new Schedule(name, startDate, endDate);

        Packet<Schedule> packet = new Packet<>(ClientState.SCHEDULE, newSchedule);
        serverOutput.writeObject(packet);

        return newSchedule;
    }

    private void enterAvailableDates() throws IOException, ClassNotFoundException {
        // 투표가 진행중인지 확인
        if(!isScheduling()) {
            writer.println("Vote had not started");
            return;
        }

        // 사용자 참여 여부 확인
        if(hasAlreadyVoted()) {
            writer.println("You can't vote again");
            return;
        }

        // 1. 서버로부터 스케줄 정보 요청
        writer.println("Retrieving schedule details from host...");
        Packet<String> requestScheduleInfo = new Packet<>(ClientState.SCHEDULE, "REQUEST_SCHEDULE_INFO");
        serverOutput.writeObject(requestScheduleInfo);
        serverOutput.flush();

        // 2. 서버로부터 스케줄 정보 수신
        Packet<String> responsePacket = (Packet<String>) serverInput.readObject();
        String scheduleInfo = responsePacket.body();

        writer.println("Current Schedule: ");
        writer.println(scheduleInfo);

        writer.println("Enter available dates (YYYY-MM-DD, comma separated)");
        String dates = scanner.nextLine();

        // 3. 서버에 가능 날짜 정보 전송
        Packet<String> packet = new Packet<>(ClientState.SCHEDULE, dates);
        serverOutput.writeObject(packet);
        serverOutput.flush();
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

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + date);
            return null;
        }
    }
}
