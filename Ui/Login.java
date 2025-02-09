
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.ComponentOrientation;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.sql.Statement;

public class Login extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel LoginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Main mainFrame;
    
    Connection conn = DatabaseConnection.getConnection();

    public Login(Main mainFrame) {
        // Getting the Frame from the Main.java
        this.mainFrame = mainFrame;

        // Main Panel in Login
        LoginPanel = new JPanel();
        setLayout(new BorderLayout());
        add(LoginPanel, BorderLayout.CENTER);
        LoginPanel.setLayout(new BorderLayout(0, 0));

        // Left side panel
        JPanel LoginBackground = new JPanel();
        LoginPanel.add(LoginBackground);
        LoginBackground.setLayout(null);
        ImageIcon icon = new ImageIcon(getClass().getResource("LoginBackground.png"));
        Image scaledIcon = icon.getImage().getScaledInstance(940, 540, Image.SCALE_SMOOTH);

        JLabel lblNewLabel_1 = new JLabel("");
        ImageIcon icon2 = new ImageIcon(getClass().getResource("NU-logo.png"));
        Image scaledIcon2 = icon2.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        lblNewLabel_1.setIcon(new ImageIcon(scaledIcon2));
        lblNewLabel_1.setBounds(161, 111, 250, 283);
        LoginBackground.add(lblNewLabel_1);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setBounds(0, 0, 615, 540);
        LoginBackground.add(lblNewLabel);
        lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        lblNewLabel.setIcon(new ImageIcon(scaledIcon));

        // Right side panel
        JPanel LoginForm = new JPanel();
        LoginForm.setPreferredSize(new Dimension(350, 10));
        LoginPanel.add(LoginForm, BorderLayout.EAST);
        LoginForm.setLayout(new BorderLayout(0, 10));

        JPanel FormHeader = new JPanel();
        FormHeader.setPreferredSize(new Dimension(10, 200));
        LoginForm.add(FormHeader, BorderLayout.NORTH);
        FormHeader.setLayout(new BorderLayout(0, 0));

        JLabel NookHeaderImg = new JLabel("");
        NookHeaderImg.setHorizontalAlignment(SwingConstants.CENTER);
        NookHeaderImg.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        ImageIcon HeaderImg = new ImageIcon(getClass().getResource("NOOK-logo.png"));
        Image HeaderImg2 = HeaderImg.getImage().getScaledInstance(200, 120, Image.SCALE_SMOOTH);
        NookHeaderImg.setIcon(new ImageIcon(HeaderImg2));
        NookHeaderImg.setBounds(174, 86, 250, 283);
        FormHeader.add(NookHeaderImg, BorderLayout.CENTER);

        JPanel NookHeaderText = new JPanel();
        NookHeaderText.setPreferredSize(new Dimension(10, 40));
        FormHeader.add(NookHeaderText, BorderLayout.SOUTH);
        NookHeaderText.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel_3 = new JLabel("Sign up to start Browsing the Library");
        lblNewLabel_3.setForeground(Color.decode("#0a003b"));
        lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblNewLabel_3.setHorizontalTextPosition(SwingConstants.LEFT);
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
        NookHeaderText.add(lblNewLabel_3, BorderLayout.SOUTH);

        JLabel lblNewLabel_4 = new JLabel("WELCOME!");
        lblNewLabel_4.setForeground(Color.decode("#0a003b"));
        lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_4.setHorizontalTextPosition(SwingConstants.CENTER);
        lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
        NookHeaderText.add(lblNewLabel_4, BorderLayout.CENTER);

        // Form Starting
        JPanel Form = new JPanel();
        LoginForm.add(Form, BorderLayout.CENTER);
        Form.setLayout(null);

        JPanel panel_2 = new JPanel();
        panel_2.setBounds(63, 24, 231, 45);
        Form.add(panel_2);
        panel_2.setLayout(null);
        panel_2.setBackground(Color.WHITE);

        JLabel lblNewLabel_2 = new JLabel("Email:");
        lblNewLabel_2.setForeground(Color.decode("#0a003b"));
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblNewLabel_2.setBounds(10, 0, 46, 14);
        panel_2.add(lblNewLabel_2);

        usernameField = new JTextField();
        usernameField.setForeground(Color.decode("#0a003b"));
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        usernameField.setPreferredSize(new Dimension(7, 19));
        usernameField.setColumns(10);
        usernameField.setBorder(null);
        usernameField.setBounds(10, 14, 211, 29);
        panel_2.add(usernameField);

        JPanel panel_2_1 = new JPanel();
        panel_2_1.setLayout(null);
        panel_2_1.setBackground(Color.WHITE);
        panel_2_1.setBounds(63, 80, 231, 45);
        Form.add(panel_2_1);

        JLabel lblNewLabel_2_1 = new JLabel("Password:");
        lblNewLabel_2_1.setForeground(Color.decode("#0a003b"));
        lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblNewLabel_2_1.setBounds(10, 0, 148, 14);
        panel_2_1.add(lblNewLabel_2_1);

        passwordField = new JPasswordField();
        passwordField.setForeground(Color.decode("#0a003b"));
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        passwordField.setBorder(null);
        passwordField.setBounds(10, 14, 211, 31);
        panel_2_1.add(passwordField);

        JCheckBox chckbxNewCheckBox = new JCheckBox("Remember me");
        chckbxNewCheckBox.setVerticalTextPosition(SwingConstants.BOTTOM);
        chckbxNewCheckBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
        chckbxNewCheckBox.setBounds(65, 126, 139, 23);
        Form.add(chckbxNewCheckBox);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBackground(Color.decode("#0a003b"));
        loginButton.setBorder(null);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 15));
        loginButton.setBounds(63, 169, 231, 38);
        Form.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, new String(password));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        mainFrame.switchToMainFrame();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });

        // Form Ended
    }
}