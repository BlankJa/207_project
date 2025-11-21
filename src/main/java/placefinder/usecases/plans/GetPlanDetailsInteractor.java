package placefinder.usecases.plans;

import placefinder.entities.Plan;
import placefinder.usecases.ports.PlanGateway;

public class GetPlanDetailsInteractor implements GetPlanDetailsInputBoundary {

    private final PlanGateway planGateway;
    private final GetPlanDetailsOutputBoundary presenter;

    public GetPlanDetailsInteractor(PlanGateway planGateway,
                                    GetPlanDetailsOutputBoundary presenter) {
        this.planGateway = planGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetPlanDetailsInputData inputData) {
        try {
            Plan plan = planGateway.findPlanWithStops(inputData.getPlanId());
            if (plan == null) {
                presenter.present(new GetPlanDetailsOutputData(null, "Plan not found."));
            } else {
                presenter.present(new GetPlanDetailsOutputData(plan, null));
            }
        } catch (Exception e) {
            presenter.present(new GetPlanDetailsOutputData(null, e.getMessage()));
        }
    }
}
