package datamodel;

import javafx.beans.property.SimpleStringProperty;
import sample.Main;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class Subscription {
    private SimpleStringProperty type, ownerPIN, startDate, endDate;

    public Subscription(String type, String ownerPIN, String startDate, String endDate) {
        this.type = new SimpleStringProperty(type);
        this.ownerPIN = new SimpleStringProperty(ownerPIN);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
    }

    public boolean upload() {
        boolean valid = true;
        if(getSubscriptionsFromDB() != null) {
            for (Subscription i : getSubscriptionsFromDB())
                if (i.getType().equals(type.get()) &&
                        Period.between(LocalDate.parse(i.getEndDate()), LocalDate.now()).toString().contains("-")) {
                    valid = false;
                    break;
                }
            if(valid) {
                try {
                    Connection connection = DriverManager.getConnection(Main.getDatabase());
                    Statement statement = connection.createStatement();
                    statement.execute(String.format("INSERT INTO subscriptions VALUES(NULL, '%s', '%s', '%s', '%s')",
                            type.get(), ownerPIN.get(), startDate.get(), endDate.get()));
                    statement.close();
                    connection.close();
                    return true;
                }
                catch (SQLException e) {
                    System.out.println("Error uploading subscription to database");
                    return false;
                }
            }
            else
                return false;
        }
        else {
            try {
                Connection connection = DriverManager.getConnection(Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute(String.format("INSERT INTO subscriptions VALUES(NULL, '%s', '%s', '%s', '%s')",
                        type.get(), ownerPIN.get(), startDate.get(), endDate.get()));
                statement.close();
                connection.close();
                return true;
            }
            catch (SQLException e) {
                System.out.println("Error uploading subscription to database");
                return false;
            }
        }
    }

    public static ArrayList<Subscription> getSubscriptionsFromDB() {
        try {
            final ArrayList<Subscription> subscriptions = new ArrayList<>();

            Connection connection = DriverManager.getConnection(Main.getDatabase());
            Statement statement = connection.createStatement();
            statement.execute("SELECT * FROM subscriptions");
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next())
                subscriptions.add(new Subscription(resultSet.getString("type"),
                        resultSet.getString("owner_pin"), resultSet.getString("start_date"),
                        resultSet.getString("end_date")));

            resultSet.close();
            statement.close();
            connection.close();

            return (!subscriptions.isEmpty()) ? subscriptions : null;
        } catch (SQLException e) {
            System.out.println("Error loading subscriptions from database");
            return null;
        }
    }

    public String getType() { return type.get(); }

    public SimpleStringProperty typeProperty() { return type; }

    public String getOwnerPIN() { return ownerPIN.get(); }

    public SimpleStringProperty ownerPINProperty() { return ownerPIN; }

    public String getStartDate() { return startDate.get(); }

    public SimpleStringProperty startDateProperty() { return startDate; }

    public String getEndDate() { return endDate.get(); }

    public SimpleStringProperty endDateProperty() { return endDate; }

    public void setType(String type) { this.type.set(type); }

    public void setOwnerPIN(String ownerPIN) { this.ownerPIN.set(ownerPIN); }

    public void setStartDate(String startDate) { this.startDate.set(startDate); }

    public void setEndDate(String endDate) { this.endDate.set(endDate); }
}
