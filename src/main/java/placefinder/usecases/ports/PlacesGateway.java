package placefinder.usecases.ports;

import placefinder.entities.Interest;
import placefinder.entities.Place;

import java.util.List;

public interface PlacesGateway {
    List<Place> searchPlaces(double lat, double lon, double radiusKm, List<Interest> interests) throws Exception;
}
