package com.travelscheduler.di;

import com.travelscheduler.data.datasources.local.*;
import com.travelscheduler.data.repositories.*;
import com.travelscheduler.domain.repositories.*;
import com.travelscheduler.domain.usecases.user.*;
import com.travelscheduler.presentation.presenters.*;
import com.travelscheduler.presentation.swing.utils.SwingWorkerHelper;
import com.travelscheduler.presentation.swing.utils.UIHelper;
import com.travelscheduler.presentation.views.*;
import com.travelscheduler.presentation.swing.frames.*;

public class DependencyContainer {
    private static DependencyContainer instance;

    // Core components
    private DatabaseManager databaseManager;

    // Data sources
    private UserLocalDataSource userLocalDataSource;
    private PreferenceLocalDataSource preferenceLocalDataSource;

    // Repositories
    private UserRepository userRepository;

    // Use cases
    private AuthenticateUserUseCase authenticateUserUseCase;
    private CreateUserUseCase createUserUseCase;
    private UpdateUserPreferencesUseCase updateUserPreferencesUseCase;

    private DependencyContainer() {
        initializeComponents();
    }

    public static DependencyContainer getInstance() {
        if (instance == null) {
            instance = new DependencyContainer();
        }
        return instance;
    }

    private void initializeComponents() {
        // Initialize core components
        databaseManager = DatabaseManager.getInstance();

        // Initialize data sources
        userLocalDataSource = new UserLocalDataSource(databaseManager);
        preferenceLocalDataSource = new PreferenceLocalDataSource(databaseManager);

        // Initialize repositories
        userRepository = new UserRepositoryImpl(userLocalDataSource, preferenceLocalDataSource);

        // Initialize use cases
        authenticateUserUseCase = new AuthenticateUserUseCase(userRepository);
        createUserUseCase = new CreateUserUseCase(userRepository);
        updateUserPreferencesUseCase = new UpdateUserPreferencesUseCase(userRepository);
    }

    // Provider methods for presentation layer
    public LoginPresenter provideLoginPresenter(LoginView view) {
        return new LoginPresenter(view, authenticateUserUseCase);
    }

    public RegistrationPresenter provideRegistrationPresenter(RegistrationView view) {
        return new RegistrationPresenter(view, createUserUseCase);
    }

    public UpdateUserPreferencesUseCase getUpdateUserPreferencesUseCase() {
        return updateUserPreferencesUseCase;
    }

    public MainDashboardPresenter provideMainDashboardPresenter(MainDashboardView view) {
        return new MainDashboardPresenter(view);
    }

    // Frame providers
    public LoginFrame provideLoginFrame() {
        return new LoginFrame(provideLoginPresenter(null));
    }

    // Cleanup method
    public void cleanup() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
    }

    // Getters for testing and other purposes
    public UserRepository getUserRepository() { 
        return userRepository; 
    }

    public UIHelper provideUIHelper() {
        return new UIHelper();
    }

    public SwingWorkerHelper provideSwingWorkerHelper() {
        return new SwingWorkerHelper();
    }
}
