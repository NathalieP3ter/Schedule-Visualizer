package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerUI extends JFrame {
    private JComboBox<String> algorithmSelector, extensionSelector;
    private JTextField quantumField, processCountField;
    private JSlider speedSlider;
    private JCheckBox stepMode;
    private JButton simulateBtn, generateRandomBtn;
    private JTable inputTable, outputTable;
    private GanttChartPanel chartPanel = new GanttChartPanel();
    private List<SchedulerLogic.Process> currentProcesses = new ArrayList<>();

    public SchedulerUI() {
        setTitle("CPU Scheduling Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildControlPanel(), BorderLayout.WEST);
        add(buildDisplayPanel(), BorderLayout.CENTER);
        add(buildTablesPanel(), BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1200, 700));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, getHeight()));
        panel.setBorder(BorderFactory.createTitledBorder("Controls"));

        algorithmSelector = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "RR", "MLFQ"});
        extensionSelector = new JComboBox<>(new String[]{".txt", ".csv", ".log", ".xml", ".json", ".dat", ".html"});
        quantumField = new JTextField("2", 5);
        processCountField = new JTextField("3", 5);
        speedSlider = new JSlider(10, 1000, 300);
        stepMode = new JCheckBox("Enable Step-by-Step");

        JButton addRow = new JButton("Add Process");
        addRow.addActionListener(e -> {
            DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
            model.addRow(new Object[]{"P" + model.getRowCount(), 0, 1});
        });

        generateRandomBtn = new JButton("Generate Random");
        generateRandomBtn.addActionListener(e -> generateRandomProcesses());

        simulateBtn = new JButton("Simulate");
        simulateBtn.addActionListener(e -> simulate());

        panel.add(new JLabel("Algorithm:"));      panel.add(algorithmSelector);
        panel.add(new JLabel("Time Quantum:"));    panel.add(quantumField);
        panel.add(new JLabel("Process Count:"));   panel.add(processCountField);
        panel.add(new JLabel("Extension:"));       panel.add(extensionSelector);
        panel.add(new JLabel("Step Delay (ms):")); panel.add(speedSlider);
        panel.add(stepMode);
        panel.add(addRow);
        panel.add(generateRandomBtn);
        panel.add(simulateBtn);

        return panel;
    }

    private JPanel buildDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        chartPanel.setStyle(GanttChartPanel.Style.FCFS);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTablesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        inputTable = new JTable(new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0));
        JScrollPane inputScroll = new JScrollPane(inputTable);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input Table"));

        outputTable = new JTable();
        JScrollPane outputScroll = new JScrollPane(outputTable);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Metrics"));

        panel.add(inputScroll);
        panel.add(outputScroll);

        return panel;
    }

    private void generateRandomProcesses() {
        int count;
        try {
            count = Integer.parseInt(processCountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid process count. Defaulting to 3.");
            count = 3;
        }

        Random rand = new Random();
        currentProcesses.clear();
        DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
        model.setRowCount(0);

        for (int i = 0; i < count; i++) {
            int arrival = rand.nextInt(5);
            int burst = 1 + rand.nextInt(9);
            currentProcesses.add(new SchedulerLogic.Process(i, arrival, burst));
            model.addRow(new Object[]{"P" + i, arrival, burst});
        }

        String[] extensions = {".txt", ".csv", ".log", ".xml", ".json", ".dat", ".html"};
        String randomExtension = extensions[rand.nextInt(extensions.length)];
        extensionSelector.setSelectedItem(randomExtension);

        JOptionPane.showMessageDialog(this,
            "Generated " + count + " random processes\nRandom file extension: " + randomExtension,
            "Random Generation", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "No processes available.");
            return;
        }

        String selectedAlgo = (String) algorithmSelector.getSelectedItem();
        int quantum;
        try {
            quantum = Integer.parseInt(quantumField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantum. Using default: 2");
            quantum = 2;
        }

        List<SchedulerLogic.Process> cloneList = new ArrayList<>();
        for (SchedulerLogic.Process p : processes)
            cloneList.add(new SchedulerLogic.Process(p.id, p.arrival, p.burst));

        List<GanttBlock> rawBlocks = new ArrayList<>();
        switch (selectedAlgo) {
            case "FCFS":  rawBlocks = SchedulerLogic.runFIFO(cloneList);  chartPanel.setStyle(GanttChartPanel.Style.FCFS);  break;
            case "SJF":   rawBlocks = SchedulerLogic.runSJF(cloneList);   chartPanel.setStyle(GanttChartPanel.Style.SJF);   break;
            case "SRTF":  rawBlocks = SchedulerLogic.runSRTF(cloneList);  chartPanel.setStyle(GanttChartPanel.Style.SRTF);  break;
            case "RR":    rawBlocks = SchedulerLogic.runRoundRobin(cloneList, quantum); chartPanel.setStyle(GanttChartPanel.Style.RR); break;
            case "MLFQ":  rawBlocks = SchedulerLogic.runMLFQ(cloneList, new int[]{quantum, quantum + 1, quantum + 2, quantum + 3}); chartPanel.setStyle(GanttChartPanel.Style.MLFQ); break;
        }

        chartPanel.setAnimationDelay(speedSlider.getValue());
        if (stepMode.isSelected()) {
            chartPanel.animateBlocks(rawBlocks);
        } else {
            chartPanel.setBlocksInstant(rawBlocks);
        }

        updateOutputTable(cloneList);
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
}
