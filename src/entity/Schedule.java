package entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Schedule {
    private LocalDate startDate;
    private LocalDate endDate;
    private final Map<LocalDate, Integer> dateAvailability = new HashMap<>();

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dateAvailability.put(currentDate, 0);
            currentDate = currentDate.plusDays(1);
        }
    }

    public void incrementAvailability(LocalDate date) {
        if (dateAvailability.containsKey(date)) {
            dateAvailability.put(date, dateAvailability.get(date) + 1);
        }
    }

    public Map<LocalDate, Integer> getSortedAvailability() {
        return dateAvailability.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
