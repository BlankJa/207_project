package placefinder.entities;

import java.util.ArrayList;
import java.util.List;

public class PreferenceProfile {
    private int userId;
    private double radiusKm;
    private List<Interest> interests = new ArrayList<>();

    public PreferenceProfile(int userId, double radiusKm, List<Interest> interests) {
        this.userId = userId;
        this.radiusKm = radiusKm;
        if (interests != null) {
            this.interests = new ArrayList<>(interests);
        }
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(double radiusKm) { this.radiusKm = radiusKm; }

    public List<Interest> getInterests() { return interests; }
    public void setInterests(List<Interest> interests) {
        this.interests = interests != null ? new ArrayList<>(interests) : new ArrayList<>();
    }
}
