package placefinder.usecases.searchplaces;

import placefinder.entities.*;
import placefinder.usecases.ports.GeocodingGateway;
import placefinder.usecases.ports.PlacesGateway;
import placefinder.usecases.ports.PreferenceGateway;
import placefinder.usecases.ports.WeatherGateway;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class SearchPlacesInteractor implements SearchPlacesInputBoundary {

    private final PreferenceGateway preferenceGateway;
    private final GeocodingGateway geocodingGateway;
    private final PlacesGateway placesGateway;
    private final WeatherGateway weatherGateway;
    private final SearchPlacesOutputBoundary presenter;

    public SearchPlacesInteractor(PreferenceGateway preferenceGateway,
                                  GeocodingGateway geocodingGateway,
                                  PlacesGateway placesGateway,
                                  WeatherGateway weatherGateway,
                                  SearchPlacesOutputBoundary presenter) {
        this.preferenceGateway = preferenceGateway;
        this.geocodingGateway = geocodingGateway;
        this.placesGateway = placesGateway;
        this.weatherGateway = weatherGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchPlacesInputData inputData) {
        try {
            PreferenceProfile profile = preferenceGateway.loadForUser(inputData.getUserId());

            GeocodeResult geo = geocodingGateway.geocode(inputData.getLocationText());
            if (geo == null) {
                presenter.present(new SearchPlacesOutputData(
                        List.of(), null, false, "Could not find that location."));
                return;
            }

            LocalDate date = LocalDate.parse(inputData.getDate());
            WeatherSummary weather = null;
            boolean weatherUsed = false;
            try {
                weather = weatherGateway.getDailyWeather(geo.getLat(), geo.getLon(), date);
                if (weather != null) weatherUsed = true;
            } catch (Exception e) {
                weatherUsed = false;
            }

            List<Place> places = placesGateway.searchPlaces(
                    geo.getLat(), geo.getLon(), profile.getRadiusKm(), profile.getInterests()
            );

            rankPlaces(places, profile.getInterests(), weather);
            presenter.present(new SearchPlacesOutputData(
                    places,
                    geo.getFormattedAddress(),
                    weatherUsed,
                    null
            ));
        } catch (Exception e) {
            presenter.present(new SearchPlacesOutputData(
                    List.of(), null, false, e.getMessage()
            ));
        }
    }

    private void rankPlaces(List<Place> places, List<Interest> interests, WeatherSummary weather) {
        boolean wet = weather != null && weather.isPrecipitationLikely();
        places.sort(Comparator.comparingDouble((Place p) -> -scorePlace(p, interests, wet)));
    }

    private double scorePlace(Place place, List<Interest> interests, boolean wet) {
        double score = 0;
        if (interests != null) {
            for (Interest i : interests) {
                if (place.getCategories().contains(i)) {
                    score += 10;
                }
            }
        }
        if (wet && place.getIndoorOutdoorType() == IndoorOutdoorType.INDOOR) {
            score += 5;
        }
        if (!wet && place.getIndoorOutdoorType() == IndoorOutdoorType.OUTDOOR) {
            score += 5;
        }
        score -= place.getDistanceKm();
        return score;
    }
}
