package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
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

    public void animateBlocks(List<GanttBlock> newBlocks) {
        this.blocks.clear();
        updatePreferredSize(newBlocks);
        new Thread(() -> {
            for (GanttBlock block : newBlocks) {
                blocks.add(block);
                repaint();
                try {
                    Thread.sleep(animationDelay);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public void setAnimationDelay(int ms) {
        this.animationDelay = ms;
    }

    public void setStyle(Style style) {
        this.currentStyle = style;
    }

    private void updatePreferredSize() {
        int totalTime = blocks.isEmpty() ? 1 : blocks.get(blocks.size() - 1).end;
        int width = MARGIN * 2 + totalTime * BLOCK_WIDTH;
        setPreferredSize(new Dimension(Math.max(800, width), 100));
        revalidate();
    }

    private void updatePreferredSize(List<GanttBlock> newBlocks) {
        int totalTime = newBlocks.isEmpty() ? 1 : newBlocks.get(newBlocks.size() - 1).end;
        int width = MARGIN * 2 + totalTime * BLOCK_WIDTH;
        setPreferredSize(new Dimension(Math.max(800, width), 100));
        revalidate();
    }

    private Color getColorForProcess(int pid) {
        if (!processColors.containsKey(pid)) {
            Random rand = new Random(pid * 1000);
            Color color = new Color(100 + rand.nextInt(156), 100 + rand.nextInt(156), 100 + rand.nextInt(156));
            processColors.put(pid, color);
        }
        return processColors.get(pid);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (blocks == null || blocks.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (GanttBlock block : blocks) {
            int x = MARGIN + block.start * BLOCK_WIDTH;
            int width = (block.end - block.start) * BLOCK_WIDTH;

            Color color = getColorForProcess(block.pid);
            g2.setColor(color);
            g2.fillRect(x, MARGIN, width, BLOCK_HEIGHT);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, MARGIN, width, BLOCK_HEIGHT);

            // Center PID text
            String text = "P" + block.pid;
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            g2.drawString(text, x + (width - textWidth) / 2, MARGIN + (BLOCK_HEIGHT + textHeight) / 2);
        }

        // Time markers
        g2.setColor(Color.BLACK);
        for (GanttBlock block : blocks) {
            int xStart = MARGIN + block.start * BLOCK_WIDTH;
            int xEnd = MARGIN + block.end * BLOCK_WIDTH;
            g2.drawString("" + block.start, xStart, MARGIN + BLOCK_HEIGHT + 15);
            g2.drawString("" + block.end, xEnd, MARGIN + BLOCK_HEIGHT + 15);
        }
    }
}

