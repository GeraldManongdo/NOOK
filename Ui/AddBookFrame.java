import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddBookFrame extends JFrame {

    private JTextField bookNameField, authorField, genreField, pagesField, publicationDateField;
    private BookDashboard dashboardPanel; // Reference to DashboardPanel
    private JButton saveButton;

    public AddBookFrame(BookDashboard bookDashboard) {
        this.dashboardPanel = bookDashboard; // Store reference to DashboardPanel

        setTitle("Add Book");
        setSize(400, 313);
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setLocationRelativeTo(null); // Center the window

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inputPanel.add(new JLabel("Book Name:"));
        bookNameField = new JTextField();
        inputPanel.add(bookNameField);

        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);

        inputPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        inputPanel.add(genreField);

        inputPanel.add(new JLabel("Pages:"));
        pagesField = new JTextField();
        inputPanel.add(pagesField);

        inputPanel.add(new JLabel("Publication Date (YYYY-MM-DD):"));
        publicationDateField = new JTextField();
        inputPanel.add(publicationDateField);

        getContentPane().add(inputPanel, BorderLayout.CENTER);
        
        saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(56, 50));
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.CENTER);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBookToDatabase();
            }
        });

        setVisible(true);
    }
    
    private void saveBookToDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Validate input fields
            String title = bookNameField.getText().trim();
            String author = authorField.getText().trim();
            String genre = genreField.getText().trim();
            String publicationDate = publicationDateField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || publicationDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int pages;
            try {
                pages = Integer.parseInt(pagesField.getText().trim());
                if (pages <= 0) {
                    JOptionPane.showMessageDialog(this, "Pages must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format for Pages.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertSQL = "INSERT INTO books (title, author, genre, pages, publication_date, availability) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, author);
                preparedStatement.setString(3, genre);
                preparedStatement.setInt(4, pages);
                preparedStatement.setString(5, publicationDate);
                preparedStatement.setString(6, "Available");

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Close the window
                    if (dashboardPanel != null) {
                        dashboardPanel.loadBooksDashboard(); // Reload Dashboard
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add the book.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: Could not insert data.\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    
}
