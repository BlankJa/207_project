package placefinder.usecases.ports;

import placefinder.entities.GeocodeResult;
import placefinder.entities.Place;
import placefinder.entities.Route;

import java.util.List;

public interface RouteGateway {
    Route computeRoute(GeocodeResult origin, List<Place> stops) throws Exception;
}
