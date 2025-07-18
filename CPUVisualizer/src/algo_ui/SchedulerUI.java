package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SchedulerUI extends JFrame {
    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> fileExtensionComboBox;
    private JTextField jobCountField, quantumField;
    private JLabel cpuStatusLabel, avgWaitingLabel, avgTurnaroundLabel, totalExecLabel;
    private JTextArea readyQueueArea;
    private JTable metricsTable;
    private GanttChartPanel ganttChartPanel;

    private SchedulerLogic schedulerLogic;

    private static final String[] ALGORITHMS = {"FIFO", "SJF", "SRTF", "Round Robin", "MLFQ"};
    private static final String[] FILE_EXTENSIONS = {".txt", ".csv", ".xml", ".log", ".json", ".dat", ".html"};

    public SchedulerUI() {
        setTitle("Kaur Peter CPU Scheduling Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        schedulerLogic = new SchedulerLogic();

        // Top control panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 1));
        JPanel topRow = new JPanel();
        JPanel midRow = new JPanel();
        JPanel bottomRow = new JPanel();

        algorithmComboBox = new JComboBox<>(ALGORITHMS);
        fileExtensionComboBox = new JComboBox<>(FILE_EXTENSIONS);
        jobCountField = new JTextField(5);
        quantumField = new JTextField(5);
        quantumField.setVisible(false);

        algorithmComboBox.addActionListener(e -> {
            String selected = (String) algorithmComboBox.getSelectedItem();
            quantumField.setVisible(selected.equals("Round Robin") || selected.equals("MLFQ"));
            pack();
        });

        JButton generateBtn = new JButton("Generate Random");
        JButton runBtn = new JButton("Run");
        JButton resetBtn = new JButton("Reset");

        generateBtn.addActionListener(e -> generateRandom());
        runBtn.addActionListener(e -> runScheduler());
        resetBtn.addActionListener(e -> reset());

        topRow.add(new JLabel("Algorithm:"));
        topRow.add(algorithmComboBox);
        topRow.add(new JLabel("Quantum:"));
        topRow.add(quantumField);
        topRow.add(new JLabel("File Type:"));
        topRow.add(fileExtensionComboBox);
        topRow.add(new JLabel("Jobs:"));
        topRow.add(jobCountField);
        topRow.add(generateBtn);
        topRow.add(runBtn);
        topRow.add(resetBtn);

        cpuStatusLabel = new JLabel("CPU Status: Idle");
        readyQueueArea = new JTextArea(2, 20);
        readyQueueArea.setEditable(false);
        readyQueueArea.setBorder(BorderFactory.createTitledBorder("Ready Queue"));

        midRow.add(cpuStatusLabel);
        midRow.add(new JScrollPane(readyQueueArea));

        avgWaitingLabel = new JLabel("Avg Waiting Time: ");
        avgTurnaroundLabel = new JLabel("Avg Turnaround Time: ");
        totalExecLabel = new JLabel("Total Execution Time: ");

        bottomRow.add(avgWaitingLabel);
        bottomRow.add(avgTurnaroundLabel);
        bottomRow.add(totalExecLabel);

        controlPanel.add(topRow);
        controlPanel.add(midRow);
        controlPanel.add(bottomRow);
        add(controlPanel, BorderLayout.NORTH);

        // Gantt chart
        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(800, 150));
        add(ganttChartPanel, BorderLayout.CENTER);

        // Metrics table
        metricsTable = new JTable(new DefaultTableModel(
                new Object[]{"PID", "Arrival", "Burst", "Start", "Completion", "Turnaround", "Waiting"}, 0
        ));
        JScrollPane tableScroll = new JScrollPane(metricsTable);
        add(tableScroll, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateRandom() {
        try {
            int num = Integer.parseInt(jobCountField.getText());
            Random rand = new Random();
            String randomExt = FILE_EXTENSIONS[rand.nextInt(FILE_EXTENSIONS.length)];
            schedulerLogic.generateRandomProcesses(num, randomExt);
            JOptionPane.showMessageDialog(this, "Generated " + num + " jobs (" + randomExt + ")");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of jobs.");
        }
    }

    private void runScheduler() {
        String algo = (String) algorithmComboBox.getSelectedItem();
        int quantum = 0;
        if (algo.equals("Round Robin") || algo.equals("MLFQ")) {
            try {
                quantum = Integer.parseInt(quantumField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantum must be a valid number.");
                return;
            }
        }

        schedulerLogic.run(algo, quantum);

        // Animate Gantt chart
        ganttChartPanel.setGanttData(schedulerLogic.getGanttBlocks());
        ganttChartPanel.animateBlocks(400);

        // CPU status and ready queue
        cpuStatusLabel.setText("CPU Status: " + schedulerLogic.getCurrentCPUStatus());
        readyQueueArea.setText(schedulerLogic.getReadyQueueString());

        // Metrics table
        DefaultTableModel model = (DefaultTableModel) metricsTable.getModel();
        model.setRowCount(0);
        for (SchedulerLogic.Process p : schedulerLogic.getProcesses()) {
            model.addRow(new Object[]{p.pid, p.arrival, p.burst, p.start, p.completion, p.turnaround, p.waiting});
        }

        avgWaitingLabel.setText("Avg Waiting Time: " + schedulerLogic.getAverageWaitingTime());
        avgTurnaroundLabel.setText("Avg Turnaround Time: " + schedulerLogic.getAverageTurnaroundTime());
        totalExecLabel.setText("Total Execution Time: " + schedulerLogic.getTotalExecutionTime());
    }

    private void reset() {
        jobCountField.setText("");
        quantumField.setText("");
        ((DefaultTableModel) metricsTable.getModel()).setRowCount(0);
        ganttChartPanel.setGanttData(null);
        ganttChartPanel.repaint();
        cpuStatusLabel.setText("CPU Status: Idle");
        readyQueueArea.setText("");
        avgWaitingLabel.setText("Avg Waiting Time: ");
        avgTurnaroundLabel.setText("Avg Turnaround Time: ");
        totalExecLabel.setText("Total Execution Time: ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerUI::new);
    }
}
