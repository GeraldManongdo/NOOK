import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TransactionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTable table;
    private JTextField textField;

    public TransactionPanel() {
        setLayout(new BorderLayout());

        // Top Panel with Image Header
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(10, 100));
        panel.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel_1 = new JLabel("");
        ImageIcon HeaderImg = new ImageIcon(getClass().getResource("libraryHeading.png"));
        Image HeaderImg2 = HeaderImg.getImage().getScaledInstance(1080, 100, Image.SCALE_SMOOTH);
        lblNewLabel_1.setIcon(new ImageIcon(HeaderImg2));
        panel.add(lblNewLabel_1, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);

        JPanel panel_1 = new JPanel(new BorderLayout());
        add(panel_1, BorderLayout.CENTER);

        JPanel panel_2 = new JPanel(new BorderLayout());
        panel_2.setPreferredSize(new Dimension(10, 100));
        panel_1.add(panel_2, BorderLayout.NORTH);

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel = new JLabel("Transaction Management");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_3.add(lblNewLabel);

        JButton btnNewButton = new JButton("Borrow");
        btnNewButton.setBackground(Color.BLUE);
        btnNewButton.setForeground(Color.WHITE);
        btnNewButton.setPreferredSize(new Dimension(120, 23));
        btnNewButton.setBorder(null);
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 15));
        panel_3.add(btnNewButton, BorderLayout.EAST);
        btnNewButton.addActionListener(e -> new BorrowingForm().setVisible(true));

        JPanel panel_5 = new JPanel();
        panel_5.setPreferredSize(new Dimension(10, 40));
        panel_5.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel_2.add(panel_5, BorderLayout.SOUTH);
        panel_5.setLayout(new BorderLayout(10, 10));

        textField = new JTextField();
        textField.setText("");
        panel_5.add(textField);
        textField.setColumns(10);

        JButton btnNewButton_1 = new JButton("Find");
        btnNewButton_1.setPreferredSize(new Dimension(100, 23));
        panel_5.add(btnNewButton_1, BorderLayout.EAST);

        // Initialize Table
        String[] columnNames = {"History ID", "Book Name", "Borrower", "Borrow Date", "Return Date", "Status", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        panel_1.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Apply custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        // Load all data initially
        loadHistorysFromDatabase("");

        // Add search functionality
        btnNewButton_1.addActionListener(e -> {
            String userId = textField.getText().trim();
            loadHistorysFromDatabase(userId);
        });
    }

    private void loadHistorysFromDatabase(String userId) {
        model.setRowCount(0); // Clear table before loading new data

        String query = "SELECT bh.history_id, u.name AS user_name, b.title AS book_title, " +
                "bh.borrow_date, bh.return_date, bh.status " +
                "FROM history bh " +
                "JOIN users u ON bh.user_id = u.user_id " +
                "JOIN books b ON bh.book_id = b.book_id " +
                "WHERE bh.status = 'Borrowed'";

        if (!userId.isEmpty()) {
            query += " AND bh.user_id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (!userId.isEmpty()) {
                stmt.setInt(1, Integer.parseInt(userId)); // Set user_id if searching
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("history_id"),
                        rs.getString("book_title"),
                        rs.getString("user_name"),
                        rs.getDate("borrow_date"),
                        rs.getDate("return_date"),
                        rs.getString("status"),
                        "Actions"
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("View");
            setForeground(Color.WHITE);
            setBackground(Color.GRAY);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private int historyId;

        public ButtonEditor() {
            button = new JButton("View");
            button.setForeground(Color.WHITE);
            button.setBackground(Color.GRAY);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            historyId = (int) table.getValueAt(row, 0);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "View";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            openViewFrame(historyId);
            fireEditingStopped();
        }
    }

    private void openViewFrame(int historyId) {
        new HistoryRecordViewFrame(historyId);
    }
}
