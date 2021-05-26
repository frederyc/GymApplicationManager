package datamodel;

import javafx.beans.property.SimpleStringProperty;
import sample.Main;

import java.sql.*;
import java.util.ArrayList;

public class Client {
    private SimpleStringProperty fullName, email, pin, gender,
        gymClient, yogaClient, aerobicClient, crossFitClient;

    public Client(int id, String fullName, String email, String pin, String gender,
                  String gymClient, String yogaClient, String aerobicClient, String crossFitClient) {
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.pin = new SimpleStringProperty(pin);
        this.gender = new SimpleStringProperty(gender);
        this.gymClient = new SimpleStringProperty(gymClient);
        this.yogaClient = new SimpleStringProperty(yogaClient);
        this.aerobicClient = new SimpleStringProperty(aerobicClient);
        this.crossFitClient = new SimpleStringProperty(crossFitClient);
    }

    public boolean upload() {
        ArrayList<Client> clients = getClientsFromDB();
        boolean valid_data = true;

        if(clients != null) {
            for (Client i : clients)
                if (i.getPin().equals(pin.get()) || i.getEmail().equals(email.get())) {
                    valid_data = false;
                    break;
                }
        }

        if(valid_data) {
            try {
                Connection connection = DriverManager.getConnection(Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute(String.format("INSERT INTO client VALUES(NULL, '%s', '%s', '%s', '%s'," +
                        " 'No', 'No', 'No', 'No');", fullName.get(), gender.get(), email.get(), pin.get()));
                statement.close();
                connection.close();
                return true;
            } catch(SQLException e) {
                System.out.println("Error when uploading client data to database");
                return false;
            }
        }
        else
            return false;
    }

    public static boolean delete(Client client) {
        boolean client_found = false;
        if(getClientsFromDB() == null)
            return false;
        else
            for(Client i : getClientsFromDB())
                if(client.getFullName().equals(i.getFullName()) && client.getGender().equals(i.getGender()) &&
                client.getEmail().equals(i.getEmail()) && client.getPin().equals(i.getPin())) {
                    client_found = true;
                    break;
                }

        if(client_found) {
            try {
                Connection connection = DriverManager.getConnection(Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute(String.format("DELETE FROM client WHERE pin='%s'", client.getPin()));
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                System.out.println("Error when deleting client from database");
                return false;
            }
            return true;
        }
        else
            return false;
    }

    public static ArrayList<Client> getClientsFromDB() {
        try {
            final ArrayList<Client> result = new ArrayList<>();

            Connection connection = DriverManager.getConnection(Main.getDatabase());
            Statement statement = connection.createStatement();
            statement.execute("SELECT * FROM client");
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next())
                result.add(new Client(resultSet.getInt("id") ,resultSet.getString("fullname"),
                        resultSet.getString("email"), resultSet.getString("pin"),
                        resultSet.getString("gender"), resultSet.getString("gymClient"),
                        resultSet.getString("yogaClient"), resultSet.getString("aerobicClient"),
                        resultSet.getString("crossFitClient")));

            resultSet.close();
            statement.close();
            connection.close();

            return (!result.isEmpty()) ? result : null;
        }
        catch(SQLException e) {
            System.out.println("Error loading all clients data from database");
            return null;
        }
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof Client)
            return pin.equals(((Client) object).pin) || email.equals(((Client) object).email);
        else
            return false;
    }

    public String getFullName() { return fullName.get(); }

    public SimpleStringProperty fullNameProperty() { return fullName; }

    public String getEmail() { return email.get(); }

    public SimpleStringProperty emailProperty() { return email; }

    public String getPin() { return pin.get(); }

    public SimpleStringProperty pinProperty() { return pin; }

    public String getGender() { return gender.get(); }

    public SimpleStringProperty genderProperty() { return gender; }

    public String getGymClient() { return gymClient.get(); }

    public SimpleStringProperty gymClientProperty() { return gymClient; }

    public String getYogaClient() { return yogaClient.get(); }

    public SimpleStringProperty yogaClientProperty() { return yogaClient; }

    public String getAerobicClient() { return aerobicClient.get(); }

    public SimpleStringProperty aerobicClientProperty() { return aerobicClient; }

    public String getCrossFitClient() { return crossFitClient.get(); }

    public SimpleStringProperty crossFitClientProperty() { return crossFitClient; }

    public void setFullName(String fullName) { this.fullName.set(fullName); }

    public void setEmail(String email) { this.email.set(email); }

    public void setPin(String pin) { this.pin.set(pin); }

    public void setGender(String gender) { this.gender.set(gender); }

    public void setGymClient(String gymClient) { this.gymClient.set(gymClient); }

    public void setYogaClient(String yogaClient) { this.yogaClient.set(yogaClient); }

    public void setAerobicClient(String aerobicClient) { this.aerobicClient.set(aerobicClient); }

    public void setCrossFitClient(String crossFitClient) { this.crossFitClient.set(crossFitClient); }
}
