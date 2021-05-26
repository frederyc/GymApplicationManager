package datamodel;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;

public final class User {
    private final int id;
    private final PersonalInformation personalInformation;
    private final AccountInformation accountInformation;
    private final SecurityQuestion securityQuestion;

    public User(int id, PersonalInformation personalInformation, AccountInformation accountInformation,
                SecurityQuestion securityQuestion) {
        this.id = id;
        this.personalInformation = personalInformation;
        this.accountInformation = accountInformation;
        this.securityQuestion = securityQuestion;
    }

    // @TODO create a method that uploads necessary data when user logs in

    /*
        @TODO add comment
    */
    public boolean upload() {
        if(personalInformation.validate() && accountInformation.validate()) {
            if(!personalInformation.upload() || !accountInformation.upload() || !securityQuestion.upload())
                return false;
            try {
                int idPI, idAI, idSQ;
                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();

                // @TODO change this crap
                statement.execute(String.format("SELECT * FROM personalInformation WHERE pin='%s'",
                        personalInformation.pin));
                idPI = idAI = idSQ = statement.getResultSet().getInt("id");
                // ------------------------

                statement.execute(String.format("INSERT INTO user VALUES(NULL, %d, %d, %d)", idPI, idAI, idSQ));

                statement.close();
                connection.close();
                return true;
            } catch (SQLException e) {
                System.out.println("Database error when uploading user information");
                return false;
            }
        }
        else
            return false;
    }

    // @returns the id of the user
    public int getId() { return id; }

