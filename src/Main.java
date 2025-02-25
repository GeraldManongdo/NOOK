import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Main class 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main frame = new Main();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Main() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("NOOK");
        setBounds(400, 200, 960, 540);
        setIconImage(new ImageIcon(getClass().getResource("NOOK-icon.png")).getImage());

        createBooksTable();
        createUserTable();
        createPenaltyTable();
        createHistoryTable();
        
        // Pass the Main frame (this) to Login
        setContentPane(new Login(this));
    }

    public void switchToMainFrame() {
        MainFrame dashboard = new MainFrame();
        dashboard.setVisible(true);
        dispose();
    }

    //Creating users table in database
    private void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(255), " +
                "role ENUM('student', 'admin'), " + // Only this can put in database
                "email VARCHAR(255), " +
                "password VARCHAR(255), " +
                "section VARCHAR(255), " +
                "number VARCHAR(255)) ";

        executeUpdate(createTableSQL, "Error creating users table!");
    }

    //Creating books table in database
    private void createBooksTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                "book_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "genre VARCHAR(100), " +
                "pages INT, " +
                "publication_date DATE, " +
                "book_image BLOB," +
                "availability ENUM('Borrowed', 'Available', 'Unavailable'))"; // Only this can put in database

        executeUpdate(createTableSQL, "Error creating books table!");
    }

    //Creating history table in database
    private void createHistoryTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS history (" +
                "history_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT, " +
                "book_id INT, " +
                "borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "return_date DATETIME NULL, " +
                "status ENUM('Borrowed', 'Returned'), " + // Only this can put in database
                "FOREIGN KEY (user_id) REFERENCES users(user_id), " + // Reference
                "FOREIGN KEY (book_id) REFERENCES books(book_id))"; // Reference

        executeUpdate(createTableSQL, "Error creating borrow_history table!");
    }
    
    //Creating penalty table in database
    private void createPenaltyTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS penalty (" +
                "penalty_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "history_id INT, " +
                "amount DECIMAL(10,2) NOT NULL, " +
                "reason ENUM('Late return', 'Lost Book', 'Damage Book') NOT NULL, " + // Only this can put in database
                "date_issued DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (history_id) REFERENCES history(history_id))"; // Reference

        executeUpdate(createTableSQL, "Error creating penalty table!");
    }

    //Show a panel if their is an error to the database
    private void executeUpdate(String sql, String errorMessage) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
