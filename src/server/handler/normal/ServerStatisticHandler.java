package server.handler.normal;

import dto.Statistic;
import dto.StatisticResponse;
import dto.VoteStatistic;
import entity.Schedule;
import server.ServerCore;

import java.io.*;
import java.util.Map;

import static dto.StatisticResponse.*;

public class ServerStatisticHandler extends ServerFeatureHandler {
    public ServerStatisticHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    public void run() {
        Schedule schedule = ServerCore.getGlobalSchedule();
        VoteStatistic voteStatistic = ServerVoteHandler.getResult();

        if (schedule.getScheduleName() == null) {
            sendResponse(clientOutput, NO_SCHEDULE);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (voteStatistic.place().isEmpty()) {
            sendResponse(clientOutput, NO_SUGGESTED_PLACE);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // 정상적인 Statistic 응답 생성
        Statistic statistic = new Statistic(schedule.getScheduleName(), schedule.getMaxValueAvailability(), voteStatistic);
        sendResponse(clientOutput, statistic);

    }

    private void sendResponse(ObjectOutputStream clientOutput, Object response) {
        try {
            clientOutput.writeObject(response);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
