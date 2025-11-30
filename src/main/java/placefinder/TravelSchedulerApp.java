package placefinder;

import javax.swing.SwingUtilities;

import placefinder.frameworks_drivers.api.GoogleMapsRouteGatewayImpl;
import placefinder.frameworks_drivers.database.Database;
import placefinder.frameworks_drivers.database.SqliteUserGatewayImpl;
import placefinder.frameworks_drivers.database.SqlitePreferenceGatewayImpl;
import placefinder.frameworks_drivers.database.SqlitePlanGatewayImpl;

import placefinder.frameworks_drivers.api.OpenCageGeocodingGateway;
import placefinder.frameworks_drivers.api.GeoApifyPlacesGatewayImpl;
import placefinder.frameworks_drivers.api.OpenMeteoWeatherGatewayImpl;

import placefinder.usecases.computeroute.ComputeRouteInputBoundary;
import placefinder.usecases.computeroute.ComputeRouteInteractor;
import placefinder.usecases.computeroute.ComputeRouteOutputBoundary;
import placefinder.usecases.computeroute.ComputeRouteOutputData;
import placefinder.usecases.ports.*;

// login & register
import placefinder.usecases.login.*;
import placefinder.usecases.register.*;

// preferences
import placefinder.usecases.preferences.*;

// search places + build/save plan
import placefinder.usecases.searchplaces.*;
import placefinder.usecases.buildplan.*;
import placefinder.usecases.saveplan.*;

// plans list/details/delete/apply
import placefinder.usecases.plans.*;

// weather advice
import placefinder.usecases.weatheradvice.*;

// interface adapters
import placefinder.interface_adapters.controllers.*;
import placefinder.interface_adapters.viewmodels.*;

// UI (you implement this in frameworks_drivers.ui)
import placefinder.frameworks_drivers.ui.frames.AppFrame;

public class TravelSchedulerApp {

    public static void main(String[] args) {

        // Ensure database is initialized (triggers static init)
        try {
            Database.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ========== GATEWAYS (Frameworks & Drivers) ==========
        UserGateway userGateway = new SqliteUserGatewayImpl();
        PreferenceGateway preferenceGateway = new SqlitePreferenceGatewayImpl();
        PlanGateway planGateway = new SqlitePlanGatewayImpl();
        GeocodingGateway geocodingGateway = new OpenCageGeocodingGateway();
        PlacesGateway placesGateway = new GeoApifyPlacesGatewayImpl();
        RouteGateway routeGateway = new GoogleMapsRouteGatewayImpl();
        WeatherGateway weatherGateway = new OpenMeteoWeatherGatewayImpl();

        // ========== VIEW MODELS ==========
        LoginViewModel loginVM = new LoginViewModel();
        RegisterViewModel registerVM = new RegisterViewModel();
        PreferencesViewModel preferencesVM = new PreferencesViewModel();
        PlanCreationViewModel planCreationVM = new PlanCreationViewModel();
        DashboardViewModel dashboardVM = new DashboardViewModel();
        PlanDetailsViewModel planDetailsVM = new PlanDetailsViewModel();
        WeatherAdviceViewModel weatherAdviceVM = new WeatherAdviceViewModel();

        // ========== PRESENTERS & INTERACTORS & CONTROLLERS ==========

        // ---- Login ----
        LoginOutputBoundary loginPresenter = new LoginOutputBoundary() {
            @Override
            public void present(LoginOutputData outputData) {
                if (outputData.isSuccess()) {
                    loginVM.setLoggedInUser(outputData.getUser());
                    loginVM.setErrorMessage(null);
                } else {
                    loginVM.setLoggedInUser(null);
                    loginVM.setErrorMessage(outputData.getMessage());
                }
            }
        };
        LoginInputBoundary loginInteractor =
                new LoginInteractor(userGateway, loginPresenter);
        LoginController loginController =
                new LoginController(loginInteractor, loginVM);

        // ---- Register ----
        RegisterOutputBoundary registerPresenter = new RegisterOutputBoundary() {
            @Override
            public void present(RegisterOutputData outputData) {
                registerVM.setSuccess(outputData.isSuccess());
                registerVM.setMessage(outputData.getMessage());
            }
        };
        RegisterInputBoundary registerInteractor =
                new RegisterInteractor(userGateway, registerPresenter);
        RegisterController registerController =
                new RegisterController(registerInteractor, registerVM);

        // ---- Preferences (Get / Update / Add / Delete Favorite) ----

        GetPreferencesOutputBoundary getPrefsPresenter = new GetPreferencesOutputBoundary() {
            @Override
            public void present(GetPreferencesOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    preferencesVM.setErrorMessage(outputData.getErrorMessage());
                    return;
                }
                preferencesVM.setRadiusKm(outputData.getRadiusKm());
                preferencesVM.setSelectedCategories(outputData.getSelectedCategories());
                preferencesVM.setFavorites(outputData.getFavorites());
            }
        };

        UpdatePreferencesOutputBoundary updatePrefsPresenter = new UpdatePreferencesOutputBoundary() {
            @Override
            public void present(UpdatePreferencesOutputData outputData) {
                if (outputData.isSuccess()) {
                    preferencesVM.setMessage(outputData.getMessage());
                    preferencesVM.setErrorMessage(null);
                } else {
                    preferencesVM.setErrorMessage(outputData.getMessage());
                }
            }
        };

        AddFavoriteOutputBoundary addFavoritePresenter = new AddFavoriteOutputBoundary() {
            @Override
            public void present(AddFavoriteOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    preferencesVM.setErrorMessage(outputData.getErrorMessage());
                    return;
                }
                if (outputData.getFavorite() != null) {
                    preferencesVM.addFavorite(outputData.getFavorite());
                }
            }
        };

