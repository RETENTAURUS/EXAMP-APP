package models;

public class User {
    private String name;
    private String userClass;

    public User(String name, String userClass) {
        this.name = name;
        this.userClass = userClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public void displayUser() {
        System.out.println("User Name: " + name);
        System.out.println("Class: " + userClass);
    }
}
