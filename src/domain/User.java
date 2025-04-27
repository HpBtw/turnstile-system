package domain;

public class User {
    public String feeType;
    public String name;
    public long id;

    public User(String feeType, String name, long id) {
        this.feeType = feeType;
        this.name = name;
        this.id = id;
    }
}
