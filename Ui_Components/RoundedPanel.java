import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private boolean hovered = false;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        setOpaque(false); // So the background is transparent and the rounded corners are visible

        // Add mouse listener to detect hover state
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());

        // Draw the rounded rectangle
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Draw the border if hovered
        if (hovered) {
            g2d.setColor(Color.decode("#32418C")); // Set the border color
            g2d.setStroke(new BasicStroke(2)); // Set the border thickness
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }
}