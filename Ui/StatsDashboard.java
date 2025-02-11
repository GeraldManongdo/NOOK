import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.CategoryAxis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatsDashboard extends JScrollPane {
	private DashboardPanel dashboardPanel;
    private JComboBox<Integer> yearComboBox;
    private ChartPanel chartPanel;
    private JPanel panel_3;
    private JLabel historyCountLabel, usersCountLabel, booksCountLabel, penaltyCountLabel;

    public StatsDashboard() {
    	this.dashboardPanel = dashboardPanel;
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().setUnitIncrement(16);
        setPreferredSize(new Dimension(1080, 720));

        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(1080, 820)); 
        setViewportView(contentPanel);
        contentPanel.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(10, 50));
        contentPanel.add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(0, 1, 0, 0));

        JLabel lblNewLabel = new JLabel("Dashboard");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 25));
        panel.add(lblNewLabel);

        JPanel MainDashboardPanel = new JPanel();
        contentPanel.add(MainDashboardPanel);
        MainDashboardPanel.setLayout(new BorderLayout(0, 0));
        
        // Number Analysis in Database
        JPanel panel_4 = new JPanel();
        panel_4.setPreferredSize(new Dimension(10, 120));
        MainDashboardPanel.add(panel_4, BorderLayout.NORTH);
        panel_4.setLayout(new GridLayout(0, 4, 10, 10)); 
        panel_4.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        historyCountLabel = new JLabel("0", SwingConstants.CENTER);
        usersCountLabel = new JLabel("0", SwingConstants.CENTER);
        booksCountLabel = new JLabel("0", SwingConstants.CENTER);
        penaltyCountLabel = new JLabel("0", SwingConstants.CENTER);

        panel_4.add(createCard("History Transactions", new Color(70, 130, 180), historyCountLabel));
        panel_4.add(createCard("Users", new Color(34, 139, 34), usersCountLabel));
        panel_4.add(createCard("Books", new Color(178, 34, 34), booksCountLabel));
        panel_4.add(createCard("Penalties", new Color(178, 34, 34), penaltyCountLabel));
        updateCounts();
     
        // Right side in Main panel for legend
        panel_3 = new JPanel();
        panel_3.setBackground(Color.WHITE);
        panel_3.setPreferredSize(new Dimension(220, 10));
        MainDashboardPanel.add(panel_3, BorderLayout.EAST);
        panel_3.setLayout(null);
        
        JLabel lblNewLabel_1 = new JLabel("Filter: ");
        lblNewLabel_1.setBounds(0, 0, 300, 67);
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel_3.add(lblNewLabel_1);

        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(0, 67, 300, 50);
        controlPanel.setBackground(Color.WHITE);
        yearComboBox = new JComboBox<>();
        yearComboBox.setBounds(10, 11, 150, 30);
        yearComboBox.setPreferredSize(new Dimension(150, 30));

        for (int year = 2024; year <= 2030; year++) { 
            yearComboBox.addItem(year);
        }
        yearComboBox.addActionListener(e -> updateChart());
        controlPanel.setLayout(null);
        controlPanel.add(yearComboBox);
        controlPanel.setPreferredSize(new Dimension(20, 50));
        panel_3.add(controlPanel);

        // Add legend to panel_3
        panel_3.add(createLegendPanel());
        
        // Left side in Main panel for the chart
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.WHITE);
        MainDashboardPanel.add(panel_2, BorderLayout.CENTER);
        chartPanel = new ChartPanel(createChart(createDataset(2024)));
        chartPanel.setPreferredSize(new Dimension(780, 420));
        panel_2.add(chartPanel, BorderLayout.CENTER);
        
        // Bottom side where i put Tables
        JPanel panel_5 = new JPanel();
        panel_5.setPreferredSize(new Dimension(10, 200));
        panel_5.setLayout(new GridLayout(1, 3, 10, 0)); // 3 columns
        panel_5.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        MainDashboardPanel.add(panel_5, BorderLayout.SOUTH);

        // Books Table
        String[] bookColumns = {"Book ID", "Title", "Pages"};
        DefaultTableModel bookModel = new DefaultTableModel(bookColumns, 0);
        JTable bookTable = new JTable(bookModel);
        bookTable.setRowHeight(20); // Adjust row height
        fetchTableData(bookModel, "SELECT book_id, title, pages FROM books LIMIT 5");
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        panel_5.add(bookScrollPane);
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single-click event
                    dashboardPanel.loadBooksDashboard();
                }
            }
        });

        // Users Table
        String[] userColumns = {"User ID", "Name", "Role"};
        DefaultTableModel userModel = new DefaultTableModel(userColumns, 0);
        JTable userTable = new JTable(userModel);
        userTable.setRowHeight(20);
        fetchTableData(userModel, "SELECT student_id, name, role FROM users LIMIT 5");
        JScrollPane userScrollPane = new JScrollPane(userTable);
        panel_5.add(userScrollPane);

        // Penalties Table
        String[] penaltyColumns = {"Penalty ID", "Reason", "Status"};
        DefaultTableModel penaltyModel = new DefaultTableModel(penaltyColumns, 0);
        JTable penaltyTable = new JTable(penaltyModel);
        penaltyTable.setRowHeight(20);
        fetchTableData(penaltyModel, "SELECT penalty_id, reason, status FROM penalty LIMIT 5");
        JScrollPane penaltyScrollPane = new JScrollPane(penaltyTable);
        panel_5.add(penaltyScrollPane);
        penaltyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // Single-click event
                    dashboardPanel.loadPenaltyDashboard();
                }
            }
        });


    }
    
    
    private void fetchTableData(DefaultTableModel model, String query) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[rs.getMetaData().getColumnCount()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    // Starting of Stats of database tables
    private JPanel createCard(String title, Color color, JLabel countLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        countLabel.setFont(new Font("Arial", Font.BOLD, 30));
        countLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(title, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setForeground(Color.WHITE);

        panel.add(countLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateCounts() {
        updateCountFromTable("borrow_history", historyCountLabel);
        updateCountFromTable("users", usersCountLabel);
        updateCountFromTable("books", booksCountLabel);
        updateCountFromTable("penalty", penaltyCountLabel);
    }

    private void updateCountFromTable(String tableName, JLabel label) {
        String query = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                label.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            label.setText("Error");
            e.printStackTrace();
        }
    }
   // Ending of Stats of database tables
    
    
    // Starting of graph in center of main panel
    //--this create the text in right side
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel();
        legendPanel.setBounds(0, 118, 300, 67);
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setPreferredSize(new Dimension(10, 100));
        GridLayout gl_legendPanel = new GridLayout(2, 1);
        gl_legendPanel.setVgap(20);
        gl_legendPanel.setHgap(10);
        legendPanel.setLayout(gl_legendPanel); 
        legendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel booksLabel = new JLabel("■ Books Borrowed");
        booksLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        booksLabel.setForeground(Color.RED);

        JLabel penaltiesLabel = new JLabel("■ Penalties Issued");
        penaltiesLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        penaltiesLabel.setForeground(Color.BLUE);

        legendPanel.add(booksLabel);
        legendPanel.add(penaltiesLabel);

        return legendPanel;
    }

    //--this for getting for data
    private CategoryDataset createDataset(int year) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Integer> borrowedBooksData = getBorrowedBooksData(year);
        Map<String, Integer> penaltyData = getPenaltyData(year);

        for (String month : borrowedBooksData.keySet()) {
            dataset.addValue(borrowedBooksData.getOrDefault(month, 0), "Books Borrowed", month);
            dataset.addValue(penaltyData.getOrDefault(month, 0), "Penalties Issued", month);
        }

        return dataset;
    }
    
    //-- this is for createChart
    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Number of Borrowed Books in " + yearComboBox.getSelectedItem(), 
                "Month",
                "Count",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                false, // Disable default legend
                true,
                false
        );

        return chart;
    }
    
    //--this is for update chart
    private void updateChart() {
        int selectedYear = (int) yearComboBox.getSelectedItem();
        chartPanel.setChart(createChart(createDataset(selectedYear)));
    }
    
    //--this is for bar of total borrowed books each month
    private Map<String, Integer> getBorrowedBooksData(int year) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT MONTHNAME(borrow_date) AS month, COUNT(*) AS count " +
                       "FROM library.borrow_history WHERE YEAR(borrow_date) = ? " +
                       "GROUP BY MONTH(borrow_date), MONTHNAME(borrow_date) " +
                       "ORDER BY MONTH(borrow_date)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = (conn != null) ? conn.prepareStatement(query) : null) {
            if (stmt != null) {
                stmt.setInt(1, year);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        data.put(rs.getString("month"), rs.getInt("count"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    //--this is for bar of total penalty each month
    private Map<String, Integer> getPenaltyData(int year) {
        Map<String, Integer> data = new LinkedHashMap<>();
        String query = "SELECT MONTHNAME(date_issued) AS month, COUNT(*) AS count " +
                       "FROM library.penalty WHERE YEAR(date_issued) = ? " +
                       "GROUP BY MONTH(date_issued), MONTHNAME(date_issued) " +
                       "ORDER BY MONTH(date_issued)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = (conn != null) ? conn.prepareStatement(query) : null) {
            if (stmt != null) {
                stmt.setInt(1, year);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        data.put(rs.getString("month"), rs.getInt("count"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    // End of graph in center of main panel
}
