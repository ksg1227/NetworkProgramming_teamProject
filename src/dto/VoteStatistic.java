package dto;

import java.io.Serializable;

public record VoteStatistic(String place, Integer count) implements Serializable {
}
