import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    public static JPanel Maindashboard = new JPanel();
    private JFrame parentFrame; // Reference to parent frame

    public DashboardPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame; // Store frame reference

        setPreferredSize(new Dimension(1280, 720));
        setLayout(new BorderLayout(0, 0));

        // Side Navigation
        JPanel sidenav = new JPanel();
        sidenav.setBackground(Color.WHITE);
        sidenav.setPreferredSize(new Dimension(200, 10));
        add(sidenav, BorderLayout.WEST);
        sidenav.setLayout(new BorderLayout(0, 0));

        // Header of the Navigation
        JPanel headingNav = new JPanel();
        headingNav.setBackground(Color.WHITE);
        headingNav.setPreferredSize(new Dimension(10, 100));
        sidenav.add(headingNav, BorderLayout.NORTH);
        headingNav.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon HeaderImgNav = new ImageIcon(getClass().getResource("NOOK-logo.png"));
        Image HeaderImgNav2 = HeaderImgNav.getImage().getScaledInstance(150, 80, Image.SCALE_SMOOTH);
        lblNewLabel.setIcon(new ImageIcon(HeaderImgNav2));
        headingNav.add(lblNewLabel, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel Navigation = new JPanel();
        Navigation.setBackground(Color.WHITE);
        Navigation.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidenav.add(Navigation, BorderLayout.CENTER);
        Navigation.setLayout(new GridLayout(8, 0, 0, 10));

        //Dashboard Button
        RoundedPanel panelDashboard = new RoundedPanel(10);
        panelDashboard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelDashboard.setBackground(Color.decode("#f3f3f7"));
        Navigation.add(panelDashboard);
        panelDashboard.setLayout(null);
        
        JLabel DashboardLabelIcon = new JLabel("");
        DashboardLabelIcon.setBounds(0, 0, 80, 51);
        DashboardLabelIcon.setHorizontalAlignment(SwingConstants.CENTER);
        DashboardLabelIcon.setIcon(new ImageIcon(getClass().getResource("dashboard-solid-24.png")));
        panelDashboard.add(DashboardLabelIcon);
        
        JLabel DashboardLabel = new JLabel("Dashboard");
        DashboardLabel.setForeground(Color.decode("#32418c"));
        DashboardLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        DashboardLabel.setBounds(58, 0, 102, 51);
        panelDashboard.add(DashboardLabel);
        panelDashboard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	reloadDashboard();
            }
        });
        

        //Book Button
        RoundedPanel panelBook = new RoundedPanel(10);
        panelBook.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelBook.setBackground(Color.decode("#f3f3f7"));
        Navigation.add(panelBook);
        panelBook.setLayout(null);
        
        JLabel BookLabelIcon = new JLabel("");
        BookLabelIcon.setBounds(0, 0, 80, 51);
        BookLabelIcon.setHorizontalAlignment(SwingConstants.CENTER);
        BookLabelIcon.setIcon(new ImageIcon(getClass().getResource("book-open-solid-24.png")));
        panelBook.add(BookLabelIcon);
        
        JLabel BookLabel = new JLabel("Book");
        BookLabel.setForeground(Color.decode("#32418c"));
        BookLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        BookLabel.setBounds(59, 0, 101, 51);
        panelBook.add(BookLabel);
        panelBook.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Maindashboard.removeAll();
                BookDashboard bookDashboard = new BookDashboard();
                Maindashboard.add(bookDashboard, BorderLayout.CENTER);
                Maindashboard.revalidate();
                Maindashboard.repaint();
            }
        });
        
        //History Button
        RoundedPanel panelHistory = new RoundedPanel(10);
        panelHistory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelHistory.setBackground(Color.decode("#f3f3f7"));
        Navigation.add(panelHistory);
        panelHistory.setLayout(null);
        
        JLabel HistoryLabelIcon = new JLabel("");
        HistoryLabelIcon.setBounds(0, 0, 80, 51);
        HistoryLabelIcon.setHorizontalAlignment(SwingConstants.CENTER);
        HistoryLabelIcon.setIcon(new ImageIcon(getClass().getResource("receipt-solid-24.png")));
        panelHistory.add(HistoryLabelIcon);
        
        JLabel HistoryLabel = new JLabel("Records");
        HistoryLabel.setForeground(Color.decode("#32418c"));
        HistoryLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        HistoryLabel.setBounds(58, 0, 102, 51);
        panelHistory.add(HistoryLabel);
        panelHistory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Maindashboard.removeAll();
                RecordTablesPanel recordTablesPanel = new RecordTablesPanel();
                Maindashboard.add(recordTablesPanel, BorderLayout.CENTER);
                Maindashboard.revalidate();
                Maindashboard.repaint();
            }
        });
        
        //Transaction Button
        RoundedPanel panelTransaction = new RoundedPanel(10);
        panelTransaction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelTransaction.setBackground(Color.decode("#f3f3f7"));
        Navigation.add(panelTransaction);
        panelTransaction.setLayout(null);
        
        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setBounds(0, 0, 80, 51);
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setIcon(new ImageIcon(getClass().getResource("book-add-solid-24.png")));
        panelTransaction.add(lblNewLabel_1);
        
        JLabel lblNewLabel_2 = new JLabel("Transaction");
        lblNewLabel_2.setForeground(Color.decode("#32418c"));
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel_2.setBounds(59, 0, 101, 51);
        panelTransaction.add(lblNewLabel_2);
        panelTransaction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadTransactionDashboard();
            }
        });
        

        // Footer Navigation
        JPanel footerNav = new JPanel();
        footerNav.setBackground(Color.WHITE);
        footerNav.setPreferredSize(new Dimension(10, 100));
        sidenav.add(footerNav, BorderLayout.SOUTH);
        footerNav.setBorder(new EmptyBorder(20, 20, 20, 20));
        footerNav.setLayout(null);

        RoundedButton logoutButton = new RoundedButton("Log Out");
        logoutButton.setBounds(20, 30, 160, 39);
        logoutButton.setPreferredSize(new Dimension(70, 50));
        logoutButton.setBackground(Color.decode("#0a003b"));
        logoutButton.setBorder(null);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Tahoma", Font.BOLD, 15));
        footerNav.add(logoutButton);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main dashboard = new Main();
                dashboard.setVisible(true);
                parentFrame.dispose();
            }
        });

        // Setting Default content for Maindashboard
        Maindashboard.setForeground(Color.decode("#f3f3f7"));
        Maindashboard.setLayout(new BorderLayout(0, 0));
        add(Maindashboard, BorderLayout.CENTER);

        // Load initial dashboard content
        reloadDashboard();
    }

    // Reload Dashboard when AddBookFrame closes
    public static void reloadDashboard() {
        Maindashboard.removeAll();
        StatsDashboard statsDashboard = new StatsDashboard();
        Maindashboard.add(statsDashboard, BorderLayout.CENTER);
        Maindashboard.revalidate();
        Maindashboard.repaint();
    }
    public static void loadBooksDashboard() {
        Maindashboard.removeAll();
        BookDashboard bookDashboard = new BookDashboard();
        Maindashboard.add(bookDashboard, BorderLayout.CENTER);
        Maindashboard.revalidate();
        Maindashboard.repaint();
    }
    public static void loadTransactionDashboard() {
        Maindashboard.removeAll();
        TransactionPanel transactionPanel = new TransactionPanel();
        Maindashboard.add(transactionPanel, BorderLayout.CENTER);
        Maindashboard.revalidate();
        Maindashboard.repaint();
    }
    
   

    

}