        DeleteFavoriteOutputBoundary deleteFavoritePresenter = new DeleteFavoriteOutputBoundary() {
            @Override
            public void present(DeleteFavoriteOutputData outputData) {
                if (outputData.isSuccess()) {
                    preferencesVM.setMessage(outputData.getMessage());
                    preferencesVM.setErrorMessage(null);
                } else {
                    preferencesVM.setErrorMessage(outputData.getMessage());
                }
            }
        };

        GetPreferencesInputBoundary getPrefsInteractor =
                new GetPreferencesInteractor(preferenceGateway, getPrefsPresenter);
        UpdatePreferencesInputBoundary updatePrefsInteractor =
                new UpdatePreferencesInteractor(preferenceGateway, updatePrefsPresenter);
        AddFavoriteInputBoundary addFavoriteInteractor =
                new AddFavoriteInteractor(preferenceGateway, geocodingGateway, addFavoritePresenter);
        DeleteFavoriteInputBoundary deleteFavoriteInteractor =
                new DeleteFavoriteInteractor(preferenceGateway, deleteFavoritePresenter);

        PreferencesController preferencesController = new PreferencesController(
                getPrefsInteractor,
                updatePrefsInteractor,
                addFavoriteInteractor,
                deleteFavoriteInteractor,
                preferencesVM
        );

        // ---- Search Places / Build Plan / Save Plan ----

