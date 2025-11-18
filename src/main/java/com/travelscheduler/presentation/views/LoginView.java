package com.travelscheduler.presentation.views;

import com.travelscheduler.domain.entities.User;

public interface LoginView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showSuccess(String message);
    void navigateToMainDashboard(User user);
    void navigateToRegistration();
    void clearForm();
}