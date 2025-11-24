package placefinder.frameworks_drivers.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A service for fetching and cleaning up place data from the Geoapify API.
 *
 * SOLID PRINCIPLES DEMONSTRATED:
 *
 * 1. Single Responsibility Principle (SRP):
 *    - This class has ONE responsibility: retrieving cleaned place data
 *    - URL building is delegated to UrlBuilder
 *    - JSON parsing is delegated to PlaceJsonParser
 *    - Data validation/cleanup is delegated to PlaceDataCleaner
 *    - Category classification is delegated to CategoryClassifier
 *
 * 2. Open/Closed Principle (OCP):
 *    - Open for extension: Can extend PlaceDataCleaner
 *    - Closed for modification: Core logic doesn't need to change to add new features
 *
 * 3. Liskov Substitution Principle (LSP):
 *    - PlaceDataCleaner subclasses can be substituted without breaking behavior
 *    - All inner classes follow contracts that allow substitution
 *
 * 4. Interface Segregation Principle (ISP):
 *    - Each inner class has a focused, minimal interface
 *
 * 5. Dependency Inversion Principle (DIP):
 *    - Depends on abstractions (HttpClient interface) rather than concrete implementations
 *    - High-level policy (place retrieval) is separated from low-level details (HTTP calls)
 */
public class GeoapifyPlacesService {

    private final String apiKey;
    private final HttpClient httpClient;
    private final UrlBuilder urlBuilder;
    private final PlaceJsonParser jsonParser;
    private final PlaceDataCleaner dataCleaner;

