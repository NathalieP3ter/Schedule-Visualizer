package CPUVisualizer.src;

import CPUVisualizer.src.algo_ui.SchedulerUI;

public class Main {
    public static void main(String[] args) {
     javax.swing.SwingUtilities.invokeLater(SchedulerUI::new);
    }
}