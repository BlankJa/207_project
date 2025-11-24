package placefinder.frameworks_drivers.api;

import placefinder.entities.GeocodeResult;
import placefinder.entities.Place;
import placefinder.entities.Route;
import placefinder.usecases.ports.RouteGateway;

import java.util.List;

public class GeoApifyRouteGatewayImpl implements RouteGateway {
    private final String apiKey;

    public GeoApifyRouteGatewayImpl() {
        this.apiKey = "75be457789934a199ed4014ad24925ba";
    }

    public GeoApifyRouteGatewayImpl(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Route computeRoute(GeocodeResult origin, List<Place> stops) {

    }
}
