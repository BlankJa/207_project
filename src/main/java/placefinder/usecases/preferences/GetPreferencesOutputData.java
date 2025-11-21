package placefinder.usecases.preferences;

import placefinder.entities.FavoriteLocation;
import placefinder.entities.Interest;

import java.util.List;

public class GetPreferencesOutputData {
    private final double radiusKm;
    private final List<Interest> interests;
    private final List<FavoriteLocation> favorites;
    private final String errorMessage;

    public GetPreferencesOutputData(double radiusKm, List<Interest> interests,
                                    List<FavoriteLocation> favorites, String errorMessage) {
        this.radiusKm = radiusKm;
        this.interests = interests;
        this.favorites = favorites;
        this.errorMessage = errorMessage;
    }

    public double getRadiusKm() { return radiusKm; }
    public List<Interest> getInterests() { return interests; }
    public List<FavoriteLocation> getFavorites() { return favorites; }
    public String getErrorMessage() { return errorMessage; }
}
