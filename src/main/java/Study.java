public class Study {

    private StudyStatus status = StudyStatus.DRAFT;

    private int limit;
    private String name;

    public Study(int limit, String name) {
        this.limit = limit;
        this.name = name;
    }

    public Study(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException("E: Limit should be greater than 0");
        }
        this.limit = limit;
    }

    public StudyStatus getStatus() {
        return this.status;
    }

    public int getLimit() {
        return limit;
    }


    public String getName() {
        return name;
    }

}
