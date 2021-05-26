package sample;

import datamodel.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class ControllerDeleteMember {
    @FXML
    private TextField fullname, pin, email;
    @FXML
    private Button back, confirm;
    @FXML
    private RadioButton genderF, genderM;

    /*
        Initialize function:
            Engage listeners
    */
    public void initialize() {
        addListeners();
    }

    /*
        If client data input is correct, the client will be deleted from database, else, an Alert will be shown
    */
    public void handleConfirm() {
        if(verifyDataForDeletion() == null) {
            Client clientModel = new Client(-1, fullname.getText(), email.getText(), pin.getText(),
                    (genderM.isSelected() ? "Male" : "Female"),
                    "No", "No", "No", "No");

            if(Client.delete(clientModel)) {
                fullname.setText("");
                genderF.setSelected(false);
                genderM.setSelected(false);
                email.setText("");
                pin.setText("");
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Client not found");
                alert.showAndWait();
            }
        }
        else
            verifyDataForDeletion().showAndWait();
    }

    /*
        Change scene from deleteMember to dashboard
    */
    public void handleBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Stage window = (Stage) back.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    /*
        Adds listeners to the text fields to restrict input accordingly
            full name: restricted to only letters, max 100 characters
            pin: restrict to only numbers, max 13 characters
            email: restricted to only letters, numbers and special characters, max 40 characters
    */
    public void addListeners() {
        fullname.textProperty().addListener((observable, oldValue, newValue) -> {
            if(fullname.getText().length() > 100)
                fullname.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z*"))
                fullname.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
        });

        pin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(pin.getText().length() > 13)
                pin.setText(oldValue);
            else if(!newValue.matches("\\d"))
                pin.setText(newValue.replaceAll("[^\\d]", ""));
        });

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if(email.getText().length() > 40)
                email.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z\\d\\W!#\\$%&\\(\\)*+,-./\\^:;<=>?@~"))
                email.setText(newValue.replaceAll("[^a-zA-Z\\d!#$%^&()*+,-./:;<=>?@~]", ""));
        });
    }

    /*
        Verifies if input data is valid for upload
            Returns an Alert if data is invalid
            Returns null if data is valid
    */
    public Alert verifyDataForDeletion() {
        String errors = "";
        if(fullname.getText().length() < 5)
            errors += "Name length must be at least 5\n";
        if(!genderM.isSelected() && !genderF.isSelected())
            errors += "Gender must be selected\n";
        if(!checkIfEmailValid(email.getText()))
            errors += "Email address is not valid\n";
        if(pin.getText().length() != 13)
            errors += "Pin must be 13 characters\n";

        if(errors.isEmpty())
            return null;
        else {
            errors = errors.substring(0, errors.length() - 1);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error uploading member");
            alert.setContentText(errors);
            return alert;
        }
    }

    /*
        Checks if email input syntax is valid
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
}
