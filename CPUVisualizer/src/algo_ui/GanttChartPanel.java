package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class GanttChartPanel extends JPanel {
   public enum Style { FCFS, SJF, RR, SRTF, MLFQ }

   private Style style = Style.FCFS;
   private List<GanttBlock> blocks;
   private int animatedBlockCount = 0;
   private int blockScale = 40;
   private int animationDelay = 300;

   public void setStyle(Style style) { this.style = style; }

   public void setAnimationDelay(int ms) { this.animationDelay = ms; }

   public void animateBlocks(List<GanttBlock> blocks) {
       this.blocks = blocks;
       this.animatedBlockCount = 0;
       Timer timer = new Timer(animationDelay, e -> {
           animatedBlockCount++;
           repaint();
           if (animatedBlockCount >= blocks.size()) ((Timer) e.getSource()).stop();
       });
       timer.start();
   }

   public void setBlocksInstant(List<GanttBlock> blocks) {
       this.blocks = blocks;
       this.animatedBlockCount = blocks.size();
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
       if (blocks == null || blocks.isEmpty()) return;

       Graphics2D g2d = (Graphics2D) g;
       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       int x = 50, y = 60, height = 50;

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
