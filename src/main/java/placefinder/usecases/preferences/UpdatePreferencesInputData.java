package placefinder.usecases.preferences;

import placefinder.entities.Interest;
import java.util.List;

public class UpdatePreferencesInputData {
    private final int userId;
    private final double radiusKm;
    private final List<Interest> interests;

    public UpdatePreferencesInputData(int userId, double radiusKm, List<Interest> interests) {
        this.userId = userId;
        this.radiusKm = radiusKm;
        this.interests = interests;
    }

    public int getUserId() { return userId; }
    public double getRadiusKm() { return radiusKm; }
    public List<Interest> getInterests() { return interests; }
}
