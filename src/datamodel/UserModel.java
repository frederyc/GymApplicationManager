package datamodel;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class UserModel {
    private SimpleStringProperty fullName, gender, email, address;

    public UserModel(String fullName, String gender, String email, String address) {
        this.fullName = new SimpleStringProperty(fullName);
        this.gender = new SimpleStringProperty(gender);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
    }

    public static ArrayList<UserModel> getUserModelsFromDB() {
        ArrayList<User> users = User.getUsersFromDB();
        if(users != null) {
            final ArrayList<UserModel> userModels = new ArrayList<>();
            for(User i : users)
                userModels.add(new UserModel(i.getPersonalInformation().getFullName(),
                        i.getPersonalInformation().getGender(), i.getAccountInformation().getEmail(),
                        i.getPersonalInformation().getAddress()));
            return userModels;
        }
        else
            return null;
    }

    public String getFullName() { return fullName.get(); }

    public SimpleStringProperty fullNameProperty() { return fullName; }

    public String getGender() { return gender.get(); }

    public SimpleStringProperty genderProperty() { return gender; }

    public String getEmail() { return email.get(); }

    public SimpleStringProperty emailProperty() { return email; }

    public String getAddress() { return address.get(); }

    public SimpleStringProperty addressProperty() { return address; }

    public void setFullName(String fullName) { this.fullName.set(fullName); }

    public void setGender(String gender) { this.gender.set(gender); }

    public void setEmail(String email) { this.email.set(email); }

    public void setAddress(String address) { this.address.set(address); }

}
