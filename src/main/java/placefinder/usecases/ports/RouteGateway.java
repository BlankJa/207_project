package placefinder.usecases.ports;

import placefinder.entities.GeocodeResult;
import placefinder.entities.PlanStop;
import placefinder.entities.Route;

import java.time.LocalTime;
import java.util.List;

public interface RouteGateway {
    Route computeRoute(GeocodeResult origin, LocalTime startTime, List<PlanStop> stops) throws Exception;
}
