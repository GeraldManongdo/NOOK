import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HistoryRecordViewFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Connection conn;
    private int historyId;

    private JLabel lblBorrower, lblBookTitle, lblBorrowDate, lblStatus, lblReason, lblAmount;
    private JButton btnReturn;
    private String status;
    private JPanel panel;
    private JPanel panel_1;

    public HistoryRecordViewFrame(int historyId) {
        this.historyId = historyId;
        this.conn = DatabaseConnection.getConnection();

        setTitle("History Record Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 350, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridLayout(6, 2, 10, 10));
        setContentPane(contentPane);

        // Labels
        contentPane.add(new JLabel("Borrower Name:"));
        lblBorrower = new JLabel();
        contentPane.add(lblBorrower);

        contentPane.add(new JLabel("Borrowed Book:"));
        lblBookTitle = new JLabel();
        contentPane.add(lblBookTitle);

        contentPane.add(new JLabel("Borrow Date:"));
        lblBorrowDate = new JLabel();
        contentPane.add(lblBorrowDate);

        contentPane.add(new JLabel("Status:"));
        lblStatus = new JLabel();
        contentPane.add(lblStatus);
        
        panel = new JPanel();
        contentPane.add(panel);
        
        panel_1 = new JPanel();
        contentPane.add(panel_1);

        // Penalty Section
        lblReason = new JLabel();
        lblAmount = new JLabel();

        // Return Button
        btnReturn = new JButton("Return Book");
        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });

        // Load Data
        loadData();
        setVisible(true);
    }

    private void loadData() {
        String query = "SELECT bh.history_id, u.name AS user_name, b.title AS book_title, " +
                "bh.borrow_date, bh.return_date, bh.status " +
                "FROM history bh " +
                "JOIN users u ON bh.user_id = u.user_id " +
                "JOIN books b ON bh.book_id = b.book_id " +
                "WHERE bh.history_id = ?";

        String penaltyQuery = "SELECT reason, amount FROM penalty WHERE history_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lblBorrower.setText(rs.getString("user_name"));
                lblBookTitle.setText(rs.getString("book_title"));
                lblBorrowDate.setText(rs.getDate("borrow_date").toString());
                status = rs.getString("status");

                // Check for penalties
                try (PreparedStatement penaltyStmt = conn.prepareStatement(penaltyQuery)) {
                    penaltyStmt.setInt(1, historyId);
                    ResultSet penaltyRs = penaltyStmt.executeQuery();

                    if (penaltyRs.next()) {
                        lblStatus.setText("Penalized");
                        contentPane.add(new JLabel("Penalty Reason:"));
                        lblReason.setText(penaltyRs.getString("reason"));
                        contentPane.add(lblReason);

                        contentPane.add(new JLabel("Penalty Amount:"));
                        lblAmount.setText("₱" + penaltyRs.getDouble("amount"));
                        contentPane.add(lblAmount);
                    } else {
                        lblStatus.setText(status);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add return button if status is 'Borrowed'
        if ("Borrowed".equals(status)) {
            contentPane.add(btnReturn);
        }
    }

    private void returnBook() {
        String updateQuery = "UPDATE history SET return_date = ?, status = 'Returned' WHERE history_id = ?";
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, currentDate);
            stmt.setInt(2, historyId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            lblStatus.setText("Returned");
            btnReturn.setEnabled(false);
            DashboardPanel.loadTransactionDashboard(); // This will refresh the dashboard panel
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
