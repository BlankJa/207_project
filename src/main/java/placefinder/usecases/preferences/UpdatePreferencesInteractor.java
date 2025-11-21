package placefinder.usecases.preferences;

import placefinder.entities.PreferenceProfile;
import placefinder.usecases.ports.PreferenceGateway;

import java.util.ArrayList;

public class UpdatePreferencesInteractor implements UpdatePreferencesInputBoundary {

    private final PreferenceGateway preferenceGateway;
    private final UpdatePreferencesOutputBoundary presenter;

    public UpdatePreferencesInteractor(PreferenceGateway preferenceGateway,
                                       UpdatePreferencesOutputBoundary presenter) {
        this.preferenceGateway = preferenceGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(UpdatePreferencesInputData inputData) {
        try {
            if (inputData.getInterests().size() > 3) {
                presenter.present(new UpdatePreferencesOutputData(false,
                        "You can select at most 3 interests."));
                return;
            }
            if (inputData.getRadiusKm() < 0 || inputData.getRadiusKm() > 5) {
                presenter.present(new UpdatePreferencesOutputData(false,
                        "Radius must be between 0 and 5 km."));
                return;
            }
            PreferenceProfile profile = preferenceGateway.loadForUser(inputData.getUserId());
            profile.setRadiusKm(inputData.getRadiusKm());
            profile.setInterests(new ArrayList<>(inputData.getInterests()));
            preferenceGateway.saveForUser(profile);
            presenter.present(new UpdatePreferencesOutputData(true, "Preferences saved."));
        } catch (Exception e) {
            presenter.present(new UpdatePreferencesOutputData(false, e.getMessage()));
        }
    }
}
