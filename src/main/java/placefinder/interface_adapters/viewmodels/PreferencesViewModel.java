package placefinder.interface_adapters.viewmodels;

import placefinder.entities.FavoriteLocation;
import placefinder.entities.Interest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferencesViewModel {
    private double radiusKm = 2.0;
    private List<Interest> interests = new ArrayList<>();
    private List<FavoriteLocation> favorites = new ArrayList<>();
    private String message;
    private String errorMessage;

    public double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(double radiusKm) { this.radiusKm = radiusKm; }

    public List<Interest> getInterests() { return Collections.unmodifiableList(interests); }
    public void setInterests(List<Interest> interests) {
        this.interests = interests != null ? new ArrayList<>(interests) : new ArrayList<>();
    }

    public List<FavoriteLocation> getFavorites() { return Collections.unmodifiableList(favorites); }
    public void setFavorites(List<FavoriteLocation> favorites) {
        this.favorites = favorites != null ? new ArrayList<>(favorites) : new ArrayList<>();
    }

    public void addFavorite(FavoriteLocation favorite) {
        if (favorite == null) return;
        this.favorites.add(favorite);
    }

    public void removeFavoriteById(int id) {
        this.favorites.removeIf(f -> f.getId() != null && f.getId() == id);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
