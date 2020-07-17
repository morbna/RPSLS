package acs.boundaries.sub;

public class InvokedBy {

    private UserId userId;

    public InvokedBy() {
        this.userId = new UserId();
    }

    public InvokedBy(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "InvokedBy [userId=" + userId + "]";
    }
}