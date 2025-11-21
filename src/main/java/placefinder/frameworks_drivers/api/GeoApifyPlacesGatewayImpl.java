package placefinder.frameworks_drivers.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import placefinder.entities.IndoorOutdoorType;
import placefinder.entities.Interest;
import placefinder.entities.Place;
import placefinder.usecases.ports.PlacesGateway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoApifyPlacesGatewayImpl implements PlacesGateway {

    private final String apiKey;

    public GeoApifyPlacesGatewayImpl() {
        this.apiKey = "75be457789934a199ed4014ad24925ba";
    }

    public GeoApifyPlacesGatewayImpl(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Place> searchPlaces(double lat, double lon, double radiusKm,
                                    List<Interest> interests) throws Exception {

        double radiusMeters = radiusKm * 1000.0;
        String categoriesParam = buildCategoriesParam(interests);

        String url = "https://api.geoapify.com/v2/places?categories=" + categoriesParam +
                "&filter=circle:" + lon + "," + lat + "," + (int) radiusMeters +
                "&bias=proximity:" + lon + "," + lat +
                "&limit=40&apiKey=" + apiKey;

        String json = HttpUtil.get(url);
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray features = root.getAsJsonArray("features");
        List<Place> places = new ArrayList<>();
        if (features == null) return places;

        for (JsonElement featureEl : features) {
            JsonObject feature = featureEl.getAsJsonObject();
            JsonObject props = feature.getAsJsonObject("properties");
            if (props == null) continue;

            String id = props.has("place_id") ? props.get("place_id").getAsString() : null;
            String name = props.has("name") ? props.get("name").getAsString() : "(no name)";
            String address = props.has("formatted") ? props.get("formatted").getAsString() : "";
            double plat = props.has("lat") ? props.get("lat").getAsDouble()
                    : feature.getAsJsonObject("geometry")
                            .getAsJsonArray("coordinates").get(1).getAsDouble();
            double plon = props.has("lon") ? props.get("lon").getAsDouble()
                    : feature.getAsJsonObject("geometry")
                            .getAsJsonArray("coordinates").get(0).getAsDouble();
            double distanceKm = props.has("distance") ? props.get("distance").getAsDouble() / 1000.0 : 0.0;

            List<String> catStrings = new ArrayList<>();
            if (props.has("categories") && props.get("categories").isJsonArray()) {
                for (JsonElement c : props.getAsJsonArray("categories")) {
                    catStrings.add(c.getAsString());
                }
            } else if (props.has("category")) {
                catStrings.add(props.get("category").getAsString());
            }

            List<Interest> mappedInterests = mapCategoriesToInterests(catStrings);
            IndoorOutdoorType type = classifyIndoorOutdoor(catStrings);

            Place place = new Place();
            place.setId(id);
            place.setName(name);
            place.setAddress(address);
            place.setLat(plat);
            place.setLon(plon);
            place.setDistanceKm(distanceKm);
            place.setIndoorOutdoorType(type);
            place.setCategories(mappedInterests);
            places.add(place);
        }

        return places;
    }

    private String buildCategoriesParam(List<Interest> interests) {
        if (interests == null || interests.isEmpty()) {
            return "tourism.sights,entertainment,leisure.park,catering";
        }
        List<String> list = new ArrayList<>();
        for (Interest i : interests) {
            switch (i) {
                case MUSEUM -> list.add("entertainment.museum");
                case CAFE -> list.add("catering.cafe");
                case PARK -> list.add("leisure.park");
                case SHOPPING -> {
                    list.add("commercial.shopping_mall");
                    list.add("commercial.marketplace");
                }
                case RESTAURANT -> list.add("catering.restaurant");
                case BAR -> list.add("catering.bar");
                case SIGHTSEEING -> list.add("tourism.sights");
                default -> list.add("tourism");
            }
        }
        return list.stream().distinct().collect(Collectors.joining(","));
    }

    private List<Interest> mapCategoriesToInterests(List<String> catStrings) {
        Set<Interest> set = new HashSet<>();
        for (String c : catStrings) {
            if (c.startsWith("entertainment.museum")) {
                set.add(Interest.MUSEUM);
            }
            if (c.startsWith("catering.cafe")) {
                set.add(Interest.CAFE);
            }
            if (c.startsWith("leisure.park")) {
                set.add(Interest.PARK);
            }
            if (c.startsWith("commercial.shopping_mall") ||
                c.startsWith("commercial.marketplace")) {
                set.add(Interest.SHOPPING);
            }
            if (c.startsWith("catering.restaurant")) {
                set.add(Interest.RESTAURANT);
            }
            if (c.startsWith("catering.bar") || c.startsWith("catering.pub")) {
                set.add(Interest.BAR);
            }
            if (c.startsWith("tourism.sights") || c.startsWith("tourism.attraction")) {
                set.add(Interest.SIGHTSEEING);
            }
        }
        return new ArrayList<>(set);
    }

    private IndoorOutdoorType classifyIndoorOutdoor(List<String> catStrings) {
        boolean hasPark = false;
        boolean hasIndoor = false;
        for (String c : catStrings) {
            if (c.startsWith("leisure.park") || c.startsWith("natural")) {
                hasPark = true;
            }
            if (c.startsWith("catering.") || c.startsWith("commercial.") || c.startsWith("entertainment.")) {
                hasIndoor = true;
            }
        }
        if (hasPark && !hasIndoor) return IndoorOutdoorType.OUTDOOR;
        if (hasIndoor && !hasPark) return IndoorOutdoorType.INDOOR;
        if (hasIndoor && hasPark) return IndoorOutdoorType.MIXED;
        return IndoorOutdoorType.MIXED;
    }
}
