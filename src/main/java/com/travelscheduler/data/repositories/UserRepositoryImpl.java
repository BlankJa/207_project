package com.travelscheduler.data.repositories;

import com.travelscheduler.data.models.UserEntity;
import com.travelscheduler.domain.entities.User;
import com.travelscheduler.domain.entities.PreferenceProfile;
import com.travelscheduler.domain.repositories.UserRepository;
import com.travelscheduler.data.datasources.local.UserLocalDataSource;
import com.travelscheduler.data.datasources.local.PreferenceLocalDataSource;
import com.travelscheduler.data.mappers.UserMapper;
import com.travelscheduler.data.mappers.PreferenceMapper;

public class UserRepositoryImpl implements UserRepository {
    private final UserLocalDataSource userLocalDataSource;
    private final PreferenceLocalDataSource preferenceLocalDataSource;
    private final UserMapper userMapper;
    private final PreferenceMapper preferenceMapper;

    public UserRepositoryImpl(UserLocalDataSource userLocalDataSource, PreferenceLocalDataSource preferenceLocalDataSource) {
        this.userLocalDataSource = userLocalDataSource;
        this.preferenceLocalDataSource = preferenceLocalDataSource;
        this.userMapper = new UserMapper();
        this.preferenceMapper = new PreferenceMapper();
    }

    @Override
    public User authenticate(String email, String password) {
        var userEntity = userLocalDataSource.authenticate(email, password);
        if (userEntity == null) {
            return null;
        }

        User user = userMapper.toDomain(userEntity);
        // Load user preferences
        var preferenceEntity = preferenceLocalDataSource.getUserPreferences(userEntity.getId());
        if (preferenceEntity != null) {
            user.setProfile(preferenceMapper.toDomain(preferenceEntity));
        }

        return user;
    }

    @Override
    public User createUser(User user) {
        var userEntity = userMapper.toData(user);
        var createdEntity = userLocalDataSource.createUser(userEntity);

        if (createdEntity != null) {
            // Create default preferences for new user
            preferenceLocalDataSource.createDefaultPreferences(createdEntity.getId());
            return userMapper.toDomain(createdEntity);
        }
        return null;
    }

    @Override
    public boolean userExists(String email) {
        return userLocalDataSource.userExists(email);
    }

    @Override
    public boolean updateUserPreferences(String email, PreferenceProfile profile) {
        return userLocalDataSource.updateUserPreferences(email, profile);
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = userLocalDataSource.getUserByEmail(email);
        return userMapper.toDomain(userEntity);
    }
}