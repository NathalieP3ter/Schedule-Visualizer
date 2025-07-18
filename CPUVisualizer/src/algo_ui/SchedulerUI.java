package CPUVisualizer.src.algo_ui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerUI extends JFrame {
    private GanttChartPanel chart = new GanttChartPanel();
    private JComboBox<String> algorithmSelector;
    private JComboBox<String> extensionSelector;
    private JTextField quantumField, processCountField;
    private JButton simulateBtn, generateRandomBtn;
    private List<SchedulerLogic.Process> currentProcesses = new ArrayList<>();


    public SchedulerUI() {
     Panel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] algorithms = {"FCFS", "SJF", "RR", "SRTF"};
        algorithmSelector = new JComboBox<>(algorithms);

        String[] extensions = {".txt", ".csv", ".log", ".xml"};
        extensionSelector = new JComboBox<>(extensions);

        quantumField = new JTextField("2", 5);
        processCountField = new JTextField("3", 5);

        simulateBtn = new JButton("Simulate");
        simulateBtn.addActionListener(e -> simulate());

        generateRandomBtn = new JButton("Generate Random");
        generateRandomBtn.addActionListener(e -> generateRandomProcesses());

        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmSelector);
        controlPanel.add(new JLabel("Quantum (RR):"));
        controlPanel.add(quantumField);
        controlPanel.add(new JLabel("Process Count:"));
        controlPanel.add(processCountField);
        controlPanel.add(new JLabel("Extension:"));
        controlPanel.add(extensionSelector);
        controlPanel.add(generateRandomBtn);
        controlPanel.add(simulateBtn);

        add(controlPanel, BorderLayout.NORTH);
        add(chart, BorderLayout.CENTER);
        setVisible(true);

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

        for (int i = 0; i < count; i++) {
            int arrival = rand.nextInt(5);     // 0–4
            int burst = 1 + rand.nextInt(9);   // 1–9
            currentProcesses.add(new SchedulerLogic.Process(i, arrival, burst));
        }

        String extension = (String) extensionSelector.getSelectedItem();
        JOptionPane.showMessageDialog(this,
            "Generated " + count + " random processes\nExtension selected: " + extension,
            "Random Generation", JOptionPane.INFORMATION_MESSAGE);
    }





//wala pa ni naupdate nako 

    private void simulate(){
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
                int quantum = 2;
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

        chart.setBlocks(rawBlocks);
    }
   

}