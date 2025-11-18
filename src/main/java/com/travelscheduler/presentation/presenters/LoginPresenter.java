package com.travelscheduler.presentation.presenters;

import com.travelscheduler.domain.entities.User;
import com.travelscheduler.presentation.views.LoginView;
import com.travelscheduler.domain.usecases.user.AuthenticateUserUseCase;
import com.travelscheduler.domain.common.Result;

public class LoginPresenter {
    private final LoginView view;
    private final AuthenticateUserUseCase authenticateUseCase;

    public LoginPresenter(LoginView view, AuthenticateUserUseCase authenticateUseCase) {
        this.view = view;
        this.authenticateUseCase = authenticateUseCase;
    }

    public void onLoginClicked(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            view.showError("Please enter your email");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            view.showError("Please enter your password");
            return;
        }

        view.showLoading();

        new Thread(() -> {
            try {
                Result result = authenticateUseCase.execute(email, password);

                javax.swing.SwingUtilities.invokeLater(() -> {
                    view.hideLoading();

                    if (result.isSuccess()) {
                        view.showSuccess("Login successful!");
                        view.navigateToMainDashboard((User)result.getData());
                    } else {
                        view.showError(result.getError());
                        view.clearForm();
                    }
                });
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    view.hideLoading();
                    view.showError("Login failed: " + e.getMessage());
                });
            }
        }).start();
    }

    public void onRegisterClicked() {
        view.navigateToRegistration();
    }
}