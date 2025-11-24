package placefinder.entities;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<PlanStop> stops = new ArrayList<>();
    private final List<Leg> legs = new ArrayList<>();
    private final int distance;
    private final int duration;
    private final String encodedPolyline;

    public Route(List<PlanStop> stops, List<Leg> legs, int distance, int duration, String encodedPolyline) {
        if (stops != null) {
            this.stops = new ArrayList<>(stops);
        }
    }

    public List<PlanStop> getStops() { return stops; }
}
