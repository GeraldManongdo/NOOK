import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import Ui.Login;
import Ui.Dashboard;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;

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

    private void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "student_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(255), " +
                "role ENUM('student', 'admin'), " +
                "email VARCHAR(255), " +
                "password VARCHAR(255))";

        executeUpdate(createTableSQL, "Error creating users table!");
    }

    private void createBooksTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS books (" +
                "book_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "genre VARCHAR(100), " +
                "pages INT, " +
                "publication_date DATE, " +
                "availability VARCHAR(255))";

        executeUpdate(createTableSQL, "Error creating books table!");
    }

    private void createHistoryTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS borrow_history (" +
                "history_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT, " +
                "book_id INT, " +
                "borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "return_date DATETIME NULL, " +
                "status ENUM('borrowed', 'returned') DEFAULT 'borrowed', " +
                "FOREIGN KEY (user_id) REFERENCES users(student_id), " + // Fixed FK reference
                "FOREIGN KEY (book_id) REFERENCES books(book_id))";

        executeUpdate(createTableSQL, "Error creating borrow_history table!");
    }

    private void createPenaltyTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS penalty (" +
                "penalty_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT, " +
                "book_id INT, " +
                "amount DECIMAL(10,2) NOT NULL, " +
                "reason ENUM('late_return', 'lost_book', 'damage') NOT NULL, " +
                "status ENUM('unpaid', 'paid') DEFAULT 'unpaid', " +
                "date_issued DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(student_id), " +
                "FOREIGN KEY (book_id) REFERENCES books(book_id))";

        executeUpdate(createTableSQL, "Error creating penalty table!");
    }

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
