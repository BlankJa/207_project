package com.travelscheduler.data.mappers;

import com.travelscheduler.domain.entities.User;
import com.travelscheduler.data.models.UserEntity;

public class UserMapper {
    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        User user = new User();
        user.setUserName(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        return user;
    }

    public UserEntity toData(User domain) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity();
        entity.setUsername(domain.getUserName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        return entity;
    }
}