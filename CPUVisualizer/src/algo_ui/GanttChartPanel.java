package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {

    public enum Style{
        FCFS, SJF, RR, SRTF
    }
    private Style style = Style.FCFS;
    private List<GanttBlock> blocks;

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
        if (blocks == null || blocks.isEmpty()) return;

        int x = 30;
        int y = 50;
        int height = 40;
        int scale = 40;


        for (GanttBlock b : blocks) {
            int width = (b.end - b.start) * scale;

            g.setColor(getColorForStyle());
            g.fillRect(x, y, width, height);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            g.drawString("P" + b.pid, x + width / 2 - 10, y + height / 2);
            g.drawString("" + b.start, x - 5, y + height + 20);

            x += width;
        }


     // Final end time label
        g.drawString("" + blocks.get(blocks.size() - 1).end, x - 5, y + height + 20);
    }

     private Color getColorForStyle() {
        switch (style) {
            case SJF: return new Color(204, 229, 255);      // Light blue
            case RR: return new Color(255, 255, 204);        // Soft yellow
            case SRTF: return new Color(255, 204, 229);      // Pink
            case FCFS:
            default: return new Color(204, 255, 204);        // Light green
        }
    }
}