package placefinder.interface_adapters.viewmodels;

import placefinder.entities.User;

public class LoginViewModel {
    private User loggedInUser;
    private String errorMessage;

    public User getLoggedInUser() { return loggedInUser; }
    public void setLoggedInUser(User loggedInUser) { this.loggedInUser = loggedInUser; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
