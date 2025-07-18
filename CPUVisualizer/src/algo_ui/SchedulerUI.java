package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class SchedulerUI extends JFrame {
    private JComboBox<String> algorithmSelector;
    private JTextField quantumField;
    private JTextField jobCountField;
    private JTable inputTable;
    private JTable metricsTable;
    private GanttChartPanel ganttChartPanel;
    private SchedulerLogic scheduler;
    private JLabel cpuStatus;
    private JLabel readyQueueLabel;
    private JLabel avgWaitLabel, avgTurnaroundLabel, totalExecLabel;

    public SchedulerUI() {
        setTitle("Kaur Peter CPU Scheduling Visualizer");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scheduler = new SchedulerLogic();
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout());

        algorithmSelector = new JComboBox<>(new String[]{"FIFO", "SJF", "SRTF", "RR"});
        quantumField = new JTextField(5);
        jobCountField = new JTextField(5);
        JButton fileButton = new JButton("Select File");
        JButton runButton = new JButton("Run");
        JButton resetButton = new JButton("Reset");

        topPanel.add(new JLabel("Algorithm:"));
        topPanel.add(algorithmSelector);
        topPanel.add(new JLabel("Quantum:"));
        topPanel.add(quantumField);
        topPanel.add(new JLabel("Jobs:"));
        topPanel.add(jobCountField);
        topPanel.add(fileButton);
        topPanel.add(runButton);
        topPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));

        // CPU Box and Ready Queue
        JPanel statusPanel = new JPanel(new GridLayout(1, 2));
        cpuStatus = new JLabel("CPU: Idle", SwingConstants.CENTER);
        cpuStatus.setFont(new Font("Arial", Font.BOLD, 16));
        cpuStatus.setBorder(BorderFactory.createTitledBorder("CPU Status"));
        readyQueueLabel = new JLabel("Ready Queue: []", SwingConstants.CENTER);
        readyQueueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        readyQueueLabel.setBorder(BorderFactory.createTitledBorder("Ready Queue"));
        statusPanel.add(cpuStatus);
        statusPanel.add(readyQueueLabel);
        centerPanel.add(statusPanel);

        // Gantt Chart Panel
        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(1000, 100));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        centerPanel.add(ganttChartPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel (Tables)
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Input Table
        inputTable = new JTable(new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0));
        JScrollPane inputScrollPane = new JScrollPane(inputTable);
        inputScrollPane.setBorder(BorderFactory.createTitledBorder("Job Input"));
        inputScrollPane.setPreferredSize(new Dimension(500, 120));

        // Metrics Table
        metricsTable = new JTable(new DefaultTableModel(
                new Object[]{"PID", "Arrival", "Burst", "Start", "Completion", "Turnaround", "Waiting"}, 0
        ));
        JScrollPane metricsScrollPane = new JScrollPane(metricsTable);
        metricsScrollPane.setBorder(BorderFactory.createTitledBorder("Metrics"));

        // Metrics Averages
        JPanel statsPanel = new JPanel(new GridLayout(3, 1));
        avgWaitLabel = new JLabel("Average Waiting Time: ");
        avgTurnaroundLabel = new JLabel("Average Turnaround Time: ");
        totalExecLabel = new JLabel("Total Execution Time: ");
        statsPanel.add(avgWaitLabel);
        statsPanel.add(avgTurnaroundLabel);
        statsPanel.add(totalExecLabel);

        JPanel metricsContainer = new JPanel(new BorderLayout());
        metricsContainer.add(metricsScrollPane, BorderLayout.CENTER);
        metricsContainer.add(statsPanel, BorderLayout.SOUTH);

        bottomPanel.add(inputScrollPane, BorderLayout.WEST);
        bottomPanel.add(metricsContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        runButton.addActionListener(e -> runScheduler());
        resetButton.addActionListener(e -> resetUI());
        fileButton.addActionListener(e -> loadFromFile());
    }

    private void runScheduler() {
        scheduler.loadProcessesFromTable(inputTable);
        String algo = (String) algorithmSelector.getSelectedItem();
        int quantum = 0;
        try {
            quantum = Integer.parseInt(quantumField.getText().trim());
        } catch (NumberFormatException ignored) {}
        List<SchedulerLogic.GanttBlock> blocks = scheduler.runAlgorithm(algo, quantum);
        ganttChartPanel.setGanttBlocks(blocks);
        scheduler.fillMetricsTable(metricsTable);

        cpuStatus.setText("CPU: Running " + algo);
        readyQueueLabel.setText("Ready Queue: Simulated");

        avgWaitLabel.setText("Average Waiting Time: " + String.format("%.2f", scheduler.getAverageWaitingTime()));
        avgTurnaroundLabel.setText("Average Turnaround Time: " + String.format("%.2f", scheduler.getAverageTurnaroundTime()));
        totalExecLabel.setText("Total Execution Time: " + scheduler.getTotalExecutionTime());
    }

    private void resetUI() {
        ((DefaultTableModel) inputTable.getModel()).setRowCount(0);
        ((DefaultTableModel) metricsTable.getModel()).setRowCount(0);
        ganttChartPanel.setGanttBlocks(null);
        cpuStatus.setText("CPU: Idle");
        readyQueueLabel.setText("Ready Queue: []");
        avgWaitLabel.setText("Average Waiting Time: ");
        avgTurnaroundLabel.setText("Average Turnaround Time: ");
        totalExecLabel.setText("Total Execution Time: ");
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
                model.setRowCount(0);
                int pid = 1;
                for (String line : lines) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 2) {
                        int arrival = Integer.parseInt(parts[0]);
                        int burst = Integer.parseInt(parts[1]);
                        model.addRow(new Object[]{pid++, arrival, burst});
                    }
                }
                jobCountField.setText(String.valueOf(pid - 1));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading file.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SchedulerUI().setVisible(true));
    }
}
