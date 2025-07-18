package CPUVisualizer.src.algo_ui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class SchedulerLogic {
    public static class Process {
        public int pid, arrival, burst, remaining, completion, start;
        public int waiting, turnaround, response;

        public Process(int pid, int arrival, int burst) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
            this.start = -1;
        }
    }

    public static class GanttBlock {
        public String pid;
        public int start, end;

        public GanttBlock(String pid, int start, int end) {
            this.pid = pid;
            this.start = start;
            this.end = end;
        }
    }

    private List<Process> processes;
    private List<GanttBlock> ganttBlocks;

    public void loadProcessesFromTable(JTable table) {
        processes = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int pid = Integer.parseInt(model.getValueAt(i, 0).toString());
            int arrival = Integer.parseInt(model.getValueAt(i, 1).toString());
            int burst = Integer.parseInt(model.getValueAt(i, 2).toString());
            processes.add(new Process(pid, arrival, burst));
        }
    }

    public List<GanttBlock> runAlgorithm(String algorithm, int quantum) {
        ganttBlocks = new ArrayList<>();
        switch (algorithm) {
            case "FIFO": runFIFO(); break;
            case "SJF": runSJF(); break;
            case "SRTF": runSRTF(); break;
            case "RR": runRR(quantum); break;
            default: runFIFO(); break;
        }
        return ganttBlocks;
    }

    private void runFIFO() {
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrival) currentTime = p.arrival;
            p.start = currentTime;
            currentTime += p.burst;
            p.completion = currentTime;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            ganttBlocks.add(new GanttBlock("P" + p.pid, p.start, p.completion));
        }
    }

    private void runSJF() {
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;

        while (completed.size() < processes.size()) {
            List<Process> available = new ArrayList<>();
            for (Process p : processes) {
                if (!completed.contains(p) && p.arrival <= currentTime)
                    available.add(p);
            }
            if (available.isEmpty()) {
                currentTime++;
                continue;
            }
            Process shortest = available.stream().min(Comparator.comparingInt(p -> p.burst)).get();
            shortest.start = currentTime;
            currentTime += shortest.burst;
            shortest.completion = currentTime;
            shortest.turnaround = shortest.completion - shortest.arrival;
            shortest.waiting = shortest.turnaround - shortest.burst;
            completed.add(shortest);
            ganttBlocks.add(new GanttBlock("P" + shortest.pid, shortest.start, shortest.completion));
        }
    }

    private void runSRTF() {
        int time = 0, completed = 0;
        Process current = null;
        List<GanttBlock> blocks = new ArrayList<>();
        while (completed < processes.size()) {
            Process shortest = null;
            for (Process p : processes) {
                if (p.arrival <= time && p.remaining > 0) {
                    if (shortest == null || p.remaining < shortest.remaining)
                        shortest = p;
                }
            }
            if (shortest == null) {
                time++;
                continue;
            }
            if (current != shortest) {
                if (current != null && current.remaining > 0)
                    blocks.get(blocks.size() - 1).end = time;
                blocks.add(new GanttBlock("P" + shortest.pid, time, time + 1));
                current = shortest;
            } else {
                blocks.get(blocks.size() - 1).end++;
            }

            if (shortest.start == -1) shortest.start = time;
            shortest.remaining--;
            time++;
            if (shortest.remaining == 0) {
                shortest.completion = time;
                shortest.turnaround = shortest.completion - shortest.arrival;
                shortest.waiting = shortest.turnaround - shortest.burst;
                completed++;
            }
        }
        ganttBlocks.addAll(blocks);
    }

    private void runRR(int quantum) {
        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int index = 0;
        while (index < processes.size() && processes.get(index).arrival <= time)
            queue.add(processes.get(index++));

        while (!queue.isEmpty()) {
            Process p = queue.poll();
            if (p.start == -1) p.start = time;
            int execTime = Math.min(p.remaining, quantum);
            ganttBlocks.add(new GanttBlock("P" + p.pid, time, time + execTime));
            time += execTime;
            p.remaining -= execTime;

            while (index < processes.size() && processes.get(index).arrival <= time)
                queue.add(processes.get(index++));

            if (p.remaining > 0) queue.add(p);
            else {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
            }
        }
    }

    public void fillMetricsTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Process p : processes) {
            model.addRow(new Object[]{
                p.pid, p.arrival, p.burst, p.start,
                p.completion, p.turnaround, p.waiting
            });
        }
    }

    public double getAverageWaitingTime() {
        return processes.stream().mapToInt(p -> p.waiting).average().orElse(0);
    }

    public double getAverageTurnaroundTime() {
        return processes.stream().mapToInt(p -> p.turnaround).average().orElse(0);
    }

    public int getTotalExecutionTime() {
        return processes.stream().mapToInt(p -> p.completion).max().orElse(0);
    }
}
