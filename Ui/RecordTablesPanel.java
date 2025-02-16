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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class RecordTablesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTable historyTable;
    private JTable penaltyTable;
    private JTable userTable;
    
	public RecordTablesPanel() {
		
	       setLayout(new BorderLayout());

	        // Top Panel with Image Header
	        JPanel panel = new JPanel();
	        panel.setPreferredSize(new Dimension(10, 150));
	        panel.setLayout(new BorderLayout(0, 0));

	        JLabel lblNewLabel_1 = new JLabel("");
	        ImageIcon HeaderImg = new ImageIcon(getClass().getResource("libraryHeading.png"));
	        Image HeaderImg2 = HeaderImg.getImage().getScaledInstance(1080, 500, Image.SCALE_SMOOTH);
	        lblNewLabel_1.setIcon(new ImageIcon(HeaderImg2));
	        panel.add(lblNewLabel_1, BorderLayout.CENTER);

	        add(panel, BorderLayout.NORTH);
	        
	        // Create a tabbed pane
	        JTabbedPane tabbedPane = new JTabbedPane();
	        tabbedPane.setUI(new BasicTabbedPaneUI() {
	            @Override
	            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
	                // Override to remove border
	            }
	        });

	        // Create panels for each tab
	        JPanel panel1 = new JPanel();
	        panel1.setBackground(Color.WHITE);
	        panel1.setLayout(new BorderLayout(0, 0));
	        JLabel lblNewLabel = new JLabel("History Record");
	        lblNewLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
	        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
	        panel1.add(lblNewLabel, BorderLayout.NORTH);
	        // Initialize Table
	        String[] columnNames = {"History ID", "User Name", "Book Name", "Borrow Date", "Return Date"};
	        model = new DefaultTableModel(columnNames, 0);
	        historyTable = new JTable(model);
	        historyTable.setRowHeight(30);

	        JScrollPane scrollPane = new JScrollPane(historyTable);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	        panel1.add(scrollPane);
	        // Load History from database
	        loadHistoryFromDatabase();

	        JPanel panel2 = new JPanel();
	        panel2.setBackground(Color.WHITE);
	        panel2.setLayout(new BorderLayout(0, 0));
	        JLabel lblNewLabel2 = new JLabel("Penalty Record");
	        lblNewLabel2.setBorder(new EmptyBorder(10, 10, 10, 10));
	        lblNewLabel2.setFont(new Font("Tahoma", Font.BOLD, 20));
	        panel2.add(lblNewLabel2, BorderLayout.NORTH);
	        // Initialize Table
	        String[] columnNames2 = {"History ID", "User Name", "Book Name","Penalty amount", "Reason", "Status", "Date issued"};
	        model = new DefaultTableModel(columnNames2, 0);
	        penaltyTable = new JTable(model);
	        penaltyTable.setRowHeight(30);

	        JScrollPane scrollPane2 = new JScrollPane(penaltyTable);
	        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane2.getVerticalScrollBar().setUnitIncrement(16);
	        panel2.add(scrollPane2);

	        // Load Penalty from database
	        loadPenaltyFromDatabase();
	        
	        JPanel panel3 = new JPanel();
	        panel3.setBackground(Color.WHITE);
	        panel3.setLayout(new BorderLayout(0, 0));
	        JLabel lblNewLabel3 = new JLabel("User List");
	        lblNewLabel3.setBorder(new EmptyBorder(10, 10, 10, 10));
	        lblNewLabel3.setFont(new Font("Tahoma", Font.BOLD, 20));
	        panel3.add(lblNewLabel3, BorderLayout.NORTH);
	        // Initialize Table
	        String[] columnNames3 = {"Student ID", "Name", "Email","Section", "No#"};
	        model = new DefaultTableModel(columnNames3, 0);
	        userTable = new JTable(model);
	        userTable.setRowHeight(30);

	        JScrollPane scrollPane3 = new JScrollPane(userTable);
	        scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        scrollPane3.getVerticalScrollBar().setUnitIncrement(16);
	        panel3.add(scrollPane3);

	        // Load user from database
	        loadUserFromDatabase();
	        

	        // Add tabs to the panel
	        tabbedPane.addTab("History ", panel1);
	        tabbedPane.addTab("Penalty ", panel2);
	        tabbedPane.addTab("User ", panel3);
	        add(tabbedPane, BorderLayout.CENTER);

	}
	 private void loadHistoryFromDatabase() {
	        try (Connection conn = DatabaseConnection.getConnection()) {
	            // Optimized Query with JOIN
	        	String query = "SELECT bh.history_id, u.name AS user_name, b.title AS book_title, " +
	                    "bh.borrow_date, bh.return_date, bh.status " +
	                    "FROM history bh " +
	                    "JOIN users u ON bh.user_id = u.user_id " +
	                    "JOIN books b ON bh.book_id = b.book_id " +
	                    "WHERE bh.status = 'Returned'";

	            try (PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {

	                while (rs.next()) {
	                    Object[] row = {
	                        rs.getInt("history_id"),
	                        rs.getString("user_name"),
	                        rs.getString("book_title"),
	                        rs.getDate("borrow_date"),
	                        rs.getDate("return_date"),
	                    };
	                    model.addRow(row);
	                }
	            }

	            // Hide the History ID column
	            historyTable.getColumnModel().getColumn(0).setMinWidth(0);
	            historyTable.getColumnModel().getColumn(0).setMaxWidth(0);
	            historyTable.getColumnModel().getColumn(0).setWidth(0);

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error loading history: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    private void loadPenaltyFromDatabase() {
	        try (Connection conn = DatabaseConnection.getConnection()) {
	            // Optimized Query with JOIN
	            String query = "SELECT p.penalty_id, u.name AS user_name, b.title AS book_title, " +
	                           "p.amount, p.amount, p.reason, p.status, p.date_issued " +
	                           "FROM penalty p " +
	                           "JOIN users u ON p.user_id = u.user_id " +
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
	            penaltyTable.getColumnModel().getColumn(0).setMinWidth(0);
	            penaltyTable.getColumnModel().getColumn(0).setMaxWidth(0);
	            penaltyTable.getColumnModel().getColumn(0).setWidth(0);

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error loading Penalty: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    private void loadUserFromDatabase() {
	        try (Connection conn = DatabaseConnection.getConnection()) {
	            // Optimized Query with JOIN
	            String query = "SELECT user_id, name, email, section, number FROM users WHERE role = 'student'"; 

	            try (PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {

	                while (rs.next()) {
	                    Object[] row = {
	                        rs.getInt("user_id"),
	                        rs.getString("name"),
	                        rs.getString("email"),
	                        rs.getString("section"), 
	                        rs.getString("number"),
	                    };
	                    model.addRow(row);
	                }
	            }

	            // Hide the student_id column
	            userTable.getColumnModel().getColumn(0).setMinWidth(0); 
	            userTable.getColumnModel().getColumn(0).setMaxWidth(0);
	            userTable.getColumnModel().getColumn(0).setWidth(0);

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }



}
