import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class BookViewFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField textField, textField_1, textField_2, textField_3, textField_4;
    private int bookId;
    private Connection conn;

    public BookViewFrame(int bookId) {
        this.bookId = bookId;
        this.conn = DatabaseConnection.getConnection(); // Get the connection

        setTitle("View Book");
        setBounds(200, 200, 432, 357);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(null);
        getContentPane().add(panel, BorderLayout.CENTER);

        JLabel label = new JLabel("Book Name:");
        label.setBounds(21, 26, 92, 26);
        panel.add(label);
        textField = new JTextField(20);
        textField.setBounds(112, 24, 283, 31);
        panel.add(textField);

        JLabel label_1 = new JLabel("Author Name:");
        label_1.setBounds(21, 65, 92, 26);
        panel.add(label_1);
        textField_1 = new JTextField(20);
        textField_1.setBounds(112, 63, 283, 31);
        panel.add(textField_1);

        JLabel label_2 = new JLabel("Genre:");
        label_2.setBounds(21, 103, 92, 26);
        panel.add(label_2);
        textField_2 = new JTextField(20);
        textField_2.setBounds(112, 101, 283, 31);
        panel.add(textField_2);

        JLabel label_3 = new JLabel("Pages:");
        label_3.setBounds(21, 145, 92, 26);
        panel.add(label_3);
        textField_3 = new JTextField(20);
        textField_3.setBounds(112, 143, 283, 31);
        panel.add(textField_3);

        JLabel label_4 = new JLabel("Publication:");
        label_4.setBounds(21, 187, 92, 26);
        panel.add(label_4);
        textField_4 = new JTextField(20);
        textField_4.setBounds(112, 185, 283, 31);
        panel.add(textField_4);

        JButton availableButton = new JButton("Available");
        availableButton.addActionListener(e -> updateBookAvailability("Available"));
        availableButton.setBounds(111, 224, 141, 31);
        panel.add(availableButton);

        JButton unavailableButton = new JButton("Unavailable");
        unavailableButton.addActionListener(e -> updateBookAvailability("Unavailable"));
        unavailableButton.setBounds(255, 224, 141, 31);
        panel.add(unavailableButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveBookToDatabase());
        saveButton.setBounds(112, 265, 283, 31);
        panel.add(saveButton);

        loadBookData();
        setVisible(true);
    }

    private void loadBookData() {
        String query = "SELECT title, author, genre, pages, publication_date FROM books WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                textField.setText(rs.getString("title"));
                textField_1.setText(rs.getString("author"));
                textField_2.setText(rs.getString("genre"));
                textField_3.setText(String.valueOf(rs.getInt("pages")));
                textField_4.setText(rs.getString("publication_date"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load book data!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBookAvailability(String status) {
        String query = "UPDATE books SET availability = ? WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();

            DashboardPanel.reloadDashboard(); // This will refresh the dashboard panel
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update availability!\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void saveBookToDatabase() {
        try {
            // Validate input fields
            String title = textField.getText().trim();
            String author = textField_1.getText().trim();
            String genre = textField_2.getText().trim();
            String publicationDate = textField_4.getText().trim();

            if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || publicationDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int pages;
            try {
                pages = Integer.parseInt(textField_3.getText().trim());
                if (pages <= 0) {
                    JOptionPane.showMessageDialog(this, "Pages must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format for Pages.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updateSQL = "UPDATE books SET title = ?, author = ?, genre = ?, pages = ?, publication_date = ? WHERE book_id = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setString(3, genre);
                preparedStatement.setInt(4, pages);
                preparedStatement.setString(5, publicationDate);
                preparedStatement.setInt(6, bookId);

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Book details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    DashboardPanel.reloadDashboard(); // This will refresh the dashboard panel
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update book details.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: Could not update data.\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
