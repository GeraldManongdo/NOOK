import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class BorrowingForm extends JFrame {
    private JTextField bookNameField, borrowerField;
    private JButton saveButton;
    private JPopupMenu popupBook, popupBorrower;
    private Connection conn = DatabaseConnection.getConnection(); // Use your existing connection

    public BorrowingForm() {
        setTitle("Borrowing Form");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Book Name Field with Auto-Suggestions
        inputPanel.add(new JLabel("Book Name:"));
        bookNameField = new JTextField();
        popupBook = new JPopupMenu();
        bookNameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> showSuggestions(bookNameField, popupBook, getBookSuggestions(bookNameField.getText())));
            }
        });
        inputPanel.add(bookNameField);

        // Borrower Name Field with Auto-Suggestions
        inputPanel.add(new JLabel("Borrower:"));
        borrowerField = new JTextField();
        popupBorrower = new JPopupMenu();
        borrowerField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> showSuggestions(borrowerField, popupBorrower, getBorrowerSuggestions(borrowerField.getText())));
            }
        });
        inputPanel.add(borrowerField);

        getContentPane().add(inputPanel, BorderLayout.CENTER);

        // Save Button
        saveButton = new JButton("Save");
        getContentPane().add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Fetch book suggestions from database
    private ArrayList<String> getBookSuggestions(String query) {
        ArrayList<String> results = new ArrayList<>();
        if (query.length() < 1) return results;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT title FROM books WHERE title LIKE ?");
            ps.setString(1, query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("title"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    // Fetch borrower suggestions from database
    private ArrayList<String> getBorrowerSuggestions(String query) {
        ArrayList<String> results = new ArrayList<>();
        if (query.length() < 1) return results;
        
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT name FROM users WHERE role = 'student' AND (name LIKE ? OR user_id LIKE ?)"
            );
            ps.setString(1, query + "%");  // Search name starting with query
            ps.setString(2, query + "%");  // Search user_id starting with query
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("name")); // Show only name in suggestions
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }


    // Show suggestions dynamically while keeping focus on the text field
    private void showSuggestions(JTextField textField, JPopupMenu popup, ArrayList<String> suggestions) {
        popup.removeAll(); // Clear previous suggestions

        if (suggestions.isEmpty()) {
            popup.setVisible(false);
            return;
        }

        int popupWidth = Math.max(textField.getWidth(), 200); // Adjust popup width

        for (String suggestion : suggestions) {
            JMenuItem item = new JMenuItem(suggestion);
            item.setPreferredSize(new Dimension(popupWidth, 30)); // Set item size

            // Handle click on suggestion
            item.addActionListener(e -> {
                textField.setText(suggestion);
                popup.setVisible(false);
                textField.requestFocus(); // Keep focus on the text field
            });

            item.setFocusable(false); // Prevent stealing focus
            popup.add(item);
        }

        // Set popup size and show it
        popup.setPreferredSize(new Dimension(popupWidth, popup.getComponentCount() * 30));
        popup.show(textField, 0, textField.getHeight());

        // Ensure text field retains focus after showing suggestions
        SwingUtilities.invokeLater(() -> textField.requestFocus());
    }


}
