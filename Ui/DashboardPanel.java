import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnDashboard.setBackground(Color.decode("#f3f3f7"));
        btnDashboard.setBorder(null);
        btnDashboard.setForeground(Color.decode("#32418c"));
        Navigation.add(btnDashboard);
        btnDashboard.addActionListener(e -> reloadDashboard());
        
        JButton btnBook = new JButton("Book");
        btnBook.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnBook.setBorder(null);
        btnBook.setBackground(Color.decode("#f3f3f7"));
        btnBook.setForeground(Color.decode("#32418c"));
        Navigation.add(btnBook);
        btnBook.addActionListener(e -> {
            Maindashboard.removeAll();
            BookDashboard bookDashboard = new BookDashboard();
            Maindashboard.add(bookDashboard, BorderLayout.CENTER);
            Maindashboard.revalidate();
            Maindashboard.repaint();
        });


        JButton btnHistory = new JButton("Records");
        btnHistory.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnHistory.setBorder(null);
        btnHistory.setBackground(Color.decode("#f3f3f7"));
        btnHistory.setForeground(Color.decode("#32418c"));
        Navigation.add(btnHistory);
        btnHistory.addActionListener(e -> {
            Maindashboard.removeAll();
            RecordTablesPanel recordTablesPanel = new RecordTablesPanel();
            Maindashboard.add(recordTablesPanel, BorderLayout.CENTER);
            Maindashboard.revalidate();
            Maindashboard.repaint();
        });
        

        // Footer Navigation
        JPanel footerNav = new JPanel();
        footerNav.setBackground(Color.WHITE);
        footerNav.setPreferredSize(new Dimension(10, 100));
        sidenav.add(footerNav, BorderLayout.SOUTH);
        footerNav.setLayout(new GridLayout(2, 1, 0, 0));
        footerNav.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton logoutButton = new JButton("Log out");
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
    
   

    

}
