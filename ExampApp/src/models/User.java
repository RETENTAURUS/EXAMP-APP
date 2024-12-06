package models;

public class User {
    private String name;
    private String User;

    public User(String name, String User) {
        this.name = name;
        this.User = User;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public void displayUser() {
        System.out.println("User Name: " + name);
        System.out.println("Whos Am i: " + User);
    }
}
