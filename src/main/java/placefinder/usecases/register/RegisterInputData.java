package placefinder.usecases.register;

public class RegisterInputData {
    private final String name;
    private final String email;
    private final String password;
    private final String homeCity;

    public RegisterInputData(String name, String email, String password, String homeCity) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.homeCity = homeCity;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getHomeCity() { return homeCity; }
}
