package placefinder.interface_adapters.viewmodels;

import placefinder.entities.Plan;

public class PlanDetailsViewModel {
    private Plan plan;
    private String errorMessage;

    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
