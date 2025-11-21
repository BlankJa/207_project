package placefinder.frameworks_drivers.ui.frames;

import placefinder.entities.Plan;
import placefinder.interface_adapters.controllers.*;
import placefinder.interface_adapters.viewmodels.*;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    // Controllers
    private final LoginController loginController;
    private final RegisterController registerController;
    private final PreferencesController preferencesController;
    private final PlanCreationController planCreationController;
    private final DashboardController dashboardController;
    private final WeatherAdviceController weatherAdviceController;

    // ViewModels
    private final LoginViewModel loginVM;
    private final RegisterViewModel registerVM;
    private final PreferencesViewModel preferencesVM;
    private final PlanCreationViewModel planCreationVM;
    private final DashboardViewModel dashboardVM;
    private final PlanDetailsViewModel planDetailsVM;
    private final WeatherAdviceViewModel weatherAdviceVM;

    // Current user
    private Integer currentUserId = null;
    private String currentUserName = null;

    // Card layout for screens
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Screen names
    public static final String CARD_LOGIN = "login";
    public static final String CARD_REGISTER = "register";
    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_PREFERENCES = "preferences";
    public static final String CARD_PLAN = "plan";
    public static final String CARD_WEATHER = "weather";
    public static final String CARD_PLAN_DETAILS = "planDetails";

    // Panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private PreferencesPanel preferencesPanel;
    private PlanBuilderPanel planBuilderPanel;
    private WeatherAdvicePanel weatherAdvicePanel;
    private PlanDetailsPanel planDetailsPanel;

    public AppFrame(
            LoginController loginController,
            RegisterController registerController,
            PreferencesController preferencesController,
            PlanCreationController planCreationController,
            DashboardController dashboardController,
            WeatherAdviceController weatherAdviceController,
            LoginViewModel loginVM,
            RegisterViewModel registerVM,
            PreferencesViewModel preferencesVM,
            PlanCreationViewModel planCreationVM,
            DashboardViewModel dashboardVM,
            PlanDetailsViewModel planDetailsVM,
            WeatherAdviceViewModel weatherAdviceVM
    ) {
        super("PlaceFinder");

        this.loginController = loginController;
        this.registerController = registerController;
        this.preferencesController = preferencesController;
        this.planCreationController = planCreationController;
        this.dashboardController = dashboardController;
        this.weatherAdviceController = weatherAdviceController;

        this.loginVM = loginVM;
        this.registerVM = registerVM;
        this.preferencesVM = preferencesVM;
        this.planCreationVM = planCreationVM;
        this.dashboardVM = dashboardVM;
        this.planDetailsVM = planDetailsVM;
        this.weatherAdviceVM = weatherAdviceVM;

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this, loginController, loginVM);
        registerPanel = new RegisterPanel(this, registerController, registerVM);
        dashboardPanel = new DashboardPanel(this, dashboardController, dashboardVM, planDetailsVM);
        preferencesPanel = new PreferencesPanel(this, preferencesController, preferencesVM);
        planBuilderPanel = new PlanBuilderPanel(this, planCreationController, planCreationVM);
        weatherAdvicePanel = new WeatherAdvicePanel(this, weatherAdviceController, weatherAdviceVM);
        planDetailsPanel = new PlanDetailsPanel( dashboardController, dashboardVM, planDetailsVM, this);

        mainPanel.add(loginPanel, CARD_LOGIN);
        mainPanel.add(registerPanel, CARD_REGISTER);
        mainPanel.add(dashboardPanel, CARD_DASHBOARD);
        mainPanel.add(preferencesPanel, CARD_PREFERENCES);
        mainPanel.add(planBuilderPanel, CARD_PLAN);
        mainPanel.add(weatherAdvicePanel, CARD_WEATHER);
        mainPanel.add(planDetailsPanel, CARD_PLAN_DETAILS);

        setContentPane(mainPanel);
        showLogin();
    }

    void showCard(String card) {
        cardLayout.show(mainPanel, card);
    }

    // ===== Navigation helpers =====

    public void showLogin() {
        showCard(CARD_LOGIN);
    }

    public void showRegister() {
        showCard(CARD_REGISTER);
    }

    public void showDashboard() {
        if (currentUserId != null) {
            dashboardPanel.refreshPlans();
        }
        showCard(CARD_DASHBOARD);
    }

    public void showPreferences() {
        preferencesPanel.loadForCurrentUser();
        showCard(CARD_PREFERENCES);
    }

    public void showNewPlan() {
        planBuilderPanel.setupForNewPlan();
        showCard(CARD_PLAN);
    }

    public void showWeatherAdvice() {
        weatherAdvicePanel.resetFields();
        showCard(CARD_WEATHER);
    }

    public void showPlanDetails() {
        planDetailsPanel.showFromViewModel();
        showCard(CARD_PLAN_DETAILS);
    }

    public void openPlanEditorWithPlan(Plan plan) {
        planBuilderPanel.editExistingPlan(plan);
        showCard(CARD_PLAN);
    }

    // ===== Session management =====

    public void onLoginSuccess() {
        if (loginVM.getLoggedInUser() != null) {
            currentUserId = loginVM.getLoggedInUser().getId();
            currentUserName = loginVM.getLoggedInUser().getName();
            showDashboard();
        }
    }

    public void logout() {
        currentUserId = null;
        currentUserName = null;
        loginVM.setLoggedInUser(null);
        loginVM.setErrorMessage(null);
        showLogin();
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }
}
