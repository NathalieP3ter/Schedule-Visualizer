package CPUVisualizer.src.ui;

import java.util.*;

public class SchedulerLogic {

    pubic stattic class Process{
        public int pid,arrival,burst,remaining,completion,start;
        public int waiting,turnaround,response;
        public Process(int pid, int arrival,int burst) {
            this.pid =pid;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
            this.start = -1;
        }
    }

    //FIFO logic

    public static List<int[]> runFIFO (List<Process> processes){
        List<int[>blocks = new ArrayList<>();
        int time = 0;
        for (Process p : processes){
            if (time < p.arrival) time = p.arrival;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.start - p.arrival;
            blocks.add (new int[]{p.pid, p.start,p.completion});
        }
        return blocks;
    }

    public static List<int[]> runSJF(List<Process> processes){
        List<int[]> blocks = new ArrayList<>();
        int time = 0;
        while (!processes.isEmpty()) {
            Process next = null;
            for (Process p : processes) {
                if (p.arrival <= time && (next == null || p.burst < next.burst)) {
                    next = p;
                }
            }
            if (next == null) {
                time++;
                continue;
            }
            if (time < next.arrival) time = next.arrival;
            next.start = time;
            time += next.burst;
            next.completion = time;
            next.turnaround = next.completion - next.arrival;
            next.waiting = next.turnaround - next.burst;
            blocks.add(new int[]{next.pid, next.start, next.completion});
            processes.remove(next);
        }
        return blocks;
    }

}
