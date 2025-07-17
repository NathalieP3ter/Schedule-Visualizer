package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class SchedulerUI extends JFrame {
    private GanttChartPanel chart = new GanttChartPanel();
    private JComboBox<String> algorithmSelector;
    private JTextField quantumField, jobCountField;
    private JLabel cpuLabel, algoLabel; 
    private JTextArea readyQueueArea; // ang kani kay ang order sa  processes nga iprioritize
    private JLabel avgWTLabel, averageTALabel, totalExecuteLabel; // kani kay waiting, turnaround ug ang total executtion time
    private JTable processTable; 

    public SchedulerUI() {
        setTitle("CPU Scheduling Visualizer");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLUE);

        initControls();
        initDashboard();
        setVisible(true);
    }

private void initControls() {
    JPanel cpntrolPanel = new JPanel(new FlowLayout());
    controlPanel.setBackground(Color.LIGHT_GRAY);
    String[] algorithms = {"FIFO", "SJF", "RR", "SRTF, MLFQ"};
    algorithmSelector = new JComboBox<>(algorithms);
    quantumField = new JTextField("2", 5);
    jobCountField = new JTextField("3", 5);
    JButton fileBtn = new JButton ("Pick file");
    JButton simulateButton = new JButton("Simulate");

    simulateButton.addActionListener(event -> simulate());

    controlPanel.add(new JLabel("Algorithm:"));
    controlPanel.add(algorithmSelector);
    controlPanel.add(new JLabel("Quantum (for RR):"));
    controlPanel.add(quantumField);
    controlPanel.add(new JLabel("Job Count:"));
    controlPanel.add(jobCountField);
    controlPanel.add(fileBtn);
    controlPanel.add(simulateButton);

    add(controlPanel, BorderLayout.NORTH);
  
}

        /*  Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] algorithms = {"FCFS", "SJF", "RR", "SRTF"};
        algorithmSelector = new JComboBox<>(algorithms);
        quantumField = new JTextField("2", 5);

        JButton simulateBtn = new JButton("Simulate");
        simulateBtn.addActionListener(event -> simulate());

        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmSelector);
        controlPanel.add(new JLabel("Quantum (for RR):"));
        controlPanel.add(quantumField);
        controlPanel.add(simulateBtn);

        add(controlPanel, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);

        setVisible(true);
    }

    private void simulate() {
        int[] arrival = {0, 2, 4};
        int[] burst = {5, 3, 1};
        String selectedAlgo = (String) algorithmSelector.getSelectedItem();

        List<SchedulerLogic.Process> processes = new ArrayList<>();
        for (int i = 0; i < arrival.length; i++) {
            processes.add(new SchedulerLogic.Process(i, arrival[i], burst[i]));
        }

        List<GanttBlock> rawBlocks = new ArrayList<>();
        switch (selectedAlgo) {
            case "FCFS":
                rawBlocks = SchedulerLogic.runFIFO(new ArrayList<>(processes));
                break;
            case "SJF":
                rawBlocks = SchedulerLogic.runSJF(new ArrayList<>(processes));
                break;
            case "RR":
                int quantum = 2; // Default quantum/value
                try {
                    quantum = Integer.parseInt(quantumField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid quantum value. Using default: 2", "Error", JOptionPane.ERROR_MESSAGE);
                }
                rawBlocks = SchedulerLogic.runRoundRobin(new ArrayList<>(processes), quantum);
                break;
            case "SRTF":
                rawBlocks = SchedulerLogic.runSRTF(new ArrayList<>(processes));
                break;
        }

        chart.setBlocks(rawBlocks);*/
    }
//}