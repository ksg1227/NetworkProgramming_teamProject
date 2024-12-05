package dto;

import java.io.Serializable;
import java.util.List;

public record Statistic(String name, List<String> dates, VoteStatistic voteStatistic) implements Serializable {
}
