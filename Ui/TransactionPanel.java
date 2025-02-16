import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;


public class TransactionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTable table;
    

	public TransactionPanel() {
		setLayout(new BorderLayout());

        // Top Panel with Image Header
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(10, 100));
        panel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel_1 = new JLabel("");
        ImageIcon HeaderImg = new ImageIcon(getClass().getResource("libraryHeading.png"));
        Image HeaderImg2 = HeaderImg.getImage().getScaledInstance(1080, 500, Image.SCALE_SMOOTH);
        lblNewLabel_1.setIcon(new ImageIcon(HeaderImg2));
        panel.add(lblNewLabel_1, BorderLayout.CENTER);
        
        add(panel, BorderLayout.NORTH);
        
        JPanel panel_1 = new JPanel();
        add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        JPanel panel_2 = new JPanel();
        panel_2.setPreferredSize(new Dimension(10, 150));
        panel_1.add(panel_2, BorderLayout.NORTH);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel = new JLabel("Transaction Management\r\n");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(lblNewLabel, BorderLayout.NORTH);
        
        JPanel panel_3 = new JPanel();
        panel_3.setPreferredSize(new Dimension(10, 100));
        panel_3.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(panel_3);
        panel_3.setLayout(new GridLayout(0, 1, 0, 0));
        
        JPanel panel_4 = new JPanel();
        panel_3.add(panel_4);
        panel_4.setLayout(new GridLayout(0, 2, 10, 0));
        
        JButton btnNewButton = new JButton("Borrow");
        panel_4.add(btnNewButton);
        btnNewButton.addActionListener(e -> {
        	BorrowingForm borrowingForm = new BorrowingForm();
        	borrowingForm.setVisible(true);
        });
        
        
        JButton btnNewButton_1 = new JButton("Return");
        panel_4.add(btnNewButton_1);
        
        JPanel panel_5 = new JPanel();
        panel_3.add(panel_5);
        
        // Initialize Table
        String[] columnNames = {"History ID","Book Name", "Borrower", "Borrow Date", "Return Date", "Status", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(30);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel_1.add(scrollPane, BorderLayout.CENTER);

        // Load books from database
        loadHistorysFromDatabase();
	}
	
    private void loadHistorysFromDatabase() {
        try {
        	Connection conn = DatabaseConnection.getConnection(); 
        	String query = "SELECT bh.history_id, u.name AS user_name, b.title AS book_title, " +
                    "bh.borrow_date, bh.return_date, bh.status " +
                    "FROM history bh " +
                    "JOIN users u ON bh.user_id = u.user_id " +
                    "JOIN books b ON bh.book_id = b.book_id " +
                    "WHERE bh.status = 'Borrowed'";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("history_id"), // Store book_id but keep it hidden
                    rs.getString("book_title"),
                    rs.getString("user_name"),
                    rs.getDate("borrow_date"),
                    rs.getDate("return_date"),
                    rs.getString("status"),
                    "Actions"
                };
                model.addRow(row);
            }
            rs.close();
            stmt.close();
            conn.close();

            // Hide the book_id column
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
