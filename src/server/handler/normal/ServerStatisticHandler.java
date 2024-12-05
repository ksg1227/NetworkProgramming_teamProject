package server.handler.normal;

import dto.Statistic;
import dto.VoteStatistic;
import entity.Schedule;
import server.ServerCore;

import java.io.*;
import java.util.Map;

public class ServerStatisticHandler extends ServerFeatureHandler {
    public ServerStatisticHandler(ObjectInputStream clientInput, ObjectOutputStream clientOutput, Map<String, ObjectOutputStream> onFeatureClients) {
        super(clientInput, clientOutput, onFeatureClients);
    }

    @Override
    public void run() {
        Schedule schedule = ServerCore.getGlobalSchedule();
        VoteStatistic voteStatistic = ServerVoteHandler.getResult();

        Statistic statistic = new Statistic(schedule.getScheduleName(), schedule.getMaxValueAvailability(), voteStatistic);

        try {
            clientOutput.writeObject(statistic);
        } catch (IOException e) {}
    }
}