        SearchPlacesOutputBoundary searchPlacesPresenter = new SearchPlacesOutputBoundary() {
            @Override
            public void present(SearchPlacesOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    planCreationVM.setRecommendedPlaces(java.util.List.of());
                    planCreationVM.setOriginAddress(null);
                    planCreationVM.setWeatherUsed(false);
                    planCreationVM.setErrorMessage(outputData.getErrorMessage());
                    return;
                }
                planCreationVM.setRecommendedPlaces(outputData.getPlaces());
                planCreationVM.setOriginAddress(outputData.getOriginAddress());
                planCreationVM.setWeatherUsed(outputData.isWeatherUsed());
                planCreationVM.setErrorMessage(null);
                if (!outputData.isWeatherUsed()) {
                    planCreationVM.setInfoMessage("Weather data unavailable. Results are not weather-optimized.");
                } else {
                    planCreationVM.setInfoMessage(null);
                }
            }
        };

        ComputeRouteOutputBoundary computeRoutePresenter = new ComputeRouteOutputBoundary() {
            @Override
            public void present(ComputeRouteOutputData data) {
                // dunno what to do here
            }
        };
        BuildPlanOutputBoundary buildPlanPresenter = new BuildPlanOutputBoundary() {
            @Override
            public void present(BuildPlanOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    planCreationVM.setPlanPreview(null);
                    planCreationVM.setPlanTruncated(false);
                    planCreationVM.setErrorMessage(outputData.getErrorMessage());
                    return;
                }
                planCreationVM.setPlanPreview(outputData.getPlan());
                planCreationVM.setPlanTruncated(outputData.isTruncated());
                if (outputData.isTruncated()) {
                    planCreationVM.setInfoMessage("Plan exceeds available time; some places were not included.");
                } else {
                    planCreationVM.setInfoMessage(null);
                }
            }
        };

        SavePlanOutputBoundary savePlanPresenter = new SavePlanOutputBoundary() {
            @Override
            public void present(SavePlanOutputData outputData) {
                if (outputData.isSuccess()) {
                    planCreationVM.setLastSavedPlan(outputData.getPlan());
                    planCreationVM.setInfoMessage(outputData.getMessage());
                    planCreationVM.setErrorMessage(null);
                } else {
                    planCreationVM.setErrorMessage(outputData.getMessage());
                }
            }
        };

        SearchPlacesInputBoundary searchPlacesInteractor =
                new SearchPlacesInteractor(
                        preferenceGateway,
                        geocodingGateway,
                        placesGateway,
                        weatherGateway,
                        searchPlacesPresenter
                );

        ComputeRouteInputBoundary computeRouteInteractor =
                new ComputeRouteInteractor(routeGateway, computeRoutePresenter);

        BuildPlanInputBoundary buildPlanInteractor =
                new BuildPlanInteractor(preferenceGateway, geocodingGateway, buildPlanPresenter);

        SavePlanInputBoundary savePlanInteractor =
                new SavePlanInteractor(planGateway, savePlanPresenter);

        PlanCreationController planCreationController = new PlanCreationController(
                searchPlacesInteractor,
                buildPlanInteractor,
                savePlanInteractor,
                planCreationVM
        );

        // ---- Plans Dashboard / Details / Delete / Apply Prefs ----

        ListPlansOutputBoundary listPlansPresenter = new ListPlansOutputBoundary() {
            @Override
            public void present(ListPlansOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    dashboardVM.setPlans(java.util.List.of());
                    dashboardVM.setErrorMessage(outputData.getErrorMessage());
                } else {
                    dashboardVM.setPlans(outputData.getPlans());
                    dashboardVM.setErrorMessage(null);
                }
            }
        };

        DeletePlanOutputBoundary deletePlanPresenter = new DeletePlanOutputBoundary() {
            @Override
            public void present(DeletePlanOutputData outputData) {
                if (outputData.isSuccess()) {
                    dashboardVM.setMessage(outputData.getMessage());
                    dashboardVM.setErrorMessage(null);
                } else {
                    dashboardVM.setErrorMessage(outputData.getMessage());
                }
            }
        };

        ApplyPreferencesFromPlanOutputBoundary applyPrefsFromPlanPresenter =
                new ApplyPreferencesFromPlanOutputBoundary() {
                    @Override
                    public void present(ApplyPreferencesFromPlanOutputData outputData) {
                        if (outputData.isSuccess()) {
                            dashboardVM.setMessage(outputData.getMessage());
                            dashboardVM.setErrorMessage(null);
                        } else {
                            dashboardVM.setErrorMessage(outputData.getMessage());
                        }
                    }
                };

        GetPlanDetailsOutputBoundary planDetailsPresenter = new GetPlanDetailsOutputBoundary() {
            @Override
            public void present(GetPlanDetailsOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    planDetailsVM.setPlan(null);
                    planDetailsVM.setErrorMessage(outputData.getErrorMessage());
                } else {
                    planDetailsVM.setPlan(outputData.getPlan());
                    planDetailsVM.setErrorMessage(null);
                }
            }
        };

        ListPlansInputBoundary listPlansInteractor =
                new ListPlansInteractor(planGateway, listPlansPresenter);
        DeletePlanInputBoundary deletePlanInteractor =
                new DeletePlanInteractor(planGateway, deletePlanPresenter);
        ApplyPreferencesFromPlanInputBoundary applyPrefsFromPlanInteractor =
                new ApplyPreferencesFromPlanInteractor(
                        planGateway,
                        preferenceGateway,
                        applyPrefsFromPlanPresenter
                );
        GetPlanDetailsInputBoundary getPlanDetailsInteractor =
                new GetPlanDetailsInteractor(planGateway, planDetailsPresenter);

        DashboardController dashboardController = new DashboardController(
                listPlansInteractor,
                deletePlanInteractor,
                applyPrefsFromPlanInteractor,
                getPlanDetailsInteractor,
                dashboardVM,
                planDetailsVM
        );

        // ---- Weather Advice ----

        WeatherAdviceOutputBoundary weatherAdvicePresenter = new WeatherAdviceOutputBoundary() {
            @Override
            public void present(WeatherAdviceOutputData outputData) {
                if (outputData.getErrorMessage() != null) {
                    weatherAdviceVM.setErrorMessage(outputData.getErrorMessage());
                    weatherAdviceVM.setSummary(null);
                    weatherAdviceVM.setAdvice(null);
                } else {
                    weatherAdviceVM.setSummary(outputData.getSummary());
                    weatherAdviceVM.setAdvice(outputData.getAdvice());
                    weatherAdviceVM.setErrorMessage(null);
                }
            }
        };

        WeatherAdviceInputBoundary weatherAdviceInteractor =
                new WeatherAdviceInteractor(geocodingGateway, weatherGateway, weatherAdvicePresenter);
        WeatherAdviceController weatherAdviceController =
                new WeatherAdviceController(weatherAdviceInteractor, weatherAdviceVM);

        // ========== START UI ==========
        SwingUtilities.invokeLater(() -> {
            AppFrame frame = new AppFrame(
                    loginController,
                    registerController,
                    preferencesController,
                    planCreationController,
                    dashboardController,
                    weatherAdviceController,
                    loginVM,
                    registerVM,
                    preferencesVM,
                    planCreationVM,
                    dashboardVM,
                    planDetailsVM,
                    weatherAdviceVM
            );
            frame.setVisible(true);
        });
    }
}
