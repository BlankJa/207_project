package placefinder.usecases.plans;

import placefinder.usecases.ports.PlanGateway;

public class DeletePlanInteractor implements DeletePlanInputBoundary {

    private final PlanGateway planGateway;
    private final DeletePlanOutputBoundary presenter;

    public DeletePlanInteractor(PlanGateway planGateway,
                                DeletePlanOutputBoundary presenter) {
        this.planGateway = planGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeletePlanInputData inputData) {
        try {
            planGateway.deletePlan(inputData.getPlanId(), inputData.getUserId());
            presenter.present(new DeletePlanOutputData(true, "Plan deleted."));
        } catch (Exception e) {
            presenter.present(new DeletePlanOutputData(false, e.getMessage()));
        }
    }
}
