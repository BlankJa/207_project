package placefinder.usecases.preferences;

public class GetPreferencesInputData {
    private final int userId;

    public GetPreferencesInputData(int userId) {
        this.userId = userId;
    }

    public int getUserId() { return userId; }
}
