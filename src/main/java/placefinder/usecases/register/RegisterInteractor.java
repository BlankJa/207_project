package placefinder.usecases.register;

import placefinder.entities.PasswordUtil;
import placefinder.entities.User;
import placefinder.usecases.ports.UserGateway;

public class RegisterInteractor implements RegisterInputBoundary {

    private final UserGateway userGateway;
    private final RegisterOutputBoundary presenter;

    public RegisterInteractor(UserGateway userGateway, RegisterOutputBoundary presenter) {
        this.userGateway = userGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(RegisterInputData inputData) {
        try {
            User existing = userGateway.findByEmail(inputData.getEmail());
            if (existing != null) {
                presenter.present(new RegisterOutputData(false, "Email already in use."));
                return;
            }
            String hash = PasswordUtil.hashPassword(inputData.getPassword());
            User user = new User(null, inputData.getName(), inputData.getEmail(), hash, inputData.getHomeCity());
            userGateway.save(user);
            presenter.present(new RegisterOutputData(true, "Registration successful. You can now log in."));
        } catch (Exception e) {
            presenter.present(new RegisterOutputData(false, e.getMessage()));
        }
    }
}
