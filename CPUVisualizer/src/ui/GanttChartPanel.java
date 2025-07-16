package CPUVisualizer.src.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Simple GanttBlock class
class GanttBlock {
    public int pid;
    public int start;
    public int end;

    public GanttBlock(int pid, int start, int end) {
        this.pid = pid;
        this.start = start;
        this.end = end;
    }
}

public class GanttChartPanel extends JPanel {
    private List<GanttBlock> blocks;

    public void setBlocks(List<GanttBlock> blocks) {
        this.blocks = blocks;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blocks == null) return;

        int x = 20;
        for (GanttBlock b : blocks) {
            int width = (b.end - b.start) * 40;

            //dynamic color based on PID
            g.setColor(new Color(100 + (b.pid * 30) % 155, 150, 255));
            g.fillRect(x, 50, width, 40);

            g.setColor(Color.BLACK);
            g.drawRect(x, 50, width, 40);
            g.drawString("P" + b.pid, x + width / 2 - 10, 75);
            g.drawString("" + b.start, x - 5, 100);

            x += width;
        }

        //final end time label
        if (!blocks.isEmpty()) {
            g.drawString("" + blocks.get(blocks.size() - 1).end, x - 5, 100);
        }
    }
}
