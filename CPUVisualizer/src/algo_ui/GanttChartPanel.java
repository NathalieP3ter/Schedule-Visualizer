package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<GanttBlock> blocks;

    public enum Style{
        FCFS, SJF, RR, SRTF
    }
    private Style style = Style.FCFS;

   public void setBlocks(List<GanttBlock> blocks) {
        this.blocks = blocks;
        repaint();
    }

    public void setStyle(Style style) {
        this.style = style;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blocks == null) return;

        int x = 50;
        int y = 50;
        int height = 50;
        int scale = 30;

        for (GanttBlock block : blocks) {
            int width = (block.end - block.start) * scale;

            g.setColor(getColorForStyle());
            g.fillRect(x, y, width, height);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            g.drawString("P" + block.pid, x + width / 2 - 10, y + height / 2);
            g.drawString(String.valueOf(block.start), x, y + height + 15);

            x += width;
        }

        if (!blocks.isEmpty()) {
            g.drawString(String.valueOf(blocks.get(blocks.size() - 1).end), x, y + height + 15);
        }
    }

    private Color getColorForStyle() {
        switch (style) {
            case SJF: return new Color(204, 229, 255);      // light blue
            case RR: return new Color(255, 255, 204);        // soft yellow
            case SRTF: return new Color(255, 204, 229);      // pink
            case FCFS:
            default: return new Color(204, 255, 204);        // light green
        }
    }
}