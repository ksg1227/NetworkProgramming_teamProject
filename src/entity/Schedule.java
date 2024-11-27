package entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Schedule {
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleName;
    private final Map<LocalDate, Integer> dateAvailability = new HashMap<>();

    public Schedule() {}

    public synchronized void incrementAvailability(LocalDate date) {
        if (dateAvailability.containsKey(date)) {
            dateAvailability.put(date, dateAvailability.get(date) + 1);
        }
    }

    private Map<LocalDate, Integer> getSortedAvailability() {
        return dateAvailability.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }

    public Map<LocalDate, Integer> getMaxValueAvailability() {
        Map<LocalDate, Integer> sortedAvailability = getSortedAvailability();
        int maxCount = sortedAvailability.entrySet().iterator().next().getValue();

        return dateAvailability.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount) // 최대값인 경우만 포함
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }

    public synchronized LocalDate getStartDate() {
        return startDate;
    }

    public synchronized LocalDate getEndDate() {
        return endDate;
    }

    public synchronized String getScheduleName() {
        return scheduleName;
    }

    public synchronized void setStartDate(LocalDate date) {
        this.startDate = date;
        initializeDateAvailability();
    }

    public synchronized void setEndDate(LocalDate date) {
        this.endDate = date;
        initializeDateAvailability();
    }

    public synchronized void setScheduleName(String name) {
        this.scheduleName = name;
    }

    private synchronized void initializeDateAvailability() {
        if (startDate != null && endDate != null) {
            dateAvailability.clear(); // 기존 데이터를 초기화
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                dateAvailability.put(currentDate, 0);
                currentDate = currentDate.plusDays(1);
            }
        }
    }

    public boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean hasInitialDates() {
        return startDate != null && endDate != null;
    }
}
