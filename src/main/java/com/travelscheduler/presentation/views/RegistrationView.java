package com.travelscheduler.presentation.views;

import com.travelscheduler.domain.entities.User;

public interface RegistrationView {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void showSuccess(String message);
    void navigateToLogin(User user);
    void clearForm();
}