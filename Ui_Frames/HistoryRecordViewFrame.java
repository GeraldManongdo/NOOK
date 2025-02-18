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

    private JTextField txtBorrower, txtBookTitle, txtBorrowDate, txtStatus, txtReason, txtAmount;
    private JButton btnReturn, btnAddPenalty;
    private String status;
    private JPanel panel;
    private JLabel lblNewLabel;

    public HistoryRecordViewFrame(int historyId) {
        this.historyId = historyId;
        this.conn = DatabaseConnection.getConnection();

        setTitle("NOOK - Borrower Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 438);
        setResizable(false); // Disable the changing size of the frame 
        setIconImage(new ImageIcon(getClass().getResource("NOOK-icon.png")).getImage()); // Adding icon in the top corner of the frame
        setLocationRelativeTo(null); // Center the window
        
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JPanel panelCenter = new JPanel(new GridLayout(6, 2, 10, 10));
        contentPane.add(panelCenter, BorderLayout.CENTER);

        panelCenter.add(new JLabel("Borrower Name:"));
        txtBorrower = new JTextField();
        txtBorrower.setEditable(false);
        panelCenter.add(txtBorrower);

        panelCenter.add(new JLabel("Borrowed Book:"));
        txtBookTitle = new JTextField();
        txtBookTitle.setEditable(false);
        panelCenter.add(txtBookTitle);

        panelCenter.add(new JLabel("Borrow Date:"));
        txtBorrowDate = new JTextField();
        txtBorrowDate.setEditable(false);
        panelCenter.add(txtBorrowDate);

        panelCenter.add(new JLabel("Status:"));
        txtStatus = new JTextField();
        txtStatus.setEditable(false);
        panelCenter.add(txtStatus);

        panelCenter.add(new JLabel("Penalty Reason:"));
        txtReason = new JTextField();
        txtReason.setEditable(false);
        panelCenter.add(txtReason);

        panelCenter.add(new JLabel("Penalty Amount:"));
        txtAmount = new JTextField();
        txtAmount.setEditable(false);
        panelCenter.add(txtAmount);

        JPanel panelSouth = new JPanel();
        panelSouth.setBorder(new EmptyBorder(20, 0, 5, 0));
        panelSouth.setPreferredSize(new Dimension(10, 100));
        contentPane.add(panelSouth, BorderLayout.SOUTH);

        btnReturn = new JButton("Return Book");
        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
        panelSouth.setLayout(new GridLayout(0, 1, 0, 5));
        panelSouth.add(btnReturn);

        btnAddPenalty = new JButton("Add Penalty");
        btnAddPenalty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPenalty();
            }
        });
        panelSouth.add(btnAddPenalty);
        
        panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        
        lblNewLabel = new JLabel("Details of the borrower\r\n");
        lblNewLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        panel.add(lblNewLabel);

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
                txtBorrower.setText(rs.getString("user_name"));
                txtBookTitle.setText(rs.getString("book_title"));
                txtBorrowDate.setText(rs.getDate("borrow_date").toString());
                status = rs.getString("status");
                txtStatus.setText(status);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        try (PreparedStatement penaltyStmt = conn.prepareStatement(penaltyQuery)) {
            penaltyStmt.setInt(1, historyId);
            ResultSet penaltyRs = penaltyStmt.executeQuery();

            if (penaltyRs.next()) {
                txtStatus.setText("Penalized");
                txtReason.setText(penaltyRs.getString("reason"));
                txtAmount.setText("₱" + penaltyRs.getDouble("amount"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading penalty: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        btnReturn.setEnabled("Borrowed".equals(status));
    }

    private void returnBook() {
        String updateHistoryQuery = "UPDATE history SET return_date = ?, status = 'Returned' WHERE history_id = ?";
        String updateBookQuery = "UPDATE books SET availability = 'Available' WHERE book_id = (SELECT book_id FROM history WHERE history_id = ?)";
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try (PreparedStatement stmtHistory = conn.prepareStatement(updateHistoryQuery);
             PreparedStatement stmtBook = conn.prepareStatement(updateBookQuery)) {

            conn.setAutoCommit(false); // Start transaction

            // Update history table
            stmtHistory.setString(1, currentDate);
            stmtHistory.setInt(2, historyId);
            stmtHistory.executeUpdate();

            // Update book availability
            stmtBook.setInt(1, historyId);
            stmtBook.executeUpdate();

            conn.commit(); // Commit transaction

            JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtStatus.setText("Returned");
            btnReturn.setEnabled(false);
            DashboardPanel.loadTransactionDashboard();
            dispose();
        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback if error occurs
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                conn.setAutoCommit(true); // Restore default behavior
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void addPenalty() {
    	new AddPenaltyFrame(historyId); // Open AddPenaltyFrame window
    	dispose(); //Close the window
    }
}
