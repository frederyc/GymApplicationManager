package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class ControllerForgotPassword {
    @FXML
    private Button back;
    @FXML
    private TextField fname, lname, email, pin, answer;
    @FXML
    private PasswordField newpsw, renewpsw;

    public void initialize() {
        addListeners();
    }

    /*
        Check if data is entered correctly, then check if a user with the respective data exists.
        If it does, changing password is granted, if the password meets password requirements ( checkPassword() )
    */
    public void handleReset() throws SQLException {
        Alert alert;
        Connection connection = DriverManager.getConnection(Main.getDatabase());
        Statement statement = connection.createStatement();
        ResultSet resultSet;

        int id = -1;
        statement.execute("SELECT * FROM personalInformation WHERE pin='" + pin.getText() + "'");
        resultSet = statement.getResultSet();

        while(resultSet.next() && id == -1)
            id = resultSet.getInt("id");

        if(id == -1) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("User not found");
            alert.showAndWait();
        }
        else {
            String pinToCheck, emailToCheck, answerToCheck, fnameToCheck, lnameToCheck;
            statement.execute("SELECT * FROM personalInformation WHERE pin='" + pin.getText() + "'");
            resultSet = statement.getResultSet();
            pinToCheck = resultSet.getString("pin");
            fnameToCheck = resultSet.getString("fname");
            lnameToCheck = resultSet.getString("lname");
            statement.execute("SELECT * FROM accountInformation WHERE id=" + id);
            emailToCheck = statement.getResultSet().getString("email");
            statement.execute("SELECT * FROM securityQuestion WHERE id=" + id);
            answerToCheck = statement.getResultSet().getString("answer");

            if(pinToCheck.equals(pin.getText()) && emailToCheck.equals(email.getText()) &&
                    answerToCheck.equals(answer.getText()) && fnameToCheck.equals(fname.getText()) &&
                    lnameToCheck.equals(lname.getText())) {
                boolean validPassword = checkPassword(newpsw.getText()) && newpsw.getText().equals(renewpsw.getText());
                if(validPassword) {
                    statement.execute(String.format("UPDATE accountInformation SET password='%s' WHERE id=%d",
                            ControllerLogin.encodePasswordMD5(newpsw.getText()), id));

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Password changed successfully");
                    clearFields();
                }
                else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Password must contain at least a letter,\na number and a special character");
                }
                alert.showAndWait();
            }
            else {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("User not found");
                alert.showAndWait();
            }
        }

        statement.close();
        connection.close();
    }

    /*
        Takes the user back to the login screen
    */
    public void handleBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Stage window = (Stage) back.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    /*
        Adds listeners to the text fields to restrict input accordingly
            first name: restrict to only letters and spaces, max 40 characters
            last name: restrict to only letters and spaces, max 40 characters
            pin: restrict to only numbers, max 13 characters
            email: restricted to only letters, numbers and special characters, max 40 characters
            password register: restricted to only letters, numbers and special characters, max 25 characters
            repassword register: restricted to only letters, numbers and special characters, max 25 characters
    */
    public void addListeners() {
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

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if(email.getText().length() > 40)
                email.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                email.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        pin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(pin.getText().length() > 13)
                pin.setText(oldValue);
            else if(!newValue.matches("\\d"))
                pin.setText(newValue.replaceAll("[^\\d]", ""));
        });

        newpsw.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newpsw.getText().length() > 25)
                newpsw.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                newpsw.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });

        renewpsw.textProperty().addListener((observable, oldValue, newValue) -> {
            if(renewpsw.getText().length() > 25)
                renewpsw.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                renewpsw.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });
    }

    /*
        Check if password contains a letter, a number, a special character and is at least 10 characters long
    */
    private boolean checkPassword(String password) {
        char[] psw = password.toCharArray();
        boolean number = false, letter = false, spec = false;
        for(char i : psw)
            if(Character.isLetter(i))
                letter = true;
            else if(Character.isDigit(i))
                number = true;
            else if("!#$%^&()*+,-./:;<=>?@~".contains(i + ""))
                spec = true;

        return number && letter && spec && password.length() >= 10;
    }

    /*
        Clears all the fields
    */
    private void clearFields() {
        fname.setText("");
        lname.setText("");
        email.setText("");
        pin.setText("");
        answer.setText("");
        newpsw.setText("");
        renewpsw.setText("");
    }
}
