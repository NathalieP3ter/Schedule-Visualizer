package CPUVisualizer.src.algo_ui;

import java.util.*;

public class SchedulerLogic {

    public static class Process{
        public static id, arrival, burst, remaining;

        public Process(int id, int arrival, int burst) {
           this.id = id;
           this.arrival = arrival;
           this.burst = burst;
           this.remaining = burst; // for preemptive algorithms
        }
    }

    //FIFO logic

    public static List<GanttBlock> runFIFO (List<Process> processes){
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        List<GanttBlock> blocks = new ArrayList<>();
        int time = 0;

         for (Process p : processes) {
            if (time < p.arrival) time = p.arrival;
            result.add(new GanttBlock(p.id, time, time + p.burst));
            time += p.burst;
        }

        return result;
    }

    //SJF logic (non-preemptive)

    public static List<GanttBlock> runSJF(List<Process> processes) {
        List<GanttBlock> result = new ArrayList<>();
        List<Process> queue = new ArrayList<>();
        int time = 0;

        while (!processes.isEmpty() || !queue.isEmpty()) {
            for (Iterator<Process> it = processes.iterator(); it.hasNext();) {
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

            queue.sort(Comparator.comparingInt(p -> p.burst));
            Process p = queue.remove(0);
            result.add(new GanttBlock(p.id, time, time + p.burst));
            time += p.burst;
        }

        return result;
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

