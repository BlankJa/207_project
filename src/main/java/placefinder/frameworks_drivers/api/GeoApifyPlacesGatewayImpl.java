package placefinder.frameworks_drivers.api;

import placefinder.entities.IndoorOutdoorType;
import placefinder.entities.Place;
import placefinder.usecases.ports.PlacesGateway;

import java.util.*;
import java.util.stream.Collectors;

public class GeoApifyPlacesGatewayImpl implements PlacesGateway {

    private final GeoapifyPlacesService service;

    public GeoApifyPlacesGatewayImpl() {
        this.service = new GeoapifyPlacesService("75be457789934a199ed4014ad24925ba");
    }

    public GeoApifyPlacesGatewayImpl(String apiKey) {
        this.service = new GeoapifyPlacesService(apiKey);
    }

    @Override
    public List<Place> searchPlaces(double lat, double lon, double radiusKm,
                                    Map<String, List<String>> selectedCategories) throws Exception {
        // Convert selectedCategories Map to List<String> for the service
        List<String> categoryList = convertCategoriesToList(selectedCategories);

        // Delegate to the new GeoapifyPlacesService
        List<GeoapifyPlacesService.CleanPlace> cleanPlaces =
            service.searchPlaces(lat, lon, radiusKm, categoryList);

        // Convert CleanPlace objects to Place entities
        return cleanPlaces.stream()
            .map(this::toPlaceEntity)
            .collect(Collectors.toList());
    }

    /**
     * Converts the Map-based category structure to a flat List of category strings.
     * Returns null if empty, allowing the service to use its defaults.
     */
    private List<String> convertCategoriesToList(Map<String, List<String>> selectedCategories) {
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            return null; // Let GeoapifyPlacesService use its default categories
        }

        List<String> allCategories = selectedCategories.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        return allCategories.isEmpty() ? null : allCategories;
    }

    /**
     * Converts a CleanPlace from the service to a Place entity for use in the application.
     */
    private Place toPlaceEntity(GeoapifyPlacesService.CleanPlace cleanPlace) {
        return new Place(
            cleanPlace.getId(),
            cleanPlace.getName(),
            cleanPlace.getAddress(),
            cleanPlace.getLatitude(),
            cleanPlace.getLongitude(),
            cleanPlace.getDistanceKm(),
            convertPlaceType(cleanPlace.getPlaceType()),
            cleanPlace.getAllCategories()
        );
    }

    /**
     * Converts the service's PlaceType to the entity's IndoorOutdoorType.
     */
    private IndoorOutdoorType convertPlaceType(GeoapifyPlacesService.PlaceType placeType) {
        return switch (placeType) {
            case INDOOR -> IndoorOutdoorType.INDOOR;
            case OUTDOOR -> IndoorOutdoorType.OUTDOOR;
            case MIXED -> IndoorOutdoorType.MIXED;
            case UNKNOWN -> IndoorOutdoorType.UNKNOWN;
        };
    }
}
