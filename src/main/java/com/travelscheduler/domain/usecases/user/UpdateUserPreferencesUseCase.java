package com.travelscheduler.domain.usecases.user;

import com.travelscheduler.domain.entities.PreferenceProfile;
import com.travelscheduler.domain.repositories.UserRepository;
import com.travelscheduler.domain.common.Result;

public class UpdateUserPreferencesUseCase {
    private final UserRepository userRepository;

    public UpdateUserPreferencesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result<Boolean> execute(String email, PreferenceProfile profile) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return Result.error("Email cannot be empty");
            }
            if (profile == null) {
                return Result.error("Profile cannot be null");
            }

            boolean success = userRepository.updateUserPreferences(email, profile);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("Failed to update preferences: " + e.getMessage());
        }
    }
}