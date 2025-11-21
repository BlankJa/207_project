package placefinder.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Plan {
    private Integer id;
    private int userId;
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private String originAddress;
    private Route route;
    private double snapshotRadiusKm;
    private List<Interest> snapshotInterests = new ArrayList<>();

    public Plan(Integer id, int userId, String name,
                LocalDate date, LocalTime startTime,
                String originAddress, Route route,
                double snapshotRadiusKm, List<Interest> snapshotInterests) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.originAddress = originAddress;
        this.route = route;
        this.snapshotRadiusKm = snapshotRadiusKm;
        if (snapshotInterests != null) {
            this.snapshotInterests = new ArrayList<>(snapshotInterests);
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public String getOriginAddress() { return originAddress; }
    public void setOriginAddress(String originAddress) { this.originAddress = originAddress; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public double getSnapshotRadiusKm() { return snapshotRadiusKm; }
    public void setSnapshotRadiusKm(double snapshotRadiusKm) { this.snapshotRadiusKm = snapshotRadiusKm; }

    public List<Interest> getSnapshotInterests() { return snapshotInterests; }
    public void setSnapshotInterests(List<Interest> snapshotInterests) {
        this.snapshotInterests = snapshotInterests != null
                ? new ArrayList<>(snapshotInterests) : new ArrayList<>();
    }
}
