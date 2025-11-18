package com.travelscheduler.presentation.views;

import com.travelscheduler.domain.entities.PreferenceProfile;

public interface PreferenceView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showSuccess(String message);
    void updateUserProfile(PreferenceProfile profile);
}