package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import CPUVisualizer.src.algo_ui.GanttBlock;

public class GanttChartPanel extends JPanel {
    private List<GanttBlock> blocks;

    public void setBlocks(List<GanttBlock> blocks) {
        this.blocks = blocks;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blocks == null) || blocks.isEmpty()) return;

        int x = 20;
        for (GanttBlock b : blocks) {
            int width = (b.end - b.start) * 40;

            //for dynamic color based on PID
            g.setColor(new Color(100 + (b.pid * 30) % 155, 150, 255));
            g.fillRect(x, 50, width, 40);

            g.setColor(Color.BLACK);
            g.drawRect(x, 50, width, 40);
            g.drawString("P" + b.pid, x + width / 2 - 10, 75);
            g.drawString("" + b.start, x - 5, 100);

            x += width;
        }

        //final end time label
            g.drawString("" + blocks.get(blocks.size() - 1).end, x - 5, 100);
        }
    }