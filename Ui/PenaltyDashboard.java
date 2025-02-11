import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PenaltyDashboard extends JPanel {

	private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTable table;

    public PenaltyDashboard() {
        setLayout(new BorderLayout());

        // Top Panel with Image Header
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(10, 200));
        panel.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel_1 = new JLabel("");
        ImageIcon HeaderImg = new ImageIcon(getClass().getResource("libraryHeading.png"));
        Image HeaderImg2 = HeaderImg.getImage().getScaledInstance(1080, 500, Image.SCALE_SMOOTH);
        lblNewLabel_1.setIcon(new ImageIcon(HeaderImg2));
        panel.add(lblNewLabel_1, BorderLayout.CENTER);

        add(panel, BorderLayout.NORTH);

        // Initialize Table
        String[] columnNames = {"History ID", "User Name", "Book Name","Penalty amount", "Reason", "Status", "Date issued"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Load History from database
        loadHistoryFromDatabase();
    }

    private void loadHistoryFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Optimized Query with JOIN
            String query = "SELECT p.penalty_id, u.name AS user_name, b.title AS book_title, " +
                           "p.amount, p.amount, p.reason, p.status, p.date_issued " +
                           "FROM penalty p " +
                           "JOIN users u ON p.user_id = u.student_id " +
                           "JOIN books b ON p.book_id = b.book_id";

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("penalty_id"),
                        rs.getString("user_name"),
                        rs.getString("book_title"),
                        rs.getBigDecimal("amount"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getDate("date_issued")
                    };
                    model.addRow(row);
                }
            }

            // Hide the History ID column
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
