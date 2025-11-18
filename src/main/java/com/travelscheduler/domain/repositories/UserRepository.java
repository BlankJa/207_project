package com.travelscheduler.domain.repositories;

import com.travelscheduler.domain.entities.User;
import com.travelscheduler.domain.entities.PreferenceProfile;

public interface UserRepository {
    User authenticate(String email, String password);
    User createUser(User user);
    boolean userExists(String email);
    boolean updateUserPreferences(String email, PreferenceProfile profile);
    User getUserByEmail(String email);
}