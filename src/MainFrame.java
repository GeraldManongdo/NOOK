import java.awt.Image;
import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
    	setResizable(false);
        setTitle("NOOK - Dashboard");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("NOOK-icon.png")).getImage());

        // Pass the MainFrame Frame (this) to Login
        setContentPane(new DashboardPanel(this));

        setVisible(true);
    }
}
