package placefinder.frameworks_drivers.database;

import placefinder.entities.FavoriteLocation;
import placefinder.entities.Interest;
import placefinder.entities.PreferenceProfile;
import placefinder.usecases.ports.PreferenceGateway;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlitePreferenceGatewayImpl implements PreferenceGateway {

    @Override
    public PreferenceProfile loadForUser(int userId) throws Exception {
        String sql = "SELECT radius_km, interests FROM preferences WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double radius = rs.getDouble("radius_km");
                    String interestsStr = rs.getString("interests");
                    List<Interest> interests = parseInterests(interestsStr);
                    return new PreferenceProfile(userId, radius, interests);
                }
            }
        }

        // create default if not exists
        PreferenceProfile profile = new PreferenceProfile(userId, 2.0, new ArrayList<>());
        saveForUser(profile);
        return profile;
    }

    @Override
    public void saveForUser(PreferenceProfile profile) throws Exception {
        String update = "UPDATE preferences SET radius_km = ?, interests = ? WHERE user_id = ?";
        String insert = "INSERT INTO preferences(user_id, radius_km, interests) VALUES (?, ?, ?)";

        String interestsStr = serializeInterests(profile.getInterests());
        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setDouble(1, profile.getRadiusKm());
                ps.setString(2, interestsStr);
                ps.setInt(3, profile.getUserId());
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    try (PreparedStatement ins = conn.prepareStatement(insert)) {
                        ins.setInt(1, profile.getUserId());
                        ins.setDouble(2, profile.getRadiusKm());
                        ins.setString(3, interestsStr);
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    @Override
    public List<FavoriteLocation> listFavorites(int userId) throws Exception {
        String sql = "SELECT id, user_id, name, address, lat, lon FROM favorite_locations WHERE user_id = ? ORDER BY id";
        List<FavoriteLocation> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FavoriteLocation(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getDouble("lat"),
                            rs.getDouble("lon")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public FavoriteLocation addFavorite(int userId, String name, String address, double lat, double lon) throws Exception {
        String sql = "INSERT INTO favorite_locations(user_id, name, address, lat, lon) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, address);
            ps.setDouble(4, lat);
            ps.setDouble(5, lon);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new FavoriteLocation(id, userId, name, address, lat, lon);
                }
            }
        }
        return null;
    }

    @Override
    public void deleteFavorite(int favoriteId, int userId) throws Exception {
        String sql = "DELETE FROM favorite_locations WHERE id = ? AND user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, favoriteId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private List<Interest> parseInterests(String interestsStr) {
        List<Interest> result = new ArrayList<>();
        if (interestsStr == null || interestsStr.isBlank()) {
            return result;
        }
        Arrays.stream(interestsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(s -> {
                    try {
                        result.add(Interest.valueOf(s));
                    } catch (IllegalArgumentException ignored) {
                    }
                });
        return result;
    }

    private String serializeInterests(List<Interest> interests) {
        if (interests == null || interests.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Interest i : interests) {
            if (sb.length() > 0) sb.append(",");
            sb.append(i.name());
        }
        return sb.toString();
    }
}
