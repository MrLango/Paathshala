package lango.mr.paathshala;

/**
 * Created by MAHE on 4/5/2018.
 */

public class User {
    private int id;
    private String name;
    private String email;
    private String type;


    public User(){}

    public User(int id, String name, String email, String type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
