package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SchedulerUI extends JFrame {
   private JComboBox<String> algorithmSelector, extensionSelector;
   private JTextField quantumField, processCountField;
   private JSlider speedSlider;
   private JCheckBox stepMode;
   private JTable inputTable, outputTable;
   private GanttChartPanel chartPanel = new GanttChartPanel();
   private JScrollPane chartScroll;
   private List<SchedulerLogic.Process> currentProcesses = new ArrayList<>();

   public SchedulerUI() {
       setTitle("CPU Scheduling Visualizer");
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setSize(1100, 650); // ✅ Resized window for better display
       setLocationRelativeTo(null);

       JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
               buildControlPanel(), buildOutputPanel());
       splitPane.setDividerLocation(300); // Adjusted for new window size
       add(splitPane);

       setVisible(true);
   }

   private JPanel buildControlPanel() {
       JPanel panel = new JPanel();
       panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
       panel.setPreferredSize(new Dimension(300, 650));
       panel.setBorder(BorderFactory.createTitledBorder("Controls"));

       algorithmSelector = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "RR", "MLFQ"});
       extensionSelector = new JComboBox<>(new String[]{
               ".txt", ".csv", ".log", ".xml", ".json", ".dat", ".html"
       });

       quantumField = new JTextField("2");
       processCountField = new JTextField("3");

       quantumField.setMaximumSize(new Dimension(100, 25));
       processCountField.setMaximumSize(new Dimension(100, 25));

       speedSlider = new JSlider(10, 1000, 300);
       stepMode = new JCheckBox("Enable Step-by-Step");

       JButton addRow = new JButton("➕ Add Process");
       addRow.addActionListener(e -> {
           DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
           model.addRow(new Object[]{"P" + model.getRowCount(), 0, 1});
       });

       JButton generateRandomBtn = new JButton("🎲 Generate Random");
       generateRandomBtn.addActionListener(e -> {
           int count = 3;
           try {
               count = Integer.parseInt(processCountField.getText());
           } catch (NumberFormatException ignored) {}

           currentProcesses.clear();
           DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
           model.setRowCount(0);
           Random rand = new Random();
           for (int i = 0; i < count; i++) {
               int arrival = rand.nextInt(5);
               int burst = 1 + rand.nextInt(9);
               currentProcesses.add(new SchedulerLogic.Process(i, arrival, burst));
               model.addRow(new Object[]{"P" + i, arrival, burst});
           }

           String selected = extensionSelector.getSelectedItem().toString();
           JOptionPane.showMessageDialog(this,
                   "Generated " + count + " processes\nExtension selected: " + selected,
                   "Random Generator", JOptionPane.INFORMATION_MESSAGE);
       });

       JButton simulateBtn = new JButton("▶️ Simulate");
       simulateBtn.addActionListener(e -> simulate());

       JButton resetBtn = new JButton("🔄 Reset"); // ✅ New Reset button
       resetBtn.addActionListener(e -> {
           ((DefaultTableModel) inputTable.getModel()).setRowCount(0);
           ((DefaultTableModel) outputTable.getModel()).setRowCount(0);
           chartPanel.setBlocksInstant(new ArrayList<>());
       });

       panel.add(new JLabel("Algorithm:"));         panel.add(algorithmSelector);
       panel.add(new JLabel("Time Quantum:"));       panel.add(quantumField);
       panel.add(new JLabel("Process Count:"));      panel.add(processCountField);
       panel.add(new JLabel("File Extension:"));     panel.add(extensionSelector);
       panel.add(new JLabel("Step Delay (ms):"));    panel.add(speedSlider);
       panel.add(stepMode);
       panel.add(Box.createVerticalStrut(10));
       panel.add(addRow);
       panel.add(generateRandomBtn);
       panel.add(simulateBtn);
       panel.add(resetBtn); // ✅ Add it here

       return panel;
   }

   private JPanel buildOutputPanel() {
       JPanel panel = new JPanel(new BorderLayout());

       inputTable = new JTable(new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0));
       outputTable = new JTable(new DefaultTableModel(new Object[]{
               "PID", "Arrival", "Burst", "Start", "Completion", "TAT", "Waiting"
       }, 0));

       JScrollPane inputScroll = new JScrollPane(inputTable);
       inputScroll.setBorder(BorderFactory.createTitledBorder("Input Table"));

       JScrollPane outputScroll = new JScrollPane(outputTable);
       outputScroll.setBorder(BorderFactory.createTitledBorder("Metrics"));

       chartScroll = new JScrollPane(chartPanel);
       chartScroll.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));

       JPanel tables = new JPanel(new GridLayout(1, 2));
       tables.add(inputScroll);
       tables.add(outputScroll);

       JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
               tables, chartScroll);
       verticalSplit.setDividerLocation(250);

       panel.add(verticalSplit, BorderLayout.CENTER);
       return panel;
   }

   private void simulate() {
       List<SchedulerLogic.Process> processes = new ArrayList<>();
       DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
       for (int i = 0; i < model.getRowCount(); i++) {
           try {
               String pidLabel = model.getValueAt(i, 0).toString();
               int pid = Integer.parseInt(pidLabel.replaceAll("[^0-9]", ""));
               int arrival = Integer.parseInt(model.getValueAt(i, 1).toString());
               int burst = Integer.parseInt(model.getValueAt(i, 2).toString());
               processes.add(new SchedulerLogic.Process(pid, arrival, burst));
           } catch (Exception ignored) {}
       }

       if (processes.isEmpty()) {
           JOptionPane.showMessageDialog(this, "No processes available for simulation.");
           return;
       }

       int quantum;
       try {
           quantum = Integer.parseInt(quantumField.getText());
       } catch (NumberFormatException e) {
           quantum = 2;
       }

       List<SchedulerLogic.Process> clones = new ArrayList<>();
       for (SchedulerLogic.Process p : processes) {
           clones.add(new SchedulerLogic.Process(p.id, p.arrival, p.burst));
       }

       List<GanttBlock> blocks = new ArrayList<>();
       String selectedAlgo = (String) algorithmSelector.getSelectedItem();

       switch (selectedAlgo) {
           case "FCFS":  blocks = SchedulerLogic.runFIFO(clones); chartPanel.setStyle(GanttChartPanel.Style.FCFS); break;
           case "SJF":   blocks = SchedulerLogic.runSJF(clones);  chartPanel.setStyle(GanttChartPanel.Style.SJF); break;
           case "SRTF":  blocks = SchedulerLogic.runSRTF(clones); chartPanel.setStyle(GanttChartPanel.Style.SRTF); break;
           case "RR":    blocks = SchedulerLogic.runRoundRobin(clones, quantum); chartPanel.setStyle(GanttChartPanel.Style.RR); break;
           case "MLFQ":  blocks = SchedulerLogic.runMLFQ(clones, new int[]{quantum, quantum + 1, quantum + 2, quantum + 3}); chartPanel.setStyle(GanttChartPanel.Style.MLFQ); break;
       }

       chartPanel.setAnimationDelay(speedSlider.getValue());
       if (stepMode.isSelected()) {
           chartPanel.animateBlocks(blocks);
       } else {
           chartPanel.setBlocksInstant(blocks);
       }

       updateOutputTable(clones);
   }

   private void updateOutputTable(List<SchedulerLogic.Process> processes) {
       DefaultTableModel model = new DefaultTableModel(new Object[]{
               "PID", "Arrival", "Burst", "Start", "Completion", "TAT", "Waiting"
       }, 0);
       for (SchedulerLogic.Process p : processes) {
           model.addRow(new Object[]{
                   "P" + p.id,
                   p.arrival,
                   p.burst,
                   p.start,
                   p.completion,
                   p.turnaround,
                   p.waiting
           });
       }
       outputTable.setModel(model);
   }

   public static void main(String[] args) {
       SwingUtilities.invokeLater(SchedulerUI::new);
   }
}
