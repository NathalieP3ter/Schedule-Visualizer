package CPUVisualizer.src.algo_ui;

import java.util.*;

public class SchedulerLogic {

    public static class Process {
        public int id, arrival, burst, remaining;
        public int start = -1, completion = 0, waiting = 0, turnaround = 0, response = -1;

        public Process(int id, int arrival, int burst) {
            this.id = id;
            this.arrival = arrival;
            this.burst = burst;
            this.remaining = burst;
        }
    }

    public static List<GanttBlock> runFIFO(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrival));
        List<GanttBlock> result = new ArrayList<>();
        int time = 0;

        for (Process p : processes) {
            time = Math.max(time, p.arrival);
            p.start = time;
            p.response = time - p.arrival;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            result.add(new GanttBlock(p.id, p.start, p.completion));
        }

        return result;
    }

    public static List<GanttBlock> runSJF(List<Process> processes) {
        List<GanttBlock> result = new ArrayList<>();
        List<Process> queue = new ArrayList<>();
        int time = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrival));

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

            time = Math.max(time, p.arrival);
            p.start = time;
            p.response = time - p.arrival;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            result.add(new GanttBlock(p.id, p.start, p.completion));
        }

        return result;
    }

    public static List<GanttBlock> runSRTF(List<Process> processes) {
        List<GanttBlock> result = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        int time = 0;
        Process current = null;

        processes.sort(Comparator.comparingInt(p -> p.arrival));

        while (!processes.isEmpty() || !ready.isEmpty() || current != null) {
            while (!processes.isEmpty() && processes.get(0).arrival == time) {
                ready.add(processes.remove(0));
            }

            if (current != null) ready.add(current);

            if (ready.isEmpty()) {
                current = null;
                time++;
                continue;
            }

            ready.sort(Comparator.comparingInt(p -> p.remaining));
            current = ready.remove(0);

            if (current.start == -1) {
                current.start = time;
                current.response = time - current.arrival;
            }

            if (!result.isEmpty() && result.get(result.size() - 1).pid == current.id) {
                result.get(result.size() - 1).end++;
            } else {
                result.add(new GanttBlock(current.id, time, time + 1));
            }

            current.remaining--;
            if (current.remaining == 0) {
                current.completion = time + 1;
                current.turnaround = current.completion - current.arrival;
                current.waiting = current.turnaround - current.burst;
                current = null;
            }

            time++;
        }

        return result;
    }

    public static List<GanttBlock> runRoundRobin(List<Process> processes, int quantum) {
        List<GanttBlock> result = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        processes.sort(Comparator.comparingInt(p -> p.arrival));

        while (!processes.isEmpty() || !queue.isEmpty()) {
            while (!processes.isEmpty() && processes.get(0).arrival <= time) {
                queue.offer(processes.remove(0));
            }

            if (queue.isEmpty()) {
                time++;
                continue;
            }

            Process p = queue.poll();

            if (p.start == -1) {
                p.start = time;
                p.response = time - p.arrival;
            }

            int execTime = Math.min(quantum, p.remaining);
            result.add(new GanttBlock(p.id, time, time + execTime));
            time += execTime;
            p.remaining -= execTime;

            while (!processes.isEmpty() && processes.get(0).arrival <= time) {
                queue.offer(processes.remove(0));
            }

            if (p.remaining > 0) {
                queue.offer(p);
            } else {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
            }
        }

        return result;
    }

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

            if (p.start == -1) {
                p.start = time;
                p.response = time - p.arrival;
            }

            int exec = Math.min(quantums[qIdx], p.remaining);
            blocks.add(new GanttBlock(p.id, time, time + exec));
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

    public static Map<String, Double> calculateAverages(List<Process> processes) {
        double totalWT = 0, totalTAT = 0, totalRT = 0;

        for (Process p : processes) {
            totalWT += p.waiting;
            totalTAT += p.turnaround;
            totalRT += p.response;
        }

        Map<String, Double> avg = new HashMap<>();
        avg.put("avgWaiting", totalWT / processes.size());
        avg.put("avgTurnaround", totalTAT / processes.size());
        avg.put("avgResponse", totalRT / processes.size());

        return avg;
    }
}