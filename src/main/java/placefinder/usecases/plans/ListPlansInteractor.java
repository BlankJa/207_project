package placefinder.usecases.plans;

import placefinder.entities.Plan;
import placefinder.usecases.ports.PlanGateway;

import java.util.List;

public class ListPlansInteractor implements ListPlansInputBoundary {

    private final PlanGateway planGateway;
    private final ListPlansOutputBoundary presenter;

    public ListPlansInteractor(PlanGateway planGateway,
                               ListPlansOutputBoundary presenter) {
        this.planGateway = planGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(ListPlansInputData inputData) {
        try {
            List<Plan> plans = planGateway.findPlansByUser(inputData.getUserId());
            presenter.present(new ListPlansOutputData(plans, null));
        } catch (Exception e) {
            presenter.present(new ListPlansOutputData(List.of(), e.getMessage()));
        }
    }
}
