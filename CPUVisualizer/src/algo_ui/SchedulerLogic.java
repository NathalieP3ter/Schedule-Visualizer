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
        Set<Process> processed = new HashSet<>();
        int time = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrival));

        while (processed.size() < processes.size()) {
            for (Process p : processes) {
                if (p.arrival <= time && !queue.contains(p) && !processed.contains(p)) {
                    queue.add(p);
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
            p.completion = time + p.burst;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            result.add(new GanttBlock(p.id, p.start, p.completion));

            processed.add(p);
            time = p.completion;
        }

        return result;
    }

    public static List<GanttBlock> runSRTF(List<Process> processes) {
        List<GanttBlock> result = new ArrayList<>();
        List<Process> ready = new ArrayList<>();
        int time = 0;
        Process current = null;

        processes.sort(Comparator.comparingInt(p -> p.arrival));

        while (true) {
            for (Process p : processes) {
                if (p.arrival == time && !ready.contains(p) && p.remaining > 0) {
                    ready.add(p);
                }
            }

            if (current != null && current.remaining > 0) {
                ready.add(current);
            }

            if (ready.isEmpty()) {
                if (current == null && time > getMaxArrival(processes)) break;
                time++;
                current = null;
                continue;
            }

            ready.sort(Comparator.comparingInt(p -> p.remaining));
            current = ready.remove(0);

            if (current.start == -1) {
                current.start = time;
                current.response = time - current.arrival;
            }

            result.add(new GanttBlock(current.id, time, time + 1));

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

    private static int getMaxArrival(List<Process> processes) {
        int max = 0;
        for (Process p : processes) {
            max = Math.max(max, p.arrival);
        }
        return max;
    }

    public static List<GanttBlock> runRoundRobin(List<Process> processes, int quantum) {
        List<GanttBlock> result = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        Set<Process> queued = new HashSet<>();
        int time = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrival));

        while (true) {
            for (Process p : processes) {
                if (p.arrival <= time && !queued.contains(p) && p.remaining > 0) {
                    queue.offer(p);
                    queued.add(p);
                }
            }

            if (queue.isEmpty()) {
                if (time > getMaxArrival(processes)) break;
                time++;
                continue;
            }

            Process p = queue.poll();

            if (p.start == -1) {
                p.start = time;
                p.response = time - p.arrival;
            }

            int exec = Math.min(quantum, p.remaining);
            result.add(new GanttBlock(p.id, time, time + exec));
            time += exec;
            p.remaining -= exec;

            for (Process q : processes) {
                if (q.arrival <= time && !queued.contains(q) && q.remaining > 0) {
                    queue.offer(q);
                    queued.add(q);
                }
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





