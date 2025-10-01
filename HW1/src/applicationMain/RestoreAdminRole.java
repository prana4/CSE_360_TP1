package applicationMain;

import database.Database;
import entityClasses.User;
import java.sql.SQLException;

public class RestoreAdminRole {

    public static void main(String[] args) {
        String username = "pri3";  // <-- replace with your username
        String password = "abc123";  // <-- replace with your password

        Database db = new Database();

        try {
            // Connect to the database
            db.connectToDatabase();

            // Check if the user exists
            if (!db.doesUserExist(username)) {
                System.out.println("User does not exist in the database.");
                return;
            }

            // Update the user's Admin role to true
            boolean success = db.updateUserRole(username, "Admin", "true");

            if (success) {
                System.out.println("Admin role successfully restored for user: " + username);
            } else {
                System.out.println("Failed to update Admin role for user: " + username);
            }

            // Optional: Verify login as Admin
            User user = new User();
            user.setUserName(username);
            user.setPassword(password);

            if (db.loginAdmin(user)) {
                System.out.println("*** You can now log in as Admin!");
            } else {
                System.out.println("*** Admin login failed. Check your username/password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }
}
