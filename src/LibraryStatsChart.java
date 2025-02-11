import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.CategoryAxis;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryStatsChart extends JFrame {
    private JComboBox<Integer> yearComboBox;
    private ChartPanel chartPanel;
    

    public LibraryStatsChart() {
        setTitle("Library Statistics");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel controlPanel = new JPanel();
        yearComboBox = new JComboBox<>();

        for (int year = 2024; year <= 2030; year++) { 
            yearComboBox.addItem(year);
        }
        yearComboBox.addActionListener(e -> updateChart());
        controlPanel.add(yearComboBox);

        chartPanel = new ChartPanel(createChart(createDataset(2024)));

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }

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

    private JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Number of Borrowed Books in " + yearComboBox.getSelectedItem(), 
                "Month",
                "Count",
                dataset
        );

        // Adjust bar spacing
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1); // Reduce spacing between bars (default is 0.2)

        // Adjust category spacing
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2); // Reduce space between categories (default is 0.4)

        return chart;
    }

    private void updateChart() {
        int selectedYear = (int) yearComboBox.getSelectedItem();
        chartPanel.setChart(createChart(createDataset(selectedYear)));
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryStatsChart chart = new LibraryStatsChart();
            chart.setVisible(true);
        });
    }
}