    /**
     * Creates a new service with the specified API key.
     * Uses default HTTP client implementation.
     *
     * @param apiKey The Geoapify API key
     */
    public GeoapifyPlacesService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new DefaultHttpClient(); // DIP: Depends on interface, not implementation
        this.urlBuilder = new UrlBuilder();
        this.jsonParser = new PlaceJsonParser();
        this.dataCleaner = new PlaceDataCleaner();
    }

    /**
     * Demonstrates Dependency Inversion Principle.
     *
     * @param apiKey The Geoapify API key
     * @param httpClient The HTTP client implementation to use
     */
    public GeoapifyPlacesService(String apiKey, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.urlBuilder = new UrlBuilder();
        this.jsonParser = new PlaceJsonParser();
        this.dataCleaner = new PlaceDataCleaner();
    }

    /**
     * Searches for places near the specified coordinates.
     *
     * @param latitude The latitude of the search center
     * @param longitude The longitude of the search center
     * @param radiusKm The search radius in kilometers
     * @param categories Optional list of category filters (e.g., "tourism.sights", "catering.restaurant")
     * @return List of cleaned and validated place data
     * @throws Exception if the API call fails
     */
    public List<CleanPlace> searchPlaces(double latitude, double longitude,
                                         double radiusKm, List<String> categories) throws Exception {

        // Build the API URL (SRP: URL building is separated)
        String url = urlBuilder.buildSearchUrl(latitude, longitude, radiusKm, categories, apiKey);

        // Make HTTP request (DIP: Using interface, not concrete implementation)
        String jsonResponse = httpClient.get(url);

        // Parse JSON response (SRP: Parsing is separated)
        List<RawPlace> rawPlaces = jsonParser.parse(jsonResponse);

        // Clean and validate data (SRP: Data cleaning is separated)
        return dataCleaner.cleanPlaces(rawPlaces);
    }

    /**
     * Simplified search method with default categories.
     *
     * @param latitude The latitude of the search center
     * @param longitude The longitude of the search center
     * @param radiusKm The search radius in kilometers
     * @return List of cleaned place data
     * @throws Exception if the API call fails
     */
    public List<CleanPlace> searchPlaces(double latitude, double longitude, double radiusKm) throws Exception {
        List<String> defaultCategories = Arrays.asList(
            "tourism.sights", "entertainment", "leisure.park", "catering"
        );
        return searchPlaces(latitude, longitude, radiusKm, defaultCategories);
    }

    // ==================== INNER CLASSES DEMONSTRATING SOLID PRINCIPLES ====================

    /**
     * Interface for HTTP client operations.
     *
     * SOLID: Dependency Inversion Principle (DIP)
     * - Abstracts away HTTP implementation details
     * - Enables testing with mock implementations
     *
     * SOLID: Interface Segregation Principle (ISP)
     * - Minimal interface with only required method
     */
    public interface HttpClient {
        String get(String url) throws Exception;
    }

    /**
     * Default implementation using existing HttpUtil.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for making HTTP GET requests
     * - Delegates to existing utility class
     */
    private static class DefaultHttpClient implements HttpClient {
        @Override
        public String get(String url) throws Exception {
            return HttpUtil.get(url);
        }
    }

    /**
     * Builds Geoapify API URLs.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for constructing valid API URLs
     *
     * SOLID: Open/Closed Principle (OCP)
     * - Can be extended to support additional query parameters
     * - Existing code doesn't need modification to add new features
     */
    private static class UrlBuilder {
        private static final String BASE_URL = "https://api.geoapify.com/v2/places";

        public String buildSearchUrl(double lat, double lon, double radiusKm,
                                     List<String> categories, String apiKey) {
            double radiusMeters = radiusKm * 1000.0;
            String categoriesParam = buildCategoriesParam(categories);

            return String.format("%s?categories=%s&filter=circle:%f,%f,%d&bias=proximity:%f,%f&limit=40&apiKey=%s",
                BASE_URL, categoriesParam, lon, lat, (int) radiusMeters, lon, lat, apiKey);
        }

        private String buildCategoriesParam(List<String> categories) {
            if (categories == null || categories.isEmpty()) {
                return "tourism.sights,entertainment,leisure.park,catering";
            }
            return String.join(",", categories);
        }
    }

    /**
     * Raw place data from API before cleaning.
     * Simple data holder with no business logic.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for holding raw API data
     */
    private static class RawPlace {
        String id;
        String name;
        String address;
        double latitude;
        double longitude;
        double distanceMeters;
        List<String> categories;

        RawPlace(String id, String name, String address, double latitude,
                double longitude, double distanceMeters, List<String> categories) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distanceMeters = distanceMeters;
            this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<>();
        }
    }

    /**
     * Cleaned and validated place data for client use.
     * Immutable class with validated data.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for representing valid place data
     * - Immutable to prevent inconsistent state
     */
    public static class CleanPlace {
        private final String id;
        private final String name;
        private final String address;
        private final double latitude;
        private final double longitude;
        private final double distanceKm;
        private final String primaryCategory;
        private final List<String> allCategories;
        private final PlaceType placeType;

        public CleanPlace(String id, String name, String address, double latitude,
                         double longitude, double distanceKm, String primaryCategory,
                         List<String> allCategories, PlaceType placeType) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distanceKm = distanceKm;
            this.primaryCategory = primaryCategory;
            this.allCategories = Collections.unmodifiableList(new ArrayList<>(allCategories));
            this.placeType = placeType;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getDistanceKm() { return distanceKm; }
        public String getPrimaryCategory() { return primaryCategory; }
        public List<String> getAllCategories() { return allCategories; }
        public PlaceType getPlaceType() { return placeType; }

        @Override
        public String toString() {
            return String.format("%s (%s) - %.2f km away [%s]",
                name, primaryCategory, distanceKm, placeType);
        }
    }

    /**
     * Enum for place types based on indoor/outdoor classification.
     *
     * SOLID: Open/Closed Principle (OCP)
     * - Easy to add new place types without modifying existing code
     */
    public enum PlaceType {
        INDOOR,
        OUTDOOR,
        MIXED,
        UNKNOWN
    }

    /**
     * Parses JSON responses from Geoapify API.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for parsing JSON into RawPlace objects
     *
     * SOLID: Open/Closed Principle (OCP)
     * - Can be extended to parse additional fields
     * - Core logic doesn't need to change
     */
    private static class PlaceJsonParser {

        public List<RawPlace> parse(String jsonResponse) {
            List<RawPlace> places = new ArrayList<>();

            try {
                JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
                JsonArray features = root.getAsJsonArray("features");

                if (features == null) {
                    return places;
                }

                for (JsonElement featureEl : features) {
                    RawPlace place = parseFeature(featureEl.getAsJsonObject());
                    if (place != null) {
                        places.add(place);
                    }
                }
            } catch (Exception e) {
                // Log error but return whatever places we could parse
                System.err.println("Error parsing JSON: " + e.getMessage());
            }

            return places;
        }

        private RawPlace parseFeature(JsonObject feature) {
            JsonObject props = feature.getAsJsonObject("properties");
            if (props == null) {
                return null;
            }

            // Extract categories
            List<String> categories = extractCategories(props);

            // Extract basic info
            String id = getStringOrNull(props, "place_id");
            String name = getStringOrNull(props, "name");
            String address = getStringOrNull(props, "formatted");

            // Extract coordinates
            double lat = extractLatitude(feature, props);
            double lon = extractLongitude(feature, props);

            // Extract distance
            double distanceMeters = props.has("distance") ?
                props.get("distance").getAsDouble() : 0.0;

            return new RawPlace(id, name, address, lat, lon, distanceMeters, categories);
        }

        private List<String> extractCategories(JsonObject props) {
            List<String> categories = new ArrayList<>();

            if (props.has("categories") && props.get("categories").isJsonArray()) {
                for (JsonElement c : props.getAsJsonArray("categories")) {
                    categories.add(c.getAsString());
                }
            } else if (props.has("category")) {
                categories.add(props.get("category").getAsString());
            }

            return categories;
        }

        private double extractLatitude(JsonObject feature, JsonObject props) {
            if (props.has("lat")) {
                return props.get("lat").getAsDouble();
            }
            return feature.getAsJsonObject("geometry")
                .getAsJsonArray("coordinates").get(1).getAsDouble();
        }

        private double extractLongitude(JsonObject feature, JsonObject props) {
            if (props.has("lon")) {
                return props.get("lon").getAsDouble();
            }
            return feature.getAsJsonObject("geometry")
                .getAsJsonArray("coordinates").get(0).getAsDouble();
        }

        private String getStringOrNull(JsonObject obj, String key) {
            return obj.has(key) ? obj.get(key).getAsString() : null;
        }
    }

    /**
     * Cleans and validates place data.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for cleaning and validating data
     *
     * SOLID: Open/Closed Principle (OCP)
     * - Can be extended with additional cleaning rules
     */
    private static class PlaceDataCleaner {
        private final CategoryClassifier classifier;

        public PlaceDataCleaner() {
            this.classifier = new CategoryClassifier();
        }

        public List<CleanPlace> cleanPlaces(List<RawPlace> rawPlaces) {
            return rawPlaces.stream()
                .map(this::cleanPlace)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        private CleanPlace cleanPlace(RawPlace raw) {
            // Skip places with invalid coordinates
            if (!isValidCoordinate(raw.latitude, raw.longitude)) {
                return null;
            }

            // Clean name
            String cleanName = cleanName(raw.name, raw.categories);

            // Clean address
            String cleanAddress = cleanAddress(raw.address);

            // Calculate distance in km
            double distanceKm = raw.distanceMeters / 1000.0;

            // Determine primary category
            String primaryCategory = classifier.getPrimaryCategory(raw.categories);

            // Classify place type
            PlaceType placeType = classifier.classifyPlaceType(raw.categories);

            return new CleanPlace(
                raw.id != null ? raw.id : generateId(raw),
                cleanName,
                cleanAddress,
                raw.latitude,
                raw.longitude,
                distanceKm,
                primaryCategory,
                raw.categories,
                placeType
            );
        }

        private boolean isValidCoordinate(double lat, double lon) {
            return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
        }

        private String cleanName(String name, List<String> categories) {
            if (name != null && !name.trim().isEmpty()) {
                return name.trim();
            }

            // Generate name from category if missing
            if (!categories.isEmpty()) {
                return formatCategoryAsName(categories.get(0));
            }

            return "Unknown Place";
        }

        private String cleanAddress(String address) {
            if (address == null || address.trim().isEmpty()) {
                return "Address not available";
            }
            return address.trim();
        }

        private String formatCategoryAsName(String category) {
            // Convert "tourism.sights" to "Tourism Sight"
            String[] parts = category.split("\\.");
            return Arrays.stream(parts)
                .map(this::capitalize)
                .collect(Collectors.joining(" "));
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }

        private String generateId(RawPlace raw) {
            // Generate a unique ID based on coordinates if none provided
            return String.format("place_%f_%f", raw.latitude, raw.longitude);
        }
    }

    /**
     * Classifies categories and determines place types.
     *
     * SOLID: Single Responsibility Principle (SRP)
     * - Only responsible for category-related logic
     *
     * SOLID: Open/Closed Principle (OCP)
     * - Easy to add new category classifications
     * - Easy to add new place type rules
     */
    private static class CategoryClassifier {

        private static final Set<String> OUTDOOR_PREFIXES = new HashSet<>(Arrays.asList(
            "leisure.park", "natural", "tourism.attraction.animal"
        ));

        private static final Set<String> INDOOR_PREFIXES = new HashSet<>(Arrays.asList(
            "catering", "commercial", "entertainment.museum",
            "entertainment.cinema", "entertainment.culture"
        ));

        public String getPrimaryCategory(List<String> categories) {
            if (categories == null || categories.isEmpty()) {
                return "general";
            }

            // Return the first category, formatted nicely
            String primary = categories.get(0);
            return formatCategory(primary);
        }

        public PlaceType classifyPlaceType(List<String> categories) {
            if (categories == null || categories.isEmpty()) {
                return PlaceType.UNKNOWN;
            }

            boolean hasOutdoor = categories.stream()
                .anyMatch(this::isOutdoorCategory);

            boolean hasIndoor = categories.stream()
                .anyMatch(this::isIndoorCategory);

            if (hasOutdoor && hasIndoor) {
                return PlaceType.MIXED;
            } else if (hasOutdoor) {
                return PlaceType.OUTDOOR;
            } else if (hasIndoor) {
                return PlaceType.INDOOR;
            } else {
                return PlaceType.UNKNOWN;
            }
        }

        private boolean isOutdoorCategory(String category) {
            return OUTDOOR_PREFIXES.stream()
                .anyMatch(category::startsWith);
        }

        private boolean isIndoorCategory(String category) {
            return INDOOR_PREFIXES.stream()
                .anyMatch(category::startsWith);
        }

        private String formatCategory(String category) {
            // Convert "tourism.sights" to "Tourism - Sights"
            String[] parts = category.split("\\.", 2);
            if (parts.length == 2) {
                return capitalize(parts[0]) + " - " + capitalize(parts[1]);
            }
            return capitalize(category);
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }
    }

    // ==================== EXAMPLE USAGE ====================

    /**
     * Example usage of the GeoapifyPlacesService.
     */
    public static void main(String[] args) {
        try {
            // Create service with API key
            String apiKey = "6ae086fd1fea4e28bccba1e1c91dfe13";
            GeoapifyPlacesService service = new GeoapifyPlacesService(apiKey);

            // Search for places in Toronto
            double latitude = 43.6532;
            double longitude = -79.3832;
            double radiusKm = 5.0;

            System.out.println("Searching for places near Toronto...\n");
            List<CleanPlace> places = service.searchPlaces(latitude, longitude, radiusKm);

            // Display results
            System.out.println("Found " + places.size() + " places:\n");
            for (int i = 0; i < Math.min(10, places.size()); i++) {
                CleanPlace place = places.get(i);
                System.out.println((i + 1) + ". " + place);
                System.out.println("   Address: " + place.getAddress());
                System.out.println("   Categories: " + place.getAllCategories());
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}