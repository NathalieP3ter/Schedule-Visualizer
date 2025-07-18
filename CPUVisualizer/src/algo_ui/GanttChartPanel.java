package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class GanttChartPanel extends JPanel {
    public enum Style { FCFS, SJF, SRTF, RR, MLFQ }

    private List<GanttBlock> blocks = new ArrayList<>();
    private int animationDelay = 200;
    private Style currentStyle = Style.FCFS;

    private static final int BLOCK_WIDTH = 40;
    private static final int BLOCK_HEIGHT = 40;
    private static final int MARGIN = 20;

    private Map<Integer, Color> processColors = new HashMap<>();

    public GanttChartPanel() {
        setPreferredSize(new Dimension(800, 100));
        setBackground(Color.WHITE);
    }

    public void setBlocksInstant(List<GanttBlock> newBlocks) {
        this.blocks = new ArrayList<>(newBlocks);
        updatePreferredSize();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int totalWidth = 50;
        if (blocks != null) {
            for (GanttBlock b : blocks) {
                totalWidth += (b.end - b.start) * blockScale;
            }
        }
        return new Dimension(Math.max(totalWidth, 800), 150);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ganttData == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < animatedBlockCount && i < blocks.size(); i++) {
            GanttBlock b = blocks.get(i);
            int width = (b.end - b.start) * blockScale;

            g2d.setColor(getColorForStyle());
            g2d.fill(new RoundRectangle2D.Double(x, y, width, height, 15, 15));

            g2d.setColor(Color.BLACK);
            g2d.draw(new RoundRectangle2D.Double(x, y, width, height, 15, 15));
            g2d.drawString("P" + b.pid, x + width / 2 - 12, y + height / 2);
            g2d.drawString("" + b.start, x - 5, y + height + 25);

            x += width;
        }

        if (animatedBlockCount > 0 && animatedBlockCount <= blocks.size()) {
            int finalEnd = blocks.get(animatedBlockCount - 1).end;
            g2d.drawString("" + finalEnd, x - 5, y + height + 25);
        }
    }

    private Color getColorForStyle() {
        return switch (style) {
            case SJF   -> new Color(204, 229, 255);
            case RR    -> new Color(255, 255, 204);
            case SRTF  -> new Color(255, 204, 229);
            case MLFQ  -> new Color(204, 204, 255);
            case FCFS  -> new Color(204, 255, 204);
        };
    }
}
