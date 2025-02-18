import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AddPenaltyFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Connection conn;
    private JTextField txtUserName, txtBookTitle, txtAmount;
    private JCheckBox chkLateReturn, chkLostBook, chkDamageBook;
    private JButton btnSavePenalty;
    private int historyId;

    public AddPenaltyFrame(int historyId) {
        this.historyId = historyId;
        this.conn = DatabaseConnection.getConnection(); // Get the connection to the database source folder

        setTitle("NOOK - Add Penalty");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 380);
        setResizable(false); // Disable the changing size of the frame 
        setIconImage(new ImageIcon(getClass().getResource("NOOK-icon.png")).getImage()); // Adding icon in the top corner of the frame
        setLocationRelativeTo(null); // Center the window
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JPanel panelCenter = new JPanel(); // Increased the row count to accommodate more fields
        contentPane.add(panelCenter, BorderLayout.CENTER);
        panelCenter.setLayout(null);

        JLabel label = new JLabel("User Name:");
        label.setBounds(0, 17, 177, 34);
        panelCenter.add(label);
        txtUserName = new JTextField();
        txtUserName.setBounds(177, 17, 177, 34);
        txtUserName.setEditable(false);
        panelCenter.add(txtUserName);

        JLabel label_1 = new JLabel("Book Title:");
        label_1.setBounds(0, 62, 177, 34);
        panelCenter.add(label_1);
        txtBookTitle = new JTextField();
        txtBookTitle.setBounds(177, 62, 177, 34);
        txtBookTitle.setEditable(false);
        panelCenter.add(txtBookTitle);

        JLabel label_2 = new JLabel("Penalty Amount:");
        label_2.setBounds(0, 114, 177, 34);
        panelCenter.add(label_2);
        txtAmount = new JTextField();
        txtAmount.setBounds(177, 114, 177, 34);
        panelCenter.add(txtAmount);

        JLabel label_3 = new JLabel("Penalty Reasons:");
        label_3.setBounds(0, 159, 177, 69);
        panelCenter.add(label_3);
        JPanel penaltyPanel = new JPanel();
        penaltyPanel.setBounds(177, 159, 177, 69);
        penaltyPanel.setLayout(new BoxLayout(penaltyPanel, BoxLayout.Y_AXIS));

        // Adding all the checkboxes
        chkLateReturn = new JCheckBox("Late return");
        chkLostBook = new JCheckBox("Lost Book");
        chkDamageBook = new JCheckBox("Damage Book");

        penaltyPanel.add(chkLateReturn);
        penaltyPanel.add(chkLostBook);
        penaltyPanel.add(chkDamageBook);

        panelCenter.add(penaltyPanel); // Adding the panel with checkboxes

        JPanel panelSouth = new JPanel();
        panelSouth.setPreferredSize(new Dimension(10, 50));
        contentPane.add(panelSouth, BorderLayout.SOUTH);

        btnSavePenalty = new JButton("Save");
        btnSavePenalty.setPreferredSize(new Dimension(57, 30));
        btnSavePenalty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePenalty();
            }
        });
        panelSouth.setLayout(new GridLayout(0, 1, 0, 0));
        panelSouth.add(btnSavePenalty);
        
        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        
        JLabel lblNewLabel = new JLabel("Penalty Form");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        panel.add(lblNewLabel);

        loadHistoryData();
        setVisible(true);
    }


    private void loadHistoryData() {
        String query = "SELECT u.name AS user_name, b.title AS book_title " +
                "FROM history bh " +
                "JOIN users u ON bh.user_id = u.user_id " +
                "JOIN books b ON bh.book_id = b.book_id " +
                "WHERE bh.history_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtUserName.setText(rs.getString("user_name"));
                txtBookTitle.setText(rs.getString("book_title"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading history data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePenalty() {
        String penaltyReason = "";
        double penaltyAmount = 0.0;

        // Retrieve the penalty amount from the text field
        try {
            penaltyAmount = Double.parseDouble(txtAmount.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid penalty amount.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine the penalty reason based on selected checkboxes
        if (chkLateReturn.isSelected()) {
            penaltyReason = "Late Return";
        } else if (chkLostBook.isSelected()) {
            penaltyReason = "Lost Book";
            updateBookStatus("Unavailable");
        } else if (chkDamageBook.isSelected()) {
            penaltyReason = "Damage Book";
            updateBookStatus("Unavailable");
        }

        // If a penalty reason is selected, insert into the database
        if (!penaltyReason.isEmpty()) {
            try {
                String insertPenaltyQuery = "INSERT INTO penalty (history_id, reason, amount) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertPenaltyQuery)) {
                    stmt.setInt(1, historyId);
                    stmt.setString(2, penaltyReason);
                    stmt.setDouble(3, penaltyAmount);
                    stmt.executeUpdate();
                    dispose(); // Close the window
                    new HistoryRecordViewFrame(historyId); //Open HistoryRecordViewFrame
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving penalty: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a penalty reason.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void updateBookStatus(String status) {
        String updateQuery = "UPDATE books SET availability = ? WHERE title = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, status);
            stmt.setString(2, txtBookTitle.getText());
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating book status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
