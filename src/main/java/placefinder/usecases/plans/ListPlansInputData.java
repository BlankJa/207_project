package placefinder.usecases.plans;

public class ListPlansInputData {
    private final int userId;

    public ListPlansInputData(int userId) {
        this.userId = userId;
    }

    public int getUserId() { return userId; }
}
