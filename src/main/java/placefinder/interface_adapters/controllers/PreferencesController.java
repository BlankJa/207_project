package placefinder.interface_adapters.controllers;

import placefinder.entities.FavoriteLocation;
import placefinder.interface_adapters.viewmodels.PreferencesViewModel;
import placefinder.usecases.preferences.*;

import java.util.List;
import java.util.Map;

public class PreferencesController implements
        GetPreferencesOutputBoundary,
        UpdatePreferencesOutputBoundary,
        AddFavoriteOutputBoundary,
        DeleteFavoriteOutputBoundary {

    private final GetPreferencesInputBoundary getPreferencesInteractor;
    private final UpdatePreferencesInputBoundary updatePreferencesInteractor;
    private final AddFavoriteInputBoundary addFavoriteInteractor;
    private final DeleteFavoriteInputBoundary deleteFavoriteInteractor;

    private final PreferencesViewModel viewModel;

    public PreferencesController(GetPreferencesInputBoundary getPreferencesInteractor,
                                 UpdatePreferencesInputBoundary updatePreferencesInteractor,
                                 AddFavoriteInputBoundary addFavoriteInteractor,
                                 DeleteFavoriteInputBoundary deleteFavoriteInteractor,
                                 PreferencesViewModel viewModel) {
        this.getPreferencesInteractor = getPreferencesInteractor;
        this.updatePreferencesInteractor = updatePreferencesInteractor;
        this.addFavoriteInteractor = addFavoriteInteractor;
        this.deleteFavoriteInteractor = deleteFavoriteInteractor;
        this.viewModel = viewModel;
    }

    public void loadPreferences(int userId) {
        viewModel.setErrorMessage(null);
        viewModel.setMessage(null);
        getPreferencesInteractor.execute(new GetPreferencesInputData(userId));
    }

    public void savePreferences(int userId, double radiusKm,
                                Map<String, List<String>> selectedCategories) {
        viewModel.setErrorMessage(null);
        viewModel.setMessage(null);
        updatePreferencesInteractor.execute(new UpdatePreferencesInputData(userId, radiusKm, selectedCategories));
    }

    public void addFavorite(int userId, String name, String address) {
        viewModel.setErrorMessage(null);
        addFavoriteInteractor.execute(new AddFavoriteInputData(userId, name, address));
    }

    public void deleteFavorite(int userId, int favoriteId) {
        viewModel.setErrorMessage(null);
        deleteFavoriteInteractor.execute(new DeleteFavoriteInputData(userId, favoriteId));
    }

    @Override
    public void present(GetPreferencesOutputData outputData) {
        if (outputData.getErrorMessage() != null) {
            viewModel.setErrorMessage(outputData.getErrorMessage());
            return;
        }
        viewModel.setRadiusKm(outputData.getRadiusKm());
        viewModel.setFavorites(outputData.getFavorites());
        viewModel.setSelectedCategories(outputData.getSelectedCategories());
    }

    @Override
    public void present(UpdatePreferencesOutputData outputData) {
        if (outputData.isSuccess()) {
            viewModel.setMessage(outputData.getMessage());
            viewModel.setErrorMessage(null);
        } else {
            viewModel.setErrorMessage(outputData.getMessage());
        }
    }

    @Override
    public void present(AddFavoriteOutputData outputData) {
        if (outputData.getErrorMessage() != null) {
            viewModel.setErrorMessage(outputData.getErrorMessage());
            return;
        }
        FavoriteLocation fav = outputData.getFavorite();
        if (fav != null) {
            viewModel.addFavorite(fav);
        }
    }

    @Override
    public void present(DeleteFavoriteOutputData outputData) {
        if (outputData.isSuccess()) {
            viewModel.setMessage(outputData.getMessage());
            viewModel.setErrorMessage(null);
        } else {
            viewModel.setErrorMessage(outputData.getMessage());
        }
    }

    public PreferencesViewModel getViewModel() { return viewModel; }
}
