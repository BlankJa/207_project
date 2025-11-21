package placefinder.usecases.plans;

public class ApplyPreferencesFromPlanOutputData {
    private final boolean success;
    private final String message;

    public ApplyPreferencesFromPlanOutputData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
