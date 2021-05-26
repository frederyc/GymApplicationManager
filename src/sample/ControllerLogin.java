package sample;

import datamodel.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class ControllerLogin {
    // Login Elements
    @FXML
    private TextField usernameLogin;
    @FXML
    private PasswordField passwordLogin;
    @FXML
    private Button loginRequest, forgotPasswordRequest;

    // Register Elements
    @FXML
    private TextField fname, lname, pin, address, email, username, answer;
    @FXML
    private RadioButton genderM, genderF;
    @FXML
    private PasswordField password, repassword;
    @FXML
    private Button signup, clear;
    @FXML
    private Label accountCreated, loginFail;
    private static User loggedInUser = null;

    // Controller Functions
    public void initialize() {
        addListeners();
    }

    // Login Functions

    /*
        Checks if username exists in the database. If it does, check the password associated with the username.
        If password matches, login is granted, a label alert is shown and the fields are cleared
    */
    public void handleLoginRequest() throws IOException {
        String username = usernameLogin.getText();
        String password = encodePasswordMD5(passwordLogin.getText());
        boolean userFound = false;

        if(User.AccountInformation.getAccountInformationFromDB() != null)
            for(User.AccountInformation i : User.AccountInformation.getAccountInformationFromDB())
                if(i.getUsername().equals(username) && i.getPassword().equals(password)) {
                    if(User.getUsersFromDB() != null)
                        for(User j : User.getUsersFromDB())
                            if(j.getId() == i.getId()) {
                                loggedInUser = j;
                                break;
                            }
                    Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    Stage window = (Stage) loginRequest.getScene().getWindow();
                    window.setScene(new Scene(root, 1920, 1017));
                    return;
                }

        // If user does not exists, display a message and clear fields
        temporaryLabelDisplay(loginFail, 3000);
        usernameLogin.setText("");
        passwordLogin.setText("");
    }

    public void handleForgotPasswordRequest() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("forgotPassword.fxml"));
        Stage window = (Stage) forgotPasswordRequest.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    // Register Functions

    /*
        If inserted data is valid, an account will be created
    */
    public void handleSignUp() {
        Alert alert = verifyDataForUpload();
        if(alert == null) {
            User user = new User(-1, new User.PersonalInformation(-1, fname.getText(), lname.getText(),
                    (genderM.isSelected() ? "Male" : "Female"), pin.getText(), address.getText()),
                    new User.AccountInformation(-1, email.getText(), username.getText(),
                            encodePasswordMD5(password.getText())),
                    new User.SecurityQuestion(-1, answer.getText()));

            boolean uploadStatus = user.upload();

            if(uploadStatus) {
                temporaryLabelDisplay(accountCreated, 3000);
                handleClear();
            }
            else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setHeaderText("User couldn't be uploaded because it already exists!");
                alert1.showAndWait();
            }
        }
        else {
            alert.showAndWait();
        }
    }

    /*
        Clears all inserted data in the register menu
    */
    public void handleClear() {
        fname.setText("");
        lname.setText("");
        pin.setText("");
        address.setText("");
        email.setText("");
        username.setText("");
        password.setText("");
        repassword.setText("");
        answer.setText("");
        genderF.setSelected(false);
        genderM.setSelected(false);
    }

    /*
        Verifies if user input data is correctly entered and respects basic requirements
    */
    public Alert verifyDataForUpload() {
        String errors = "";
        if(fname.getText().isEmpty())
            errors += "First Name field must not be empty\n";
        if(lname.getText().isEmpty())
            errors += "Last Name field must not be empty\n";
        if(!genderM.isSelected() && !genderF.isSelected())
            errors += "Gender must be selected\n";
        if(pin.getText().length() != 13)
            errors += "PIN field must have 13 characters\n";
        if(address.getText().length() < 5)
            errors += "Address field must have at least 5 characters\n";
        if(!checkIfEmailValid(email.getText()))
            errors += "Email field is incorrect\n";

        if(username.getText().length() < 5)
            errors += "Length of username must be at least 5 characters\n";
        char[] usernameCheck = username.getText().toCharArray();
        boolean letterFound = false, numberFound = false, specCharFound = false;
        for(char i : usernameCheck)
            if(Character.isLetter(i))
                letterFound = true;
            else if(Character.isDigit(i))
                numberFound = true;
            else if("!#$%^&()*+,-./:;<=>?@~".contains(i + ""))
                specCharFound = true;
        if(!letterFound)
            errors += "Username must contain at least a letter\n";
        if(!numberFound)
            errors += "Username must contain at least a number\n";
        if(!specCharFound)
            errors += "Username must contain at least a special character\n";

        if(password.getText().length() < 10)
            errors += "Length of password must be at least 10 characters\n";
        char[] passwordCheck = password.getText().toCharArray();
        letterFound = false; numberFound = false; specCharFound = false;
        for(char i : passwordCheck)
            if(Character.isLetter(i))
                letterFound = true;
            else if(Character.isDigit(i))
                numberFound = true;
            else if("!#$%^&()*+,-./:;<=>?@~".contains(i + ""))
                specCharFound = true;
        if(!letterFound)
            errors += "Password must contain at least a letter\n";
        if(!numberFound)
            errors += "Password must contain at least a number\n";
        if(!specCharFound)
            errors += "Password must contain at least a special character\n";

        if(!password.getText().equals(repassword.getText()))
            errors += "Passwords do not match\n";
        if(answer.getText().length() < 5)
            errors += "Answer must be at least 5 characters long\n";

        if(errors.isEmpty())
            return null;
        else {
            errors = errors.substring(0, errors.length() - 1);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Sign Up failed");
            alert.setContentText(errors);
            return alert;
        }
    }

    // Other functions

    /*
        Adds listeners to the text fields to restrict input accordingly
            username: restricted to only letters, numbers and special characters, max 25 characters
            password: restricted to only letters, numbers and special characters, max 25 characters
            first name: restrict to only letters and spaces, max 40 characters
            last name: restrict to only letters and spaces, max 40 characters
            pin: restrict to only numbers, max 13 characters
            address: max 80 characters
            email: restricted to only letters, numbers and special characters, max 40 characters
            username register: restricted to only letters, numbers and special characters, max 25 characters
            password register: restricted to only letters, numbers and special characters, max 25 characters
            repassword register: restricted to only letters, numbers and special characters, max 25 characters
            answer: max 80 characters
    */
    public void addListeners() {
        usernameLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(usernameLogin.getText().length() > 25)
                usernameLogin.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                usernameLogin.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        passwordLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(passwordLogin.getText().length() > 25)
                passwordLogin.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                passwordLogin.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        fname.textProperty().addListener((observable, oldValue, newValue) -> {
            if(fname.getText().length() > 40)
                fname.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z*"))
                fname.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
        });

        lname.textProperty().addListener((observable, oldValue, newValue) -> {
            if(lname.getText().length() > 40)
                lname.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z*"))
                lname.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
        });

        pin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(pin.getText().length() > 13)
                pin.setText(oldValue);
            else if(!newValue.matches("\\d"))
                pin.setText(newValue.replaceAll("[^\\d]", ""));
        });

        address.textProperty().addListener((observable, oldValue, newValue) -> {
           if(address.getText().length() > 80)
               address.setText(oldValue);
        });

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if(email.getText().length() > 40)
                email.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                email.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            if(username.getText().length() > 25)
                username.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                username.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if(password.getText().length() > 25)
                password.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                password.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        repassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if(repassword.getText().length() > 25)
                repassword.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                repassword.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        answer.textProperty().addListener((observable, oldValue, newValue) -> {
            if(answer.getText().length() > 80)
                answer.setText(oldValue);
        });
    }

    public static User getLoggedInUser() { return loggedInUser; }

    /*
        Encode password using MD5 encoding
    */
    public static String encodePasswordMD5(String password) {
        String generatedPassword;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte aByte : bytes)
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return generatedPassword;
    }

    /*
        Validates email address
    */
    private boolean checkIfEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    // Other

    /*
        @TODO add comment
    */
    public void temporaryLabelDisplay(Label label, long time) {
        new Thread(() -> {
            Platform.runLater(() -> label.setVisible(true));
            try {
                Thread.sleep(time);
            }
            catch(InterruptedException ex) {
                System.out.println(ex.toString());
            }
            Platform.runLater(new Runnable() {
                public void run() {
                    label.setVisible(false);
                }
            });
        }).start();
    }
}
