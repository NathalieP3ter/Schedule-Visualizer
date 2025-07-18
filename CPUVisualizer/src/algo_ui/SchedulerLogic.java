package CPUVisualizer.src.algo_ui;

import java.util.*;

public class SchedulerLogic {
    public static class Process {
        public int pid, arrival, burst, remaining, completion, start;
        public int waiting, turnaround;

        public Process(int pid, int arrival, int burst) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
            this.start = -1;
        }
    }

    public static class GanttBlock {
        int pid, start, end;
        public GanttBlock(int pid, int start, int end) {
            this.pid = pid;
            this.start = start;
            this.end = end;
        }
    }

    private List<Process> processes = new ArrayList<>();
    private List<GanttBlock> ganttBlocks = new ArrayList<>();
    private Queue<Process> readyQueue = new LinkedList<>();
    private String cpuStatus = "Idle";
    private double avgWaiting = 0, avgTurnaround = 0;
    private int totalExecTime = 0;

    public void generateRandomProcesses(int count, String ext) {
        Random rand = new Random();
        processes.clear();
        for (int i = 0; i < count; i++) {
            int arrival = rand.nextInt(10);
            int burst = 1 + rand.nextInt(10);
            processes.add(new Process(i + 1, arrival, burst));
        }
        processes.sort(Comparator.comparingInt(p -> p.arrival));
    }

    public void run(String algorithm, int quantum) {
        ganttBlocks.clear();
        readyQueue.clear();
        cpuStatus = "Idle";
        avgWaiting = 0;
        avgTurnaround = 0;
        totalExecTime = 0;

        if (algorithm.equals("FIFO")) runFIFO();
        // else if (algorithm.equals("SJF")) runSJF(); // Extend as needed
        // else if (algorithm.equals("SRTF")) runSRTF();
        // else if (algorithm.equals("Round Robin")) runRR(quantum);
        // else if (algorithm.equals("MLFQ")) runMLFQ(quantum);

        int totalWaiting = 0, totalTurnaround = 0;
        for (Process p : processes) {
            totalWaiting += p.waiting;
            totalTurnaround += p.turnaround;
        }
        avgWaiting = totalWaiting / (double) processes.size();
        avgTurnaround = totalTurnaround / (double) processes.size();
    }

    private void runFIFO() {
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        int time = 0;
        for (Process p : processes) {
            if (time < p.arrival) time = p.arrival;
            p.start = time;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.start - p.arrival;
            ganttBlocks.add(new GanttBlock(p.pid, p.start, p.completion));
            readyQueue.offer(p);
        }
        cpuStatus = "Running P" + (readyQueue.peek() != null ? readyQueue.peek().pid : "-");
        totalExecTime = time;
    }

    public String getCurrentCPUStatus() {
        return cpuStatus;
    }

    public String getReadyQueueString() {
        StringBuilder sb = new StringBuilder();
        for (Process p : readyQueue) {
            sb.append("P").append(p.pid).append(" ");
        }
        return sb.toString();
    }

    public double getAverageWaitingTime() {
        return avgWaiting;
    }

    public double getAverageTurnaroundTime() {
        return avgTurnaround;
    }

    public int getTotalExecutionTime() {
        return totalExecTime;
    }

    public List<GanttBlock> getGanttBlocks() {
        return ganttBlocks;
    }

    public List<Process> getProcesses() {
        return processes;
    }
}
//te be continued