import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BorrowedBooksChart extends JFrame {
    private JLabel historyCountLabel, usersCountLabel, booksCountLabel;

    public BorrowedBooksChart() {
        setTitle("Database Records Counter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10)); // Three cards in a column

        // Create cards for each table
        historyCountLabel = createCard("History Transactions", new Color(70, 130, 180)); // Steel Blue
        usersCountLabel = createCard("Users", new Color(34, 139, 34)); // Forest Green
        booksCountLabel = createCard("Books", new Color(178, 34, 34)); // Firebrick Red

        // Fetch and update data from the database
        updateCounts();

        setVisible(true);
    }

    private JLabel createCard(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 30));
        countLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(title, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setForeground(Color.WHITE);

        panel.add(countLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);

        add(panel);
        return countLabel;
    }

    private void updateCounts() {
        updateCountFromTable("borrow_history", historyCountLabel);
        updateCountFromTable("users", usersCountLabel);
        updateCountFromTable("books", booksCountLabel);
    }

    private void updateCountFromTable(String tableName, JLabel label) {
        String query = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                label.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            label.setText("Error");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BorrowedBooksChart::new);
    }
}
