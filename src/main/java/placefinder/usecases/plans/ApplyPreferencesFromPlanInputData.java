package placefinder.usecases.plans;

public class ApplyPreferencesFromPlanInputData {
    private final int planId;
    private final int userId;

    public ApplyPreferencesFromPlanInputData(int planId, int userId) {
        this.planId = planId;
        this.userId = userId;
    }

    public int getPlanId() { return planId; }
    public int getUserId() { return userId; }
}
