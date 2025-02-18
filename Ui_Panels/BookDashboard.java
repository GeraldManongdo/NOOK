import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;

public class BookDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel model;
    private JTable table;
    
    public BookDashboard() {
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
        panel_2.setPreferredSize(new Dimension(10, 50));
        panel_1.add(panel_2, BorderLayout.NORTH);
        panel_2.setLayout(new GridLayout(1, 0, 0, 0));
        
        JLabel lblNewLabel = new JLabel("Book Management List\r\n");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblNewLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(lblNewLabel);
        
        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel_2.add(panel_3);

        panel_3.setLayout(new BorderLayout(0, 0));

        
        JButton btnAddBook = new JButton("Add Book");
        btnAddBook.setPreferredSize(new Dimension(150, 50));
        btnAddBook.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnAddBook.setBorder(null);
        btnAddBook.setBackground(Color.decode("#32418c"));
        btnAddBook.setForeground(Color.WHITE);
        btnAddBook.setFocusPainted(false);
        btnAddBook.setContentAreaFilled(false);
        btnAddBook.setOpaque(true);
        panel_3.add(btnAddBook, BorderLayout.EAST);

        // Open AddBookFrame and pass DashboardPanel reference
        btnAddBook.addActionListener(e -> {
            AddBookFrame addBookFrame = new AddBookFrame(this);
            addBookFrame.setLocationRelativeTo(null);
            addBookFrame.setVisible(true);
        });
        
        // Initialize Table
        String[] columnNames = {"Book ID", "Book Name", "Author", "Genre", "Pages", "Publication Date", "Availability", "Actions"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel_1.add(scrollPane, BorderLayout.CENTER);
        
        // Apply custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());


        // Load books from database
        loadBooksFromDatabase();
    }

    private void loadBooksFromDatabase() {
        try {
        	Connection conn = DatabaseConnection.getConnection(); 
            String query = "SELECT book_id, title, author, genre, pages, publication_date, availability FROM books";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("book_id"), // Store book_id but keep it hidden
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("pages"),
                    rs.getDate("publication_date"),
                    rs.getString("availability"),
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

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton = createStyledButton("View");
        private JButton deleteButton = createStyledButton("Delete");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
            add(viewButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        private JButton viewButton = createStyledButton("View");
        private JButton deleteButton = createStyledButton("Delete");
        private int bookId;

        public ButtonEditor() {
            viewButton.addActionListener(e -> openViewFrame(bookId));
            deleteButton.addActionListener(e -> deleteBook(bookId));
            panel.add(viewButton);
            panel.add(deleteButton);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            bookId = (int) model.getValueAt(row, 0);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
    
    private void openViewFrame(int bookId) {
        new BookViewFrame(bookId);
    }

    private void deleteBook(int bookID) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection(); // Get database connection
                String query = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, bookID);
                stmt.executeUpdate();
                stmt.close();

                // Remove the row from the table after deletion
                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((int) model.getValueAt(i, 0) == bookID) { // Find the correct row by book_id
                        model.removeRow(i);
                        break; // Exit loop after removing the correct row
                    }
                }
                DashboardPanel.reloadDashboard();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.GRAY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(70, 25));
        return button;
    }

    public void loadBooksDashboard() {
        model.setRowCount(0); // Clear existing rows
        loadBooksFromDatabase(); // Reload books from the database
        this.revalidate();
        this.repaint();
    }

}
