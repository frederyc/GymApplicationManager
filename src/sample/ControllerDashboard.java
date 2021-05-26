package sample;

import datamodel.Client;
import datamodel.Subscription;
import datamodel.User;
import datamodel.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ControllerDashboard {
    @FXML
    private Label welcomeback;
    @FXML
    private Button addmember, deletemember, renewsub, logout,
            activemembersButton, subscriptionsButton, staffmembersButton;
    @FXML
    private TableView<Client> activememberstable;
    @FXML
    private TableView<Subscription> subscriptionstable;
    @FXML
    private TableView<UserModel> staffmemberstable;
    @FXML
    private TableColumn<Client, String> nameColumnAM, genderColumnAM, emailColumnAM,
        gymColumnAM, yogaColumnAM, crossfitColumnAM, aerobicColumnAM;
    @FXML
    private TableColumn<Subscription, String> typeColumnSB,ownerpinColumnSB, startdateColumnSB, enddateColumnSB;
    @FXML
    private TableColumn<UserModel, String> nameColumnSM, genderColumnSM, emailColumnSM, addressColumnSM;

    private User loggedInUser;
    private final ObservableList<Client> clients = FXCollections.observableArrayList();
    private final ObservableList<Subscription> subscriptions = FXCollections.observableArrayList();
    private final ObservableList<UserModel> staffmembers = FXCollections.observableArrayList();

    /*
        Initialize function:
            Gets logged in user data
            Sets logged in data accordingly
            Loads all records from database
    */
    public void initialize() {
        loggedInUser = ControllerLogin.getLoggedInUser();
        setLogInData();
        setCellFactories();
        loadDataFromDB();
    }

    /*
        Loads all records for clients, subscriptions and staff members
    */
    public void loadDataFromDB() {
        if(Client.getClientsFromDB() != null)
            for(Client i : Client.getClientsFromDB())
                clients.add(0, i);
        if(UserModel.getUserModelsFromDB() != null)
            for(UserModel i : UserModel.getUserModelsFromDB())
                staffmembers.add(0, i);
        if(Subscription.getSubscriptionsFromDB() != null)
            for(Subscription i : Subscription.getSubscriptionsFromDB())
                subscriptions.add(0, i);
    }

    /*
        Sets cell factories for clients, subscriptions and staff members tables
    */
    public void setCellFactories() {
        // Active members
        nameColumnAM.setCellValueFactory(new PropertyValueFactory<>("FullName"));
        genderColumnAM.setCellValueFactory(new PropertyValueFactory<>("Gender"));
        emailColumnAM.setCellValueFactory(new PropertyValueFactory<>("Email"));
        gymColumnAM.setCellValueFactory(new PropertyValueFactory<>("GymClient"));
        yogaColumnAM.setCellValueFactory(new PropertyValueFactory<>("YogaClient"));
        crossfitColumnAM.setCellValueFactory(new PropertyValueFactory<>("CrossFitClient"));
        aerobicColumnAM.setCellValueFactory(new PropertyValueFactory<>("AerobicClient"));
        activememberstable.setItems(clients);
        // Subscriptions
        typeColumnSB.setCellValueFactory(new PropertyValueFactory<>("Type"));
        ownerpinColumnSB.setCellValueFactory(new PropertyValueFactory<>("OwnerPIN"));
        startdateColumnSB.setCellValueFactory(new PropertyValueFactory<>("StartDate"));
        enddateColumnSB.setCellValueFactory(new PropertyValueFactory<>("EndDate"));
        subscriptionstable.setItems(subscriptions);
        // Staff members
        nameColumnSM.setCellValueFactory(new PropertyValueFactory<>("FullName"));
        genderColumnSM.setCellValueFactory(new PropertyValueFactory<>("Gender"));
        emailColumnSM.setCellValueFactory(new PropertyValueFactory<>("Email"));
        addressColumnSM.setCellValueFactory(new PropertyValueFactory<>("Address"));
        staffmemberstable.setItems(staffmembers);
    }

    /*
        Sets different UI elements acording to logged in user and database statistics
    */
    public void setLogInData() {
        welcomeback.setText(welcomeback.getText() + loggedInUser.getPersonalInformation().getFullName());
        ArrayList<Client> _clients = Client.getClientsFromDB();
        ArrayList<Subscription> _subscriptions = Subscription.getSubscriptionsFromDB();
        ArrayList<UserModel> _userModels = UserModel.getUserModelsFromDB();

        if(_clients == null)
            activemembersButton.setText("0" + activemembersButton.getText());
        else
            activemembersButton.setText(_clients.size() + activemembersButton.getText());
        if(_subscriptions == null)
            subscriptionsButton.setText("0" + subscriptionsButton.getText());
        else
            subscriptionsButton.setText(_subscriptions.size() + subscriptionsButton.getText());
        if(_userModels == null)
            staffmembersButton.setText("0" + staffmembersButton.getText());
        else
            staffmembersButton.setText(_userModels.size() + staffmembersButton.getText());
    }

    /*
        If user chooses 'Yes', change scene from dashboard to login
    */
    public void handleLogout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Are you sure you want to log out?");
        alert.showAndWait();

        if(alert.getResult().getText().equals("Yes")) {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage window = (Stage) logout.getScene().getWindow();
            window.setScene(new Scene(root, 1920, 1017));
        }
    }

    /*
        Sets members table as visible
    */
    public void handleActiveMembers() {
        subscriptionstable.setVisible(false);
        staffmemberstable.setVisible(false);
        activememberstable.setVisible(true);
    }

    /*
        Sets subscriptions table as visible
    */
    public void handleSubscriptions() {
        staffmemberstable.setVisible(false);
        activememberstable.setVisible(false);
        subscriptionstable.setVisible(true);
    }

    /*
        Sets staff members table as visible
    */
    public void handleStaffMembers() {
        activememberstable.setVisible(false);
        subscriptionstable.setVisible(false);
        staffmemberstable.setVisible(true);
    }

    /*
        Change scene from dashboard to addMember
    */
    public void handleAddMember() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addMember.fxml"));
        Stage window = (Stage) addmember.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    /*
        Change scene from dashboard to deleteMember
    */
    public void handleDeleteMember() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("deleteMember.fxml"));
        Stage window = (Stage) deletemember.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    /*
        Change scene from dashboard to renewSubscription
    */
    public void handleRenewSubscription() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addSubscription.fxml"));
        Stage window = (Stage) renewsub.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }
}
