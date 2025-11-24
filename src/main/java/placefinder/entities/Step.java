package placefinder.entities;

public class Step {
    private final int distance;
    private final double duration;
    private final String encodedPolyline;
    private final PlanStop startLocation;
    private final PlanStop endLocation;
    private final String navInstruction;

    public Step (int distance, double duration, String polyline, PlanStop startLocation, PlanStop endLocation, String navInstruction) {
        this.distance = distance;
        this.duration = duration;
        this.encodedPolyline = polyline;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.navInstruction = navInstruction;
    }

    public int getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    public String getEncodedPolyline() {
        return encodedPolyline;
    }

    public PlanStop getStartLocation() {
        return startLocation;
    }

    public PlanStop getEndLocation() {
        return endLocation;
    }

    public String getNavInstruction() {
        return navInstruction;
    }
}
