package com.travelscheduler.presentation.views;

public interface MainDashboardView {
    void showWelcomeMessage(String username);
    void showError(String message);
    void navigateToLogin();
    void openPreferenceFrame();
}