package dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record Statistic(String name, Map<LocalDate, Integer> dates, VoteStatistic voteStatistic) implements Serializable {
}
