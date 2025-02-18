import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = Color.decode("#32418C"); // Blue color
    }
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 5, 5);
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(new Color(230, 230, 230)); // Light gray track
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }


    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new JButton() { // Empty button to hide arrows
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new JButton() { // Empty button to hide arrows
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
    }
    
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(10, super.getPreferredSize(c).height); // Thicker scrollbar width
    }
}