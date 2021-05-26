package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ControllerSubscription {
    @FXML
    private Button goBack, standardGymPlanSelect, proGymPlanSelect, vipGymPlanSelect,
            yogaPlanSelect, crossFitPlanSelect, aerobicFitPlanSelect;
    private static String title, price, description;

    /*
        Sets title, price and description according to the Aerobic Plan
        Changes scene to selection screen
    */
    @FXML
    public void handleAerobicFitPlanSelect() throws IOException {
        title="Aerobic Standard Plan";
        price="PRICE: 115€";
        description="Human bodies are designed to be fit";
        goToSelectionScreen();
    }

    /*
        Sets title, price and description according to the CrossFit Plan
        Changes scene to selection screen
    */
    @FXML
    public void handleCrossFitPlanSelect() throws IOException {
        title="CrossFit Standard Plan";
        price="PRICE: 80€";
        description="You're too cool for lifting";
        goToSelectionScreen();
    }

    /*
        Sets title, price and description according to the Yoga Plan
        Changes scene to selection screen
    */
    @FXML
    public void handleYogaPlanSelect() throws IOException {
        title="Yoga Standard Plan";
        price="PRICE: 65€";
        description="Inhale the future, exhale the past";
        goToSelectionScreen();
    }

    /*
            Sets title, price and description according to the VIP Gym Plan
            Changes scene to selection screen
    */
    @FXML
    public void handleVipGymPlanSelect() throws IOException {
        title="VIP Gym Plan";
        price="PRICE: 185€";
        description="Muscle soreness is your best friend";
        goToSelectionScreen();
    }

    /*
        Sets title, price and description according to the Pro Gym Plan
        Changes scene to selection screen
    */
    @FXML
    public void handleProGymPlanSelect() throws IOException {
        title="Pro Gym Plan";
        price="PRICE: 110€";
        description="Best for those who take it seriously";
        goToSelectionScreen();
    }

    /*
        Sets title, price and description according to the Standard Gym Plan
        Changes scene to selection screen
    */
    @FXML
    public void handleStandardGymPlanSelect() throws IOException {
        title="Standard Gym Plan";
        price="PRICE: 80€";
        description="Good for beginners";
        goToSelectionScreen();
    }

    /*
        Change scene from addSubscription to dashboard
    */
    @FXML
    public void handleGoBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
        Stage window = (Stage) goBack.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }

    /*
        Returns the title, price and description from the plan selected
    */
    public static ArrayList<String> getTitlePriceDescription() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(title);
        strings.add(price);
        strings.add(description);
        return strings;
    }

    /*
        Change scene from addSubscription to selectSubscription
    */
    private void goToSelectionScreen() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("selectSubscription.fxml"));
        Stage window = (Stage) goBack.getScene().getWindow();
        window.setScene(new Scene(root, 1920, 1017));
    }
}
