package CPUVisualizer.src.algo_ui;

import java.util.*;
import CPUVisualizer.src.algo_ui.GanttBlock;

public class SchedulerLogic {

    public static class Process{
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

    public static List<GanttBlock> runFIFO (List<Process> processes){
        List<GanttBlock> blocks = new ArrayList<>();
        int time = 0;
        for (Process p : processes){
            if (time < p.arrival) time = p.arrival;
            p.start = time;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.start - p.arrival;
            blocks.add (new GanttBlock(p.pid, p.start, p.completion));
        }
        return blocks;
    }
    //SJF logic (non-preemptive)

    public static List<GanttBlock> runSJF(List<Process> processes) {
        List<GanttBlock> blocks = new ArrayList<>();
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
            time+= next.burst;
            next.completion = time;
            next.turnaround = next.completion - next.arrival;
            next.waiting = next.turnaround - next.burst;
            blocks.add(new GanttBlock(next.pid, next.start, next.completion));  
            processes.remove(next);
        }
        return blocks;
    }

    //SRTF logic (preemptive)

    public static List<GanttBlock> runSRTF(List<Process> processes) {
        List<GanttBlock> blocks = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        int time = 0, completed = 0, n = processes.size();
        Process current = null;
        int lastTime = 0;
        while(completed < n){
            for(Process p : processes){
                if (p.arrival == time) ready.add(p);
        }
        Process next = ready.stream()
        .filter(p -> p.remaining > 0)
        .min(Comparator.comparingInt(p -> p.remaining))
        .orElse(null);

        if(next != null){
            if (current != next){
                if (current != null && current.remaining > 0) {
                    blocks.add(new GanttBlock(current.pid, lastTime, time));
                }
                current = next;
                lastTime = time;
                if (current.start == -1) current.start = time;
            }

            next.remaining--;
            if (next.remaining == 0){
                next.completion = time + 1;
                next.turnaround =next.completion - next.arrival;
                next.waiting = next.turnaround - next.burst;
                completed++;
            }
            time ++;
        } else {
            time++;
        }
    }
        if (current != null && current.remaining > 0) {
            blocks.add(new GanttBlock(current.pid, lastTime, time));
        }
        return blocks;
    }
    //Round Robin logic
    public static List<GanttBlock> runRoundRobin(List<Process> processes, int quantum) {
        List<GanttBlock> blocks = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        List<Process> arrived = new ArrayList<>(processes);
        int time = 0, completed = 0, n = processes.size();
        
        while (completed < n) {
            for (Iterator<Process> it = arrived.iterator(); it.hasNext();) {
                Process p = it.next();
                if (p.arrival <= time) {
                    queue.add(p);
                    it.remove();
                }
            }
            if (queue.isEmpty()) {
                time++;
                continue;
            }
            Process p = queue.poll();
            int exec = Math.min(quantum, p.remaining);
            if (p.start == -1) p.start = time;

            blocks.add(new GanttBlock(p.pid, time, time + exec));

            time += exec;
            p.remaining -= exec;
            
            for (Iterator<Process> it = arrived.iterator(); it.hasNext();) {
                Process q = it.next();
                if (q.arrival <= time) {
                    queue.add(q);
                    it.remove();
                }
            }
            if (p.remaining > 0) {
                queue.add(p);
            } else {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
                completed++;
            }
        }
        return blocks;
    }

    //Multi-level Feedback Queue logic
    public static List<GanttBlock> runMLFQ(List<Process> processes, int[] quantums) {
        int numQueues = quantums.length;
        List<GanttBlock> blocks = new ArrayList<>();
        List<Queue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < numQueues; i++) queues.add(new LinkedList<>());
 
        int time = 0, completed = 0, n = processes.size();
        List<Process> arrived = new ArrayList<>(processes);
        while (completed < n) {
            for (Iterator<Process> it = arrived.iterator(); it.hasNext();) {
                Process p = it.next();
                if (p.arrival <= time) {
                    queues.get(0).add(p);
                    it.remove();
                }
            }
            Process p = null;
            int qIdx = -1;
            for (int i = 0; i < numQueues; i++) {
                if (!queues.get(i).isEmpty()) {
                    p = queues.get(i).poll();
                    qIdx = i;
                    break;
                }
            }
            if (p == null) {
                time++;
                continue;
            }

            int exec = Math.min(quantums[qIdx], p.remaining);
            if (p.start == -1) p.start = time;

            blocks.add(new GanttBlock(p.pid, time, time + exec));
            time += exec;
            p.remaining -= exec;
            for (Iterator<Process> it = arrived.iterator(); it.hasNext();) {
                Process q = it.next();
                if (q.arrival <= time) {
                    queues.get(0).add(q);
                    it.remove();
                }
            }
            if (p.remaining > 0) {
                if (qIdx < numQueues - 1) {
                    queues.get(qIdx + 1).add(p);
                } else {
                    queues.get(qIdx).add(p);
                }
            } else {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
                completed++;
            }
        }
        return blocks;
    }

    // Utility: Calculate averages
    public static Map<String, Double> calculateAverages(List<Process> processes) {
        double totalWT = 0, totalTAT = 0;
        for (Process p : processes) {
            totalWT += p.waiting;
            totalTAT += p.turnaround;
        }
        Map<String, Double> avg = new HashMap<>();
        avg.put("avgWaiting", totalWT / processes.size());
        avg.put("avgTurnaround", totalTAT / processes.size());
        return avg;
    }
}

