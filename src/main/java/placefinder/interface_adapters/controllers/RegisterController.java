package placefinder.interface_adapters.controllers;

import placefinder.interface_adapters.viewmodels.RegisterViewModel;
import placefinder.usecases.register.*;

public class RegisterController implements RegisterOutputBoundary {

    private final RegisterInputBoundary interactor;
    private final RegisterViewModel viewModel;

    public RegisterController(RegisterInputBoundary interactor, RegisterViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    public void register(String name, String email, String password, String homeCity) {
        viewModel.setMessage(null);
        viewModel.setSuccess(false);
        interactor.execute(new RegisterInputData(name, email, password, homeCity));
    }

    @Override
    public void present(RegisterOutputData outputData) {
        viewModel.setSuccess(outputData.isSuccess());
        viewModel.setMessage(outputData.getMessage());
    }

    public RegisterViewModel getViewModel() { return viewModel; }
}
