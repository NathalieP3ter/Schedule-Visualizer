package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<SchedulerLogic.GanttBlock> ganttData;
    private int animatedIndex = 0;
    private Timer animationTimer;

    public void setGanttData(List<SchedulerLogic.GanttBlock> data) {
        this.ganttData = data;
        this.animatedIndex = 0;
    }

    public void animateBlocks(int delay) {
        if (ganttData == null || ganttData.isEmpty()) return;

        animatedIndex = 0;
        animationTimer = new Timer(delay, e -> {
            animatedIndex++;
            repaint();
            if (animatedIndex >= ganttData.size()) {
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ganttData == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        int x = 20;
        int y = 30;
        int height = 40;
        int scale = 20;

        for (int i = 0; i < animatedIndex && i < ganttData.size(); i++) {
            SchedulerLogic.GanttBlock block = ganttData.get(i);
            int width = (block.end - block.start) * scale;
            g2.setColor(Color.CYAN);
            g2.fillRect(x, y, width, height);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, width, height);
            g2.drawString("P" + block.pid, x + width / 2 - 10, y + height / 2);
            g2.drawString("" + block.start, x - 5, y + height + 15);
            x += width;
        }

        if (animatedIndex == ganttData.size() && !ganttData.isEmpty()) {
            SchedulerLogic.GanttBlock last = ganttData.get(ganttData.size() - 1);
            g2.drawString("" + last.end, x - 5, y + height + 15);
        }
    }
}
