package com.travelscheduler.presentation.presenters;

import com.travelscheduler.presentation.views.PreferenceView;
import com.travelscheduler.domain.usecases.user.UpdateUserPreferencesUseCase;
import com.travelscheduler.domain.entities.PreferenceProfile;
import com.travelscheduler.domain.common.Result;

public class PreferencePresenter {
    private final PreferenceView view;
    private final UpdateUserPreferencesUseCase updatePreferencesUseCase;

    public PreferencePresenter(PreferenceView view, UpdateUserPreferencesUseCase updatePreferencesUseCase) {
        this.view = view;
        this.updatePreferencesUseCase = updatePreferencesUseCase;
    }

    public void onSavePreferences(String email, PreferenceProfile profile) {
        view.showLoading();

        new Thread(() -> {
            try {
                Result<Boolean> result = updatePreferencesUseCase.execute(email, profile);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    view.hideLoading();

                    if (result.isSuccess() && result.getData()) {
                        view.updateUserProfile(profile);
                        view.showSuccess("Preferences saved successfully!");
                    } else {
                        view.showError(result.getError());
                    }
                });
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    view.hideLoading();
                    view.showError("Failed to save preferences: " + e.getMessage());
                });
            }
        }).start();
    }
}