    // @returns all user's data from database
    public static ArrayList<User> getUsersFromDB() {

        try {
            ArrayList<User> users = new ArrayList<>();

            ArrayList<PersonalInformation> personalInformations = PersonalInformation.getPersonalInformationFromDB();
            ArrayList<AccountInformation> accountInformations = AccountInformation.getAccountInformationFromDB();
            ArrayList<SecurityQuestion> securityQuestions = SecurityQuestion.getSecurityQuestionFromDB();


            Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
            Statement statement = connection.createStatement();
            statement.execute("SELECT * FROM user");
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next()) {
                PersonalInformation pi = null;
                AccountInformation ai = null;
                SecurityQuestion sq = null;

                assert personalInformations != null;
                for(PersonalInformation i : personalInformations)
                    if(resultSet.getInt("id_personalInformation") == i.getId()) {
                        pi = i;
                        break;
                    }

                assert accountInformations != null;
                for(AccountInformation i : accountInformations)
                    if(resultSet.getInt("id_accountInformation") == i.getId()) {
                        ai = i;
                        break;
                    }

                assert securityQuestions != null;
                for(SecurityQuestion i : securityQuestions)
                    if(resultSet.getInt("id_securityQuestion") == i.getId()) {
                        sq = i;
                        break;
                    }

                if(pi == null || ai == null || sq == null)
                    throw new Exception("Database distortion may be present");

                users.add(new User(resultSet.getInt("id"), pi, ai, sq));
            }

            statement.close();
            connection.close();
            return users;
        }
        catch (SQLException e) {
            System.out.println("Error loading all user's data from database");
            System.out.println(e.toString());
            return null;
        }
        catch(Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

    // @returns the personal information of the user
    public PersonalInformation getPersonalInformation() { return personalInformation; }

    // @returns the account information of the user
    public AccountInformation getAccountInformation() { return accountInformation; }

    // @return the security question of the user
    public SecurityQuestion getSecurityQuestion() { return securityQuestion; }

    // @class holding personal data
    public final static class PersonalInformation {
        private final int id;
        private final String fname, lname, gender, pin, address;

        public PersonalInformation(int id, String fname, String lname, String gender, String pin, String address) {
            this.id = id;
            this.fname = fname;
            this.lname = lname;
            this.gender = gender;
            this.pin = pin;
            this.address = address;
        }

        // @returns all user's personal data from database, null if operation fails
        public static ArrayList<PersonalInformation> getPersonalInformationFromDB() {
            try {
                ArrayList<PersonalInformation> personalInformations = new ArrayList<>();

                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute("SELECT * FROM personalInformation");
                ResultSet resultSet = statement.getResultSet();

                while(resultSet.next())
                    personalInformations.add(new PersonalInformation(resultSet.getInt("id"),
                            resultSet.getString("fname"), resultSet.getString("lname"),
                            resultSet.getString("gender"), resultSet.getString("pin"),
                            resultSet.getString("address")));

                resultSet.close();
                statement.close();
                connection.close();

                return personalInformations;
            } catch (SQLException e) {
                System.out.println("Error loading all user's personal data from database");
                return null;
            }
        }

        public String getFullName() { return fname + " " + lname; }

        public String getFname() { return fname; }

        public String getLname() { return lname; }

        public String getGender() { return gender; }

        public String getPin() { return pin; }

        public String getAddress() { return address; }

        /*
            Uploads personal data to database if data is validated
                @returns true if data is uploaded
                @returns false if data couldn't be uploaded
        */
        public boolean upload() {
            if(validate()) {
                try {
                    Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                    Statement statement = connection.createStatement();
                    statement.execute(String.format("INSERT INTO personalInformation VALUES(NULL, '%s', '%s', '%s', '%s', '%s')",
                            fname, lname, gender, pin, address));
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Database error when uploading personal information");
                    return false;
                }
                return true;
            }
            else
                return false;
        }

        /*
            Validates data for database uploading by checking if data already exists
                @returns true if user doesnt exist in the database
                @returns false if user does exist in the database
        */
        public boolean validate() {
            try {
                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute("SELECT * FROM personalInformation WHERE pin='" + pin + "'");

                boolean valid = true;
                while (statement.getResultSet().next() && valid)
                    valid = false;

                statement.close();
                connection.close();
                return valid;
            } catch (SQLException e) {
                System.out.println("Error when validating data for personalInformation");
                return false;
            }
        }

        // @returns the id assigned to this record in database
        public int getId() { return id; }
    }

    // @class holding account data
    public final static class AccountInformation {
        private final int id;
        private final String email, username, password;

        public AccountInformation(int id, String email, String username, String password) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        // @returns all user's account data from database, null if operation fails
        public static ArrayList<AccountInformation> getAccountInformationFromDB() {
            try {
                ArrayList<AccountInformation> accountInformations = new ArrayList<>();

                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute("SELECT * FROM accountInformation");
                ResultSet resultSet = statement.getResultSet();

                while(resultSet.next())
                    accountInformations.add(new AccountInformation(resultSet.getInt("id"),
                        resultSet.getString("email"), resultSet.getString("username"),
                        resultSet.getString("password")));

                resultSet.close();
                statement.close();
                connection.close();

                return accountInformations;
            } catch (SQLException e) {
                System.out.println("Error loading all user's account data from database");
                return null;
            }
        }

        public String getEmail() { return email; }

        public String getUsername() { return username; }

        public String getPassword() { return password; }

        /*
                    Uploads account data to database if data is validated
                        @returns true if data is uploaded
                        @returns false if data couldn't be uploaded
        */
        public boolean upload() {
            if(validate()) {
                try {
                    Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                    Statement statement = connection.createStatement();
                    statement.execute(String.format("INSERT INTO accountInformation VALUES(NULL, '%s', '%s', '%s')",
                            email, username, password));
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Database error when uploading account information");
                    return false;
                }
                return true;
            }
            else
                return false;
        }

        /*
            Validates data for database uploading by checking if data already exists
                @returns true if user doesnt exist in the database
                @returns false if user does exist in the database
        */
        public boolean validate() {
            try {
                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute("SELECT * FROM accountInformation WHERE username='" + username +"'");

                boolean validUsername = true;
                while (statement.getResultSet().next() && validUsername)
                    validUsername = false;

                statement.execute("SELECT * FROM accountInformation WHERE email='" + email + "'");
                boolean validEmail = true;
                while(statement.getResultSet().next() && validEmail)
                    validEmail = false;

                statement.close();
                connection.close();
                return validUsername && validEmail;
            } catch (SQLException e) {
                System.out.println("Error when validating data for accountInformation");
                return false;
            }
        }

        // @returns the id assigned to this record in database
        public int getId() { return id; }
    }

    public final static class SecurityQuestion {
        private final int id;
        private final String answer;

        public SecurityQuestion(int id, String answer) {
            this.id = id;
            this.answer = answer;
        }

        // @returns all user's security questions from database, null if operation fails
        public static ArrayList<SecurityQuestion> getSecurityQuestionFromDB() {
            try {
                ArrayList<SecurityQuestion> securityQuestions = new ArrayList<>();

                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute("SELECT * FROM securityQuestion");
                ResultSet resultSet = statement.getResultSet();

                while(resultSet.next())
                    securityQuestions.add(new SecurityQuestion(resultSet.getInt("id"),
                        resultSet.getString("answer")));

                resultSet.close();
                statement.close();
                connection.close();

                return securityQuestions;
            } catch (SQLException e) {
                System.out.println("Error loading all user's security data from database");
                return null;
            }
        }

        public String getAnswer() { return answer; }

        /*
                    Uploads security data to database
                        @returns true if data is uploaded
                        @returns false if data couldn't be uploaded
        */
        public boolean upload() {
            try {
                Connection connection = DriverManager.getConnection(sample.Main.getDatabase());
                Statement statement = connection.createStatement();
                statement.execute(String.format("INSERT INTO securityQuestion VALUES(NULL, '%s')", answer));
                statement.close();
                connection.close();
                return true;
            } catch (SQLException e) {
                System.out.println("Database error when uploading security question");
                return false;
            }
        }

        // @returns the id assigned to this record in database
        public int getId() { return id; }
    }


}
