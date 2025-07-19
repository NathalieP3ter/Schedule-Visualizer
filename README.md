Project overview:
This Java-based simulation visualizes how different CPU scheduling algorithms handle process execution. It allows users to input or randomly generate processes, select algorithms, and view scheduling through a Gantt Chart and performance metrics. The tool is designed for learning, demonstration, and interactive testing of various scheduling techniques.

How to Run a Simulation:
1. Install & Open:
Requires Java (JDK 8+)
Run Main.java to start the GUI interface

2 javac CPUVisualizer/src/Main.java java CPUVisualizer.src.Main Using the GUI:
On launch, you’ll see a control panel (left) and output panel (right)
Select algorithm from the dropdown (FCFS, SJF, SRTF, RR, or MLFQ)
Input processes manually or generate them randomly
Adjust time quantum (for RR and MLFQ)
Hit ▶️ Simulate to run the visualization
Optionally enable step-by-step animation and speed slider to control visual pace
View Gantt Chart, per-process metrics, and average stats

Algorithms Implemented
FCFS (First-Come, First-Served): Runs processes in arrival order.
SJF (Shortest Job First): Non-preemptive, picks the shortest burst among ready processes.
SRTF (Shortest Remaining Time First): Preemptive version of SJF, checks every unit for the shortest remaining.
Round Robin: Equal time slices (quantum) for all ready processes; preemptive and fair.
MLFQ (Multi-Level Feedback Queue): Dynamic queue levels, process priority lowers with execution; supports multiple quantum levels.

Screenshots: 



<img width="1919" height="1019" alt="Simulation Scheduling Visualozer" src="https://github.com/user-attachments/assets/7f042c66-9036-499d-a635-555c130219e3" />
<img width="756" height="310" alt="Metrics Table" src="https://github.com/user-attachments/assets/f4674da3-f740-49dd-b6ac-b2703e935f4e" />
<img width="1502" height="92" alt="Average Calculation" src="https://github.com/user-attachments/assets/e33bfc87-a149-4eaf-b6ae-7902a8f32e86" />
<img width="1919" height="1018" alt="FCFS_Result" src="https://github.com/user-attachments/assets/e888ceaf-aa07-4a75-aa25-0f98ae563212" />
<img width="1919" height="1024" alt="SJF Result" src="https://github.com/user-attachments/assets/e5b60199-13c8-488e-83fd-507f960e0aeb" />
<img width="1919" height="1016" alt="SRTF_Result" src="https
<img width="1916" height="990" alt="MLFQ_Result" src="https://github.com/user-attachments/assets/bfe4ca99-6458-42ef-8878-76baeb1e58a4" />
://github.com/user-attachments/assets/5bae5e83-8cb7-4768-8fda-e59ff71bafc5" />
<img width="1919" height="1018" alt="RR_Result" src="https://github.com/user-attachments/assets/16e6a2dd-2080-469b-b021-e9f3cb1cbf67" />


<img width="1907" height="1015" alt="FCFS Input and Expected Output" src="https://github.com/user-attachments/assets/fade0308-8091-40ea-85df-27b1f00e41c2" />

Known Bugs or limitations:

- No status/Progress bar for each of the Processes
-Generate Random does not mix different File Type but instead relies on what was chosen in the Extension File Drop Down Box
-Step Delay/Simulation Speed does not indicate/show the number of the Speed

Members: 
Nathalie Faye L Peter
Banesha Kaur

Roles and Contributions:

Nathalie Peter 
Created Repository
Initial Start of the code
Partially Working Generate Random Button
Gantt Chart Animation and Color 
Risizing of the Control Buttons 
UI Designer 

Banesha Kaur
Drop-down Box of the Extension Files
Code for the Input Table and the Metrics Table
UI Designer 
Scrollbar in Gantt Chart Panel
Buttons: Add Process, Reset, Simulate
Sliding Step Delay/Speed Simulation 

Enhancement: Could have designed the UI better, all buttons to function properly, Additional Status bar for all Processes input by user. 
Bonus: Putting Ready Queue (Order of the Incoming Processes)

