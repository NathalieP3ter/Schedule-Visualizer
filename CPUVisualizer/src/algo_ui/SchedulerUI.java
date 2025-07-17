package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerUI extends JFrame {
    private GanttChartPanel chart = new GanttChartPanel();
    private JComboBox<String> algorithmSelector;
    private JTextField quantumField;

    public SchedulerUI() {
        setTitle("CPU Scheduling Visualizer");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        String[] algorithms = {"FCFS", "SJF", "RR", "SRTF"};
        algorithmSelector = new JComboBox<>(algorithms);
        quantumField = new JTextField("2", 5);

        JButton simulateBtn = new JButton("Simulate");
        simulateBtn.addActionListener(e -> simulate());

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

        List<int[]> rawBlocks = new ArrayList<>();
        switch (selectedAlgo) {
            case "FIFO":
                rawBlocks = SchedulerLogic.runFIFO(new ArrayList<>(processes));
                break;
            case "SJF":
                rawBlocks = SchedulerLogic.runSJF(new ArrayList<>(processes));
                break;
            case "RR":
                int quantum = Integer.parseInt(quantumField.getText());
                rawBlocks = SchedulerLogic.runRoundRobin(new ArrayList<>(processes), quantum);
                break;
            case "SRTF":
                rawBlocks = SchedulerLogic.runSRTF(new ArrayList<>(processes));
                break;
        }

        // Convert int[] to GanttBlock
        List<GanttBlock> result = new ArrayList<>();
        for (int[] arr : rawBlocks) {
            result.add(new GanttBlock(arr[0], arr[1], arr[2]));
        }

        chart.setBlocks(result);
    }
}
<br>
test