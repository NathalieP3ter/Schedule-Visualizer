package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<SchedulerLogic.GanttBlock> blocks;
    private int currentAnimationIndex = 0;

    public void setGanttBlocks(List<SchedulerLogic.GanttBlock> blocks) {
        this.blocks = blocks;
        this.currentAnimationIndex = 0;
        repaint();
        animateBlocks();
    }

    private void animateBlocks() {
        Timer timer = new Timer(500, e -> {
            if (currentAnimationIndex < blocks.size()) {
                currentAnimationIndex++;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blocks == null || blocks.isEmpty()) return;

        int blockWidth = 60;
        int blockHeight = 40;
        int gap = 10;
        int x = 10;
        int y = 20;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < currentAnimationIndex; i++) {
            SchedulerLogic.GanttBlock block = blocks.get(i);
            int width = (block.end - block.start) * blockWidth / 2;
            g2.setColor(new Color(100 + (i * 30) % 155, 100, 200));
            g2.fillRect(x, y, width, blockHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, width, blockHeight);
            g2.drawString(block.pid, x + width / 2 - 10, y + 25);
            g2.drawString("" + block.start, x, y + blockHeight + 15);
            x += width + gap;
        }

        // Draw the final end time
        if (currentAnimationIndex == blocks.size() && !blocks.isEmpty()) {
            SchedulerLogic.GanttBlock last = blocks.get(blocks.size() - 1);
            int finalX = x - gap;
            g2.drawString("" + last.end, finalX, y + blockHeight + 15);
        }
    }
}
