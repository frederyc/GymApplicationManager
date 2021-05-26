package sample;

import datamodel.Client;
import datamodel.Subscription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class ControllerSelectSubscription {

    @FXML
    private TextField fullName, pin, email;
    @FXML
    private Button back;
    @FXML
    private Label title, price, description;

    /*
        Initialize:
            Sets plan data gained from subscription menu
            Engage listeners
    */
    public void initialize() {
        setPlan();
        addListeners();
    }

    /*

    */
    @FXML
    public void handleConfirm() {
        ArrayList<Client> clients = Client.getClientsFromDB();
        if(clients != null) {
            boolean client_found = false;
            for(Client i : clients)
                if(i.getPin().equals(pin.getText()) && i.getEmail().equals(email.getText()) &&
                i.getFullName().equals(fullName.getText())) {
                    client_found = true;
                    break;
                }

            if(client_found) {
                Subscription subscription = new Subscription(title.getText(), pin.getText(),
                        LocalDate.now().toString(), LocalDate.now().plusMonths(1).toString());

                if(subscription.upload()) {
                    try {
                        Connection connection = DriverManager.getConnection(Main.getDatabase());
                        Statement statement = connection.createStatement();
                        if(title.getText().contains("Gym"))
                            statement.execute(String.format("UPDATE client SET gymClient='Yes' WHERE pin='%s'",
                                    pin.getText()));
                        else if(title.getText().contains("Yoga"))
                            statement.execute(String.format("UPDATE client SET yogaClient='Yes' WHERE pin='%s'",
                                    pin.getText()));
                        else if(title.getText().contains("Aerobic"))
                            statement.execute(String.format("UPDATE client SET aerobicClient='Yes' WHERE pin='%s'",
                                    pin.getText()));
                        else if(title.getText().contains("CrossFit"))
                            statement.execute(String.format("UPDATE client SET crossFitClient='Yes' WHERE pin='%s'",
                                    pin.getText()));
                        statement.close();
                        connection.close();
                        fullName.setText("");
                        email.setText("");
                        pin.setText("");
                    }
                    catch (SQLException e) {
                        System.out.println("Error when updating client status");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error when updating client status");
                        alert.showAndWait();
                    }
                }
                else {
                    System.out.println("Error when uploading subscription");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error when uploading subscription");
                    alert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Client not found");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("There are no clients in the database");
            alert.showAndWait();
        }
    }

    /*
        Change scene from selectSubscription to addSubscription
    */
    @FXML
    public void handleBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addSubscription.fxml"));
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
        fullName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(fullName.getText().length() > 100)
                fullName.setText(oldValue);
            else if(!newValue.matches("\\sa-zA-Z*"))
                fullName.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
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
        Sets plan data
    */
    public void setPlan() {
        ArrayList<String> strings = ControllerSubscription.getTitlePriceDescription();
        title.setText(strings.get(0));
        price.setText(strings.get(1));
        description.setText(strings.get(2));
    }

}